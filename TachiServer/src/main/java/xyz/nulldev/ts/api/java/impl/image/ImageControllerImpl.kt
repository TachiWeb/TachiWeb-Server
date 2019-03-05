package xyz.nulldev.ts.api.java.impl.image

import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.HttpSource
import xyz.nulldev.ts.api.java.model.image.ImageController
import xyz.nulldev.ts.api.java.util.pageList
import xyz.nulldev.ts.api.java.util.sourceObj
import xyz.nulldev.ts.api.java.util.updateInfo
import xyz.nulldev.ts.ext.kInstanceLazy
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files

class ImageControllerImpl : ImageController {
    private val coverCache: CoverCache by kInstanceLazy()

    override fun fetchCover(manga: Manga, stream: OutputStream): String {
        //Find thumbnail URL
        var url = manga.thumbnail_url
        try {
            if (url.isNullOrEmpty()) {
                manga.updateInfo()
                url = manga.thumbnail_url
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to update manga (no thumbnail)!", e)
        }

        if (url.isNullOrEmpty()) {
            throw IllegalStateException("This manga has no thumbnail URL!")
        }

        val source = manga.sourceObj

        //Check cache
        val cacheFile = coverCache.getCoverFile(url!!)
        val parentFile = cacheFile.parentFile
        //Make cache dirs
        parentFile.mkdirs()
        //Download image if it does not exist
        if (!cacheFile.exists()) {
            if (source !is HttpSource) {
                throw IllegalStateException("This manga is not an HttpSource!")
            }
            try {
                //Write downloaded thumbnail to cache file
                FileOutputStream(cacheFile).use { outputStream ->
                    val httpResponse = source.client.newCall(
                            okhttp3.Request.Builder().headers(source.headers).url(url!!).build()).execute()
                    httpResponse.use {
                        val input = httpResponse!!.body().byteStream()
                        input.use {
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            while (true) {
                                val n = input.read(buffer)
                                if(n == -1) {
                                    break
                                }
                                outputStream.write(buffer, 0, n)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                //Delete corrupt cache file
                cacheFile.delete()
                throw IOException("Failed to download cover image!", e)
            }
        }
        //Send cached image
        val mime = Files.probeContentType(cacheFile.toPath())
        try {
            FileInputStream(cacheFile).use { inputStream ->
                stream.use { os ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val n = inputStream.read(buffer)
                        if(n == -1) {
                            break
                        }
                        os.write(buffer, 0, n)
                    }
                }
            }
        } catch (e: Exception) {
            throw IOException("Error writing cached cover to stream!", e)
        }
        return mime
    }

    override fun fetchImage(chapter: Chapter, page: Int, stream: OutputStream): String {
        //Fetch page list and find page in page list
        val pages = chapter.pageList
        val pageObj = requireNotNull(pages.firstOrNull { it.index == page }, {
            "Page $page not found!"
        })

        return fetchImage(chapter, pageObj, stream)
    }

    override fun fetchImage(chapter: Chapter, page: Page, stream: OutputStream): String {
        throw NotImplementedError("Deprecated, use v3 API instead!")

        /*val manga = chapter.manga.ensureInDatabase()
        val source = manga.sourceObj.ensureLoaded()

        //TODO Accept offline sources
        if(source !is HttpSource) {
            throw IllegalArgumentException("This source is currently unsupported!")
        }

        //Download image if not downloaded
        val newPage = if (page.status != Page.READY) {
            source.fetchImageFromCacheThenNet(page).toBlocking().first()
        } else page

        //Write image to stream
        try {
            if (newPage!!.status == Page.READY && newPage.uri != null) {
                stream.use { outputStream ->
                    FileInputStream(newPage.uri!!.file()).copyTo(outputStream)
                }
                return Files.probeContentType(Paths.get(newPage.uri!!.java()))
            } else {
                throw IllegalStateException("Page not downloaded!")
            }
        } catch (e: Exception) {
            throw IOException("Failed to download page!", e)
        }*/
    }
}