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
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import org.json.JSONArray
import org.json.JSONObject
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.getDownload
import xyz.nulldev.ts.ext.isDownloaded
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class ChaptersRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val manga = db.getManga(mangaId).executeAsBlocking()
                ?: return error("The specified manga does not exist!")
        val source = sourceManager.get(manga.source)
        val chapters = db.getChapters(manga).executeAsBlocking().sortedBy(Chapter::chapter_number)
        val array = JSONArray()
        for (chapter in chapters) {
            val jsonChapter = JSONObject()
            jsonChapter.put(KEY_ID, chapter.id)
            jsonChapter.put(KEY_NAME, chapter.name)
            jsonChapter.put(KEY_DATE, chapter.date_upload)
            jsonChapter.put(KEY_READ, chapter.read)
            jsonChapter.put(KEY_LAST_READ, chapter.last_page_read)
            jsonChapter.put(KEY_CHAPTER_NUMBER, chapter.chapter_number.toDouble())
            jsonChapter.put(KEY_SOURCE_ORDER, chapter.source_order)
            if (source != null) {
                jsonChapter.put(KEY_DOWNLOAD_STATUS, getDownloadStatus(source, manga, chapter))
            }
            array.put(jsonChapter)
        }
        return success().put(KEY_CONTENT, array)
    }

    private fun getDownloadStatus(source: Source, manga: Manga, chapter: Chapter): String {
        val isDownloaded = chapter.isDownloaded(source, manga)
        if (isDownloaded) {
            return STATUS_DOWNLOADED
        } else {
            //TODO Handle other download statuses
            return if (downloadManager.getDownload(chapter) != null) STATUS_DOWNLOADING else STATUS_NOT_DOWNLOADED
        }
    }

    companion object {
        val KEY_ID = "id"
        val KEY_NAME = "name"
        val KEY_DATE = "date"
        val KEY_READ = "read"
        val KEY_LAST_READ = "last_page_read"
        val KEY_CHAPTER_NUMBER = "chapter_number"
        val KEY_DOWNLOAD_STATUS = "download_status"
        val KEY_SOURCE_ORDER = "source_order"
        val KEY_CONTENT = "content"

        val STATUS_DOWNLOADED = "DOWNLOADED"
        val STATUS_DOWNLOADING = "DOWNLOADING"
        val STATUS_NOT_DOWNLOADED = "NOT_DOWNLOADED"
    }
}