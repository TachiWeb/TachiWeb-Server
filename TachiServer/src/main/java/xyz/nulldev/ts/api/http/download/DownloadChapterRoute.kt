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

package xyz.nulldev.ts.api.http.download

import android.content.Context
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.DownloadService
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import spark.Request
import spark.Response
import uy.kohesive.injekt.injectLazy
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.getDownload
import xyz.nulldev.ts.ext.isDownloaded
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Download a chapter
 */
class DownloadChapterRoute : TachiWebRoute() {

    private val sourceManager: SourceManager by kInstanceLazy()
    private val downloadManager: DownloadManager by kInstanceLazy()
    private val context : Context by injectLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any {
        //Get params
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val chapterId = request.params(":chapterId")?.toLong()
                ?: return error("ChapterID must be specified!")

        //Resolve objects and parse params
        val manga = db.getManga(mangaId).executeAsBlocking()
                ?: return error("The specified manga does not exist!")
        val source = sourceManager.get(manga.source)
                ?: throw IllegalArgumentException()
        val chapter = db.getChapter(chapterId).executeAsBlocking()
                ?: return error("The specified chapter does not exist!")
        val delete = "true".equals(request.queryParams("delete"), ignoreCase = true)

        //Check for active download
        //TODO Handle other download statuses
        val activeDownload = downloadManager.getDownload(chapter)
        if (activeDownload != null) {
            if (delete) {
                return error("This chapter is currently being downloaded!")
            } else {
                return error("This chapter is already being downloaded!")
            }
        }

        //Check if chapter is downloaded
        val isChapterDownloaded = chapter.isDownloaded(source, manga)
        if (!delete && isChapterDownloaded) {
            return error("This chapter is already downloaded!")
        }
        if (delete && !isChapterDownloaded) {
            return error("This chapter is not downloaded!")
        }

        if (delete) {
            //This is a delete request! Delete the chapter!
            deleteChapter(source, manga, chapter)
        } else {
            //Download the chapter
            downloadChapters(manga, listOf(chapter))
        }
        return success()
    }

    private fun deleteChapter(source: Source, manga: Manga, chapter: Chapter) {
        downloadManager.queue.remove(chapter)
        downloadManager.deleteChapter(source, manga, chapter)
    }

    private fun downloadChapters(manga: Manga, chapters: List<Chapter>) {
        DownloadService.start(context)
        downloadManager.downloadChapters(manga, chapters)
    }
}
