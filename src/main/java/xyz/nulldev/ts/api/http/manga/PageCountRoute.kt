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

import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.source.Source
import eu.kanade.tachiyomi.data.source.SourceManager
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.util.ChapterUtils

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class PageCountRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager = Injekt.get()
    private val sourceManager: SourceManager = Injekt.get()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
        val chapterId = request.params(":chapterId")?.toLong()
        if (mangaId == null) {
            return error("MangaID must be specified!")
        } else if (chapterId == null) {
            return error("ChapterID must be specified!")
        }
        val manga = library.getManga(mangaId)
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

        val chapter = library.getChapter(chapterId)
                ?: return error("The specified chapter does not exist!")
        val pages = ChapterUtils.getPageList(downloadManager, source, manga, chapter)
                ?: return error("Failed to fetch page list!")
        return success().put(KEY_PAGE_COUNT, pages.size)
    }

    companion object {
        private val KEY_PAGE_COUNT = "page_count"
    }
}