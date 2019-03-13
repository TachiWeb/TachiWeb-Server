/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.api.http.image

import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.online.HttpSource
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.enableCache
import xyz.nulldev.ts.ext.kInstanceLazy
import xyz.nulldev.ts.library.LibraryUpdater
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files

/**
 * Get a manga thumbnail.
 */
class CoverRoute : TachiWebRoute() {

    private val sourceManager: SourceManager by kInstanceLazy()
    private val coverCache: CoverCache by kInstanceLazy()
    private val libraryUpdater: LibraryUpdater by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any? {
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val manga = db.getManga(mangaId).executeAsBlocking()
                ?: return error("The specified manga does not exist!")
        val source: Source?
        try {
            source = sourceManager.get(manga.source)
            if (source == null) {
                throw IllegalArgumentException()
            }
        } catch (e: Exception) {
            return error("This manga's source is not loaded!")
        }

        var url = manga.thumbnail_url
        try {
            if (url.isNullOrEmpty()) {
                libraryUpdater._silentUpdateMangaInfo(manga)
                url = manga.thumbnail_url
            }
        } catch (e: Exception) {
            logger.info("Failed to update manga (no thumbnail)!")
        }

        if (url.isNullOrEmpty()) {
            response.redirect("/img/no-cover.png", 302)
            return null
        }
        val cacheFile = coverCache.getCoverFile(url!!)
        val parentFile = cacheFile.parentFile
        //Make cache dirs
        parentFile.mkdirs()
        //Download image if it does not exist
        if (!cacheFile.exists()) {
            if (source !is HttpSource) {
                response.redirect("/img/no-cover.png", 302)
                return null
            }
            try {
                FileOutputStream(cacheFile).use { outputStream ->
                    val httpResponse = source.client.newCall(
                            okhttp3.Request.Builder().headers(source.headers).url(url).build()).execute()
                    httpResponse.use {
                        val stream = httpResponse!!.body().byteStream()
                        stream.use {
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            while (true) {
                                val n = stream.read(buffer)
                                if(n == -1) {
                                    break
                                }
                                outputStream.write(buffer, 0, n)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to download cover image!", e)
                return error("Failed to download cover image!")
            }
        }
        //Send cached image
        response.enableCache()
        response.type(Files.probeContentType(cacheFile.toPath()))
        try {
            FileInputStream(cacheFile).use { stream ->
                response.raw().outputStream.use { os ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val n = stream.read(buffer)
                        if(n == -1) {
                            break
                        }
                        os.write(buffer, 0, n)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error sending cached cover!", e)
            return error("Error sending cached cover!")
        }

        return ""
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1024 * 4
    }
}
