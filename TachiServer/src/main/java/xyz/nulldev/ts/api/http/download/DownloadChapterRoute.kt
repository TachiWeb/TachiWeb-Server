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

import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Download a chapter
 */
class DownloadChapterRoute : TachiWebRoute() {

    override fun handleReq(request: Request, response: Response): Any {
        //Get params
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val chapterId = request.params(":chapterId")?.toLong()
                ?: return error("ChapterID must be specified!")

        //Resolve objects and parse params
        val manga = api.database.getManga(mangaId).executeAsBlocking()
                ?: return error("The specified manga does not exist!")
        val source = api.catalogue.getSource(manga.source)
                ?: throw IllegalArgumentException()
        val chapter = api.database.getChapter(chapterId).executeAsBlocking()
                ?: return error("The specified chapter does not exist!")
        val delete = "true".equals(request.queryParams("delete"), ignoreCase = true)

        //Check for active download
        //TODO Handle other download statuses
        val activeDownload = api.downloads.downloads.find { it.chapter.id == chapter.id }
        if (activeDownload != null) {
            if (delete) {
                return error("This chapter is currently being downloaded!")
            } else {
                return error("This chapter is already being downloaded!")
            }
        }

        //Check if chapter is downloaded
        val isChapterDownloaded = api.downloads.isDownloaded(chapter)
        if (!delete && isChapterDownloaded) {
            return error("This chapter is already downloaded!")
        }
        if (delete && !isChapterDownloaded) {
            return error("This chapter is not downloaded!")
        }

        if (delete) {
            //This is a delete request! Delete the chapter!
            api.downloads.delete(chapter)
        } else {
            //Download the chapter
            api.downloads.add(chapter)
        }
        return success()
    }
}
