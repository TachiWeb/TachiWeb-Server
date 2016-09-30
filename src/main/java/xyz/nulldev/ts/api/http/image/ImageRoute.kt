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

import eu.kanade.tachiyomi.data.source.Source
import eu.kanade.tachiyomi.data.source.model.Page
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.utils.IOUtils
import xyz.nulldev.ts.DIReplacement
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.util.ChapterUtils
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class ImageRoute : TachiWebRoute() {

    private val downloadManager = DIReplacement.get().injectDownloadManager()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
        val chapterId = request.params(":chapterId")?.toLong()
        var page = request.params(":page")?.toInt()
        if (mangaId == null) {
            return error("MangaID must be specified!")
        } else if (chapterId == null) {
            return error("ChapterID must be specified!")
        }
        if (page == null || page < 0) {
            page = 0
        }
        val manga = library.getManga(mangaId)
                ?: return error("The specified manga does not exist!")
        val source: Source?
        try {
            source = DIReplacement.get().injectSourceManager().get(manga.source)
            if (source == null) {
                throw IllegalArgumentException()
            }
        } catch (e: Exception) {
            return error("This manga's source is not loaded!")
        }

        val chapter = library.getChapter(chapterId)
                ?: return error("The specified chapter does not exist!")
        val pages = ChapterUtils.getPageList(downloadManager, source, manga, chapter)
                ?: return error("Failed to fetch page list!")
        var pageObj: Page? = null
        for (toCheck in pages) {
            if (toCheck.pageNumber == page) {
                pageObj = toCheck
                break
            }
        }
        if (pageObj == null) {
            return error("Could not find specified page!")
        }
        //Get downloaded image if downloaded
        if (downloadManager.isChapterDownloaded(source, manga, chapter)) {
            val downloadDir = downloadManager.getAbsoluteChapterDirectory(source, manga, chapter)
            pageObj = downloadManager.getDownloadedImage(pageObj, downloadDir).toBlocking().first()
        }
        //Download image if not downloaded
        if (pageObj!!.status != Page.READY) {
            pageObj = source.fetchImage(pageObj).toBlocking().first()
        }
        try {
            response.raw().outputStream.use { outputStream ->
                if (pageObj!!.status == Page.READY && pageObj!!.imagePath != null) {
                    response.status(200)
                    response.type(Files.probeContentType(Paths.get(pageObj!!.imagePath)))
                    IOUtils.copy(
                            FileInputStream(pageObj!!.imagePath!!),
                            outputStream)
                } else {
                    throw IllegalStateException()
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to download page!", e)
            return error("Failed to download page!")
        }

        return ""
    }
}