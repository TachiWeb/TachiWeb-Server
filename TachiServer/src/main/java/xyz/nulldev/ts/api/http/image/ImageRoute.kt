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
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.source.online.fetchImageFromCacheThenNet
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.utils.IOUtils
import xyz.nulldev.androidcompat.util.file
import xyz.nulldev.androidcompat.util.java
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.api.java.util.isDownloaded
import xyz.nulldev.ts.api.java.util.pageList
import xyz.nulldev.ts.ext.enableCache
import xyz.nulldev.ts.ext.kInstanceLazy
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class ImageRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    private val logger = LoggerFactory.getLogger(javaClass)

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
        //TODO Error handling
        val pages = chapter.pageList
        var pageObj: Page? = pages.firstOrNull { it.index == page } ?: return error("Could not find specified page!")

        //Get downloaded image if downloaded
        if (chapter.isDownloaded) {
            pageObj = downloadManager.buildPageList(source, manga, chapter)
                    .toBlocking()
                    .first().first { it.index == page }
        }
        //TODO Accept offline sources
        if(source !is HttpSource) {
            return error("This source is currently unsupported!")
        }
        //Download image if not downloaded
        if (pageObj!!.status != Page.READY) {
            pageObj = source.fetchImageFromCacheThenNet(pageObj).toBlocking().first()
        }
        try {
            response.raw().outputStream.use { outputStream ->
                if (pageObj!!.status == Page.READY && pageObj.uri != null) {
                    response.status(200)
                    response.enableCache()
                    response.type(Files.probeContentType(Paths.get(pageObj.uri!!.java())))
                    IOUtils.copy(
                            FileInputStream(pageObj.uri!!.file()),
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