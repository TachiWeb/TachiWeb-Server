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
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class ReadingStatusRoute : TachiWebRoute() {

    private val db: DatabaseHelper by kInstanceLazy()

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
        val chapter = db.getChapter(chapterId).executeAsBlocking()
                ?: return error("The specified chapter does not exist!")
        val lastPage = request.queryParams("lp")
        val read = request.queryParams("read")
        //Keep changes atomic
        var page = -1
        var readB = false
        if (lastPage != null) {
            try {
                page = lastPage.toInt()
            } catch (e: NumberFormatException) {
                return error("Last page is not a number!")
            }

        }
        if (read != null) {
            if (read.equals("true", ignoreCase = true)) {
                readB = true
            } else if (read.equals("false", ignoreCase = true)) {
                readB = false
            } else {
                return error("Read is not a boolean!")
            }
        }
        if (lastPage != null) {
            chapter.last_page_read = page
        }
        if (read != null) {
            chapter.read = readB
        }
        db.insertChapter(chapter).executeAsBlocking()
        return success()
    }
}
