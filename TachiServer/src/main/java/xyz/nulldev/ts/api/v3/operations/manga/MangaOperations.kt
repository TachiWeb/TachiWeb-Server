package xyz.nulldev.ts.api.v3.operations.manga

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.OpenOptions
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpVersion
import io.vertx.core.streams.Pump
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.core.file.closeAwait
import io.vertx.kotlin.core.file.openAwait
import io.vertx.kotlin.core.file.propsAwait
import io.vertx.kotlin.core.http.httpClientOptionsOf
import mu.KotlinLogging
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.mime.MimeTypes
import xyz.nulldev.ts.api.v3.*
import xyz.nulldev.ts.api.v3.models.WSortDirection
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes.*
import xyz.nulldev.ts.api.v3.models.manga.*
import xyz.nulldev.ts.api.v3.util.*
import xyz.nulldev.ts.cache.AsyncDiskLFUCache
import xyz.nulldev.ts.ext.kInstanceLazy
import xyz.nulldev.ts.library.LibraryUpdater
import java.net.URL
import java.nio.charset.StandardCharsets

private const val MANGA_ID_PARAM = "mangaId"

class MangaOperations(private val vertx: Vertx) : OperationGroup {
    private val logger = KotlinLogging.logger { }

    private val db: DatabaseHelper by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()
    private val libraryUpdater: LibraryUpdater by kInstanceLazy()
    private val coverCache: AsyncDiskLFUCache by kInstanceLazy()
    private val detector: MimeTypes by kInstanceLazy()
    private val downloadManager: DownloadManager by kInstanceLazy()

    private val httpClient by lazy {
        vertx.createHttpClient(httpClientOptionsOf(
                tryUseCompression = true,
                // TODO Use HTTP 2, some sources break with HTTP 2 though :(
//            protocolVersion = HttpVersion.HTTP_2,
//            alpnVersions = HttpVersion.values().toList(),
//            useAlpn = true
                protocolVersion = HttpVersion.HTTP_1_1
        ))
    }

    override fun register(routerFactory: OpenAPI3RouterFactory) {
        routerFactory.op(::getMangas.name, ::getMangas)
        routerFactory.opWithContext(::getLibraryMangas.name, ::getLibraryMangas)
        routerFactory.opWithParamsAndContext(::getMangaCover.name, MANGA_ID_PARAM, ::getMangaCover)
        routerFactory.opWithParams(::getMangaFlags.name, MANGA_ID_PARAM, ::getMangaFlags)
        routerFactory.opWithParams(::setMangaFlags.name, MANGA_ID_PARAM, ::setMangaFlags)
    }

    suspend fun getMangas(): List<WManga> {
        return db.getMangas().await().map { it.asWeb() }
    }

    suspend fun getLibraryMangas(rc: RoutingContext): List<WLibraryManga> {
        val mangas = db.getLibraryMangas().await()
        val includeDownloadInfo = rc.queryParamObjs<Boolean>("include-download-info").firstOrNull() ?: false

        return mangas.groupBy {
            it.id
        }.map { (_, mangasWithDifferentCategories) ->
            val referenceManga = mangasWithDifferentCategories.first()
            WLibraryManga(
                    referenceManga.asWeb(mangasWithDifferentCategories.map { it.category }),
                    if (includeDownloadInfo)
                        downloadManager.getDownloadCount(referenceManga)
                    else null,
                    referenceManga.unread
            )
        }
    }

    suspend fun getMangaFlags(mangaId: Long): WMangaFlags {
        val manga = db.getManga(mangaId).await() ?: notFound(NO_MANGA)

        return WMangaFlags(
                WMangaBookmarkedFilter.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaDisplayMode.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaDownloadedFilter.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaReadFilter.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WSortDirection.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaSortType.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE)
        )
    }

    suspend fun setMangaFlags(mangaId: Long, flags: WMangaFlags) {
        val manga = db.getManga(mangaId).await() ?: notFound(NO_MANGA)

        val newFlags: List<WMangaFlag> = listOf(
                flags.bookmarkedFilter,
                flags.displayMode,
                flags.downloadedFilter,
                flags.readFilter,
                flags.sortDirection,
                flags.sortType
        )

        newFlags.forEach {
            manga.setFlag(it)
        }
    }

    suspend fun getMangaCover(mangaId: Long, rc: RoutingContext) {
        val manga = db.getManga(mangaId).await() ?: notFound(NO_MANGA)
        val source = sourceManager.get(manga.source) ?: expectedError(
                500,
                "Cannot find source for manga: $mangaId",
                NO_SOURCE
        )

        // Update manga if not updated
        var url = manga.thumbnail_url
        if (url == null && !manga.initialized) {
            try {
                libraryUpdater.updateMangaInfo(manga, source)
                url = manga.thumbnail_url
            } catch (e: Exception) {
                expectedError(500, MANGA_INFO_UPDATE_FAILED, e)
            }
        }

        if (url == null) notFound(NO_COVER)

        val fs = rc.vertx().fileSystem()

        // Try cache
        val cachedCover = coverCache.get(url)
        if (cachedCover != null) {
            // Cover in cache!
            cachedCover.use {
                val path = it.absolutePath
                val pathProps = fs.propsAwait(path) // Get size of file
                val asyncFile = fs.openAwait(path, OpenOptions())
                try {
                    rc.response().putHeader(HttpHeaders.CONTENT_LENGTH, pathProps.size().toString())
                    serveCoverResponse(url, asyncFile, rc.response())
                } finally {
                    asyncFile.closeAwait()
                }
            }
        } else {
            logger.debug { "Fetching manga cover @: $url" }
            // Build HTTP request for cover
            val request = httpClient.get(URL(url).asRequestOptions())
            if (source is HttpSource) {
                source.headers.toMultimap().forEach { t, u ->
                    request.putHeader(t, u)
                }
            }
            request.setFollowRedirects(true)

            // Prepare cache entry
            val cacheEntry = coverCache.put(url)
            val asyncFile = fs.openAwait(cacheEntry.file.absolutePath, OpenOptions())

            try {
                // NOTE: NO SUSPEND CALLS can take place between awaitResponse and the call that reads the body of the response
                //       This is because, otherwise, the handler will not be installed in time. Alternatively, pause the response
                val coverResponse = request.awaitResponse()
                if (coverResponse.statusCode() != 200) {
                    //       Alternatively, just pause the stream
                    val body = coverResponse.awaitBody()
                    logger.warn {
                        "Cover download returned bad status code: ${coverResponse.statusCode()}, body follows:\n${body?.bytes?.toString(StandardCharsets.UTF_8)}"
                    }
                    expectedError(500, "Invalid status code from $url: ${coverResponse.statusCode()}!", COVER_DOWNLOAD_ERROR)
                }

                val coverResponseLength = coverResponse.getHeader(HttpHeaders.CONTENT_LENGTH)
                val coverResponseLengthLong = coverResponseLength?.toLongOrNull()
                        ?: expectedError(500, "Response from $url did not include valid Content-Length ($coverResponseLength)!", COVER_DOWNLOAD_ERROR)
                rc.response().putHeader(HttpHeaders.CONTENT_LENGTH, coverResponseLength)
                val written = serveCoverResponse(url, coverResponse, combineWriteStreams(rc.response(), asyncFile))

                if (written == coverResponseLengthLong) {
                    cacheEntry.commit()
                } else {
                    logger.warn { "Length of cover response body ($written) from $url did not match Content-Length ($coverResponseLength), not caching this cover!" }
                    cacheEntry.cancel()
                }
            } catch (e: Exception) {
                cacheEntry.cancel()
                throw e
            } finally {
                asyncFile.closeAwait()
            }
        }
        rc.response().tryEnd()
    }

    private suspend fun serveCoverResponse(url: String, input: ReadStream<Buffer>, response: WriteStream<Buffer>): Long {
        // Read in some data for the mime type
        val (stream, buffer) = input.readBytes(detector.minLength)
        detector.detect(buffer.inputStream(), Metadata().apply {
            this[TikaCoreProperties.IDENTIFIER] = url
        })
        response.write(Buffer.buffer(buffer))

        var totalBytesWritten = buffer.size.toLong()

        val pump = Pump.pump(stream, response.watched {
            totalBytesWritten += it.length()
        })
        pump.start()
        stream.resumeAndAwaitEnd()

        return totalBytesWritten
    }

    fun Manga.flagsAsWeb() = WMangaFlags(
            WMangaBookmarkedFilter.values().firstForManga(this) ?: internalError(UNKNOWN_FLAG_VALUE),
            WMangaDisplayMode.values().firstForManga(this) ?: internalError(UNKNOWN_FLAG_VALUE),
            WMangaDownloadedFilter.values().firstForManga(this) ?: internalError(UNKNOWN_FLAG_VALUE),
            WMangaReadFilter.values().firstForManga(this) ?: internalError(UNKNOWN_FLAG_VALUE),
            WSortDirection.values().firstForManga(this) ?: internalError(UNKNOWN_FLAG_VALUE),
            WMangaSortType.values().firstForManga(this) ?: internalError(UNKNOWN_FLAG_VALUE)
    )

    fun Manga.statusAsWeb() = when (status) {
        SManga.UNKNOWN -> WMangaStatus.UNKNOWN
        SManga.ONGOING -> WMangaStatus.ONGOING
        SManga.COMPLETED -> WMangaStatus.COMPLETED
        SManga.LICENSED -> WMangaStatus.LICENSED
        else -> internalError(UNKNOWN_MANGA_STATUS)
    }

    fun Manga.viewerAsWeb() = WMangaViewer.values().getOrNull(viewer) ?: internalError(UNKNOWN_MANGA_VIEWER)

    suspend fun Manga.asWeb(categories: List<Int>? = null): WManga {
        val loadedCategories = (categories ?: db.getCategoriesForManga(this).await().map {
            it.id
        }).filter { it != 0 } // Filter out default category

        return WManga(
                artist,
                author,
                loadedCategories.map { it!!.toLong() },
                description,
                favorite,
                flagsAsWeb(),
                genre,
                id!!,
                initialized,
                last_update,
                source,
                statusAsWeb(),
                title,
                listOf(), // TODO
                url,
                viewerAsWeb()
        )
    }
}

