package xyz.nulldev.ts.api.v3.operations.manga

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.source.Source
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.mime.MimeTypes
import xyz.nulldev.ts.api.v3.*
import xyz.nulldev.ts.api.v3.models.WSortDirection
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes.*
import xyz.nulldev.ts.api.v3.models.exceptions.WException
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
        routerFactory.opWithParams(::getManga.name, MANGA_ID_PARAM, ::getManga)
        routerFactory.opWithParams(::setMangaFavorited.name, MANGA_ID_PARAM, ::setMangaFavorited)
        routerFactory.opWithParams(::getMangaFlags.name, MANGA_ID_PARAM, ::getMangaFlags)
        routerFactory.opWithParams(::setMangaFlags.name, MANGA_ID_PARAM, ::setMangaFlags)
        routerFactory.opWithParams(::setMangaViewer.name, MANGA_ID_PARAM, ::setMangaViewer)
        routerFactory.opWithParams(::updateMangaInfo.name, MANGA_ID_PARAM, ::updateMangaInfo)
    }

    suspend fun getMangas(): List<WManga> {
        return db.getMangas().await().map { it.asWeb(db, sourceManager) }
    }

    suspend fun getManga(mangaId: Long): WManga {
        return db.getManga(mangaId).await()?.asWeb(db, sourceManager) ?: notFound()
    }

    suspend fun getLibraryMangas(rc: RoutingContext): List<WLibraryManga> = coroutineScope {
        val mangas = db.getLibraryMangas().await()

        val includeLastReadIndex = rc.queryParamObjs<Boolean>("include-last-read-index").firstOrNull() ?: false
        val includeTotalChaptersIndex = rc.queryParamObjs<Boolean>("include-total-chapters-index").firstOrNull()
                ?: false
        val includeTotalDownloaded = rc.queryParamObjs<Boolean>("include-total-downloaded").firstOrNull() ?: false

        val lastReadIndexes = if (includeLastReadIndex) {
            var counter = 0
            db.getLastReadManga().await().associate { it.id!! to counter++ }
        } else null
        val totalChaptersIndexes = if (includeTotalChaptersIndex) {
            var counter = 0
            db.getTotalChapterManga().await().associate { it.id!! to counter++ }
        } else null

        mangas.groupBy {
            it.id
        }.map { (_, mangasWithDifferentCategories) ->
            val referenceManga = mangasWithDifferentCategories.first()
            val totalDownloaded = if (includeTotalDownloaded) {
                withContext(Dispatchers.IO) { downloadManager.getDownloadCount(referenceManga) }
            } else null

            WLibraryManga(
                    lastReadIndexes?.get(referenceManga.id!!),
                    referenceManga.asWeb(db, sourceManager, mangasWithDifferentCategories.map { it.category }),
                    totalChaptersIndexes?.get(referenceManga.id!!),
                    totalDownloaded,
                    referenceManga.unread
            )
        }
    }

    suspend fun setMangaFavorited(mangaId: Long, favorited: Boolean): Boolean {
        val manga = db.getManga(mangaId).await() ?: notFound()

        manga.favorite = favorited

        db.insertManga(manga).await()

        return favorited
    }

    suspend fun getMangaFlags(mangaId: Long): WMangaFlags {
        val manga = db.getManga(mangaId).await() ?: notFound()

        return WMangaFlags(
                WMangaBookmarkedFilter.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaDisplayMode.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaDownloadedFilter.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaReadFilter.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WSortDirection.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE),
                WMangaSortType.values().firstForManga(manga) ?: internalError(UNKNOWN_FLAG_VALUE)
        )
    }

    suspend fun setMangaFlags(mangaId: Long, flags: WMangaFlags): WMangaFlags {
        val manga = db.getManga(mangaId).await() ?: notFound()

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

        db.insertManga(manga).await()

        return flags
    }

    suspend fun setMangaViewer(mangaId: Long, viewer: WMangaViewer): WMangaViewer {
        val manga = db.getManga(mangaId).await() ?: notFound()

        manga.viewer = viewer.ordinal

        db.insertManga(manga).await()

        return viewer
    }

    suspend fun updateMangaInfo(mangaId: Long): WManga {
        val manga = db.getManga(mangaId).await() ?: notFound()
        val source = sourceManager.get(manga.source) ?: expectedError(
                500,
                "Cannot find source for manga: $mangaId",
                NO_SOURCE
        )

        try {
            libraryUpdater.updateMangaInfo(manga, source)
            return manga.asWeb(db, sourceManager, sourceObj = source)
        } catch (t: Throwable) {
            expectedError(500, MANGA_INFO_UPDATE_FAILED, t)
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

        try {
            serveMangaCover(source, url, rc)
        } catch (e: WException) {
            // Retry failed cover downloads after updating the manga
            if (!manga.initialized
                    && (e.data as? WException.DataType.ExpectedError)?.wError?.type == COVER_DOWNLOAD_ERROR.name) {
                val newUrl = try {
                    libraryUpdater.updateMangaInfo(manga, source)
                    manga.thumbnail_url
                } catch (e: Exception) {
                    expectedError(500, MANGA_INFO_UPDATE_FAILED, e)
                }

                if (newUrl != null && newUrl != url) {
                    serveMangaCover(source, newUrl, rc)
                } else throw e // New URL is bad too :(
            } else throw e
        }
    }

    suspend fun serveMangaCover(source: Source, url: String, rc: RoutingContext) {
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
                if (coverResponseLengthLong != null) {
                    rc.response().putHeader(HttpHeaders.CONTENT_LENGTH, coverResponseLength)
                } else {
                    rc.response().isChunked = true
                }
                val written = serveCoverResponse(url, coverResponse, combineWriteStreams(rc.response(), asyncFile))

                if (written == coverResponseLengthLong || (written != 0L && coverResponseLengthLong == null)) {
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

}

fun Manga.flagsAsWeb() = WMangaFlags(
        WMangaBookmarkedFilter.values().firstForManga(this)!!,
        WMangaDisplayMode.values().firstForManga(this)!!,
        WMangaDownloadedFilter.values().firstForManga(this)!!,
        WMangaReadFilter.values().firstForManga(this)!!,
        WSortDirection.values().firstForManga(this)!!,
        WMangaSortType.values().firstForManga(this)!!
)

fun Manga.statusAsWeb() = when (status) {
    SManga.UNKNOWN -> WMangaStatus.UNKNOWN
    SManga.ONGOING -> WMangaStatus.ONGOING
    SManga.COMPLETED -> WMangaStatus.COMPLETED
    SManga.LICENSED -> WMangaStatus.LICENSED
    else -> throw IllegalStateException("Unknown status: $status")
}

fun Manga.viewerAsWeb() = WMangaViewer.values().getOrNull(viewer) ?: error("Invalid viewer id: $viewer")

suspend fun Manga.asWeb(db: DatabaseHelper,
                        sourceManager: SourceManager,
                        categories: List<Int>? = null,
                        sourceObj: Source? = sourceManager.get(source)): WManga {
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
            source.toString(),
            statusAsWeb(),
            title,
            listOf(), // TODO
            (sourceObj as? HttpSource)?.mangaDetailsRequest(this)?.url()?.toString(),
            viewerAsWeb()
    )
}
