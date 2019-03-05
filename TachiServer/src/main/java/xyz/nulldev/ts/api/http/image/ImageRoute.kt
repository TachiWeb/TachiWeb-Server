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

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.ui.reader.loader.ChapterLoader
import eu.kanade.tachiyomi.ui.reader.model.ReaderChapter
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.mime.MimeTypes
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.utils.IOUtils
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.enableCache
import xyz.nulldev.ts.ext.kInstanceLazy

// TODO Alternative HttpPageLoader
class ImageRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    private val logger = LoggerFactory.getLogger(javaClass)

    private val detector = MimeTypes()

    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val chapterId = request.params(":chapterId")?.toLong()
                ?: return error("ChapterID must be specified!")
        var page = request.params(":page")?.toInt()

        if (page == null || page < 0) {
            page = 0
        }

        val manga = db.getManga(mangaId).executeAsBlocking()
                ?: return error("The specified manga does not exist!")
        val source: CatalogueSource?
        try {
            val tmpSrc = sourceManager.get(manga.source) as? CatalogueSource
                    ?: throw IllegalArgumentException("Source is not a catalogue source!")
            source = tmpSrc
        } catch (e: Exception) {
            logger.warn("Error loading source: ${manga.source}!", e)
            return error("This manga's source is not loaded!")
        }

        val chapter = db.getChapter(chapterId).executeAsBlocking()
                ?: return error("The specified chapter does not exist!")
        val readerChapter = ReaderChapter(chapter).apply { ref() }
        try {
            ChapterLoader(downloadManager, manga, source).loadChapter(readerChapter).await()
            if (readerChapter.state is ReaderChapter.State.Error) {
                logger.error("Failed to load chapter!")
                return error("Failed to load chapter!")
            }
            val pageObj = readerChapter.pages!!.firstOrNull { it.index == page }
                    ?: return error("Could not find specified page!")
            val pageStatus = readerChapter.pageLoader!!.getPage(pageObj).toBlocking().toIterable()
            for (status in pageStatus) {
                if (status == Page.READY) break
                else if (status == Page.ERROR) {
                    logger.error("Failed to download page!")
                    return error("Failed to download page!")
                }
            }

            response.raw().outputStream.use { outputStream ->
                response.status(200)
                response.enableCache()
                pageObj.stream!!().buffered().use { inputStream ->
                    // Detect content type
                    inputStream.mark(detector.minLength)
                    response.type(detector.detect(inputStream, Metadata().apply {
                        if (!pageObj.imageUrl.isNullOrBlank()) this[TikaCoreProperties.IDENTIFIER] = pageObj.imageUrl
                    }).toString())
                    inputStream.reset()

                    IOUtils.copy(inputStream, outputStream)
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to download page!", e)
            return error("Failed to download page!")
        } finally {
            readerChapter.unref()
        }

        return ""
    }
}