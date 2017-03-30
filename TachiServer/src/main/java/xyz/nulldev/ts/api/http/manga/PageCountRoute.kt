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

package xyz.nulldev.ts.api.http.manga

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.getPageList
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class PageCountRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
        val chapterId = request.params(":chapterId")?.toLong()
        if (mangaId == null) {
            return error("MangaID must be specified!")
        } else if (chapterId == null) {
            return error("ChapterID must be specified!")
        }
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

        val chapter = db.getChapter(chapterId).executeAsBlocking()
                ?: return error("The specified chapter does not exist!")
        //Probably should handle exception here instead
        val pages = chapter.getPageList(source, manga)
                ?: return error("Failed to fetch page list!")
        return success().put(KEY_PAGE_COUNT, pages.size)
    }

    companion object {
        private val KEY_PAGE_COUNT = "page_count"
    }
}