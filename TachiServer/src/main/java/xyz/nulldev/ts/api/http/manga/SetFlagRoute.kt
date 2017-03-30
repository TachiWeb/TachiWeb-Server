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
class SetFlagRoute : TachiWebRoute() {

    private val db: DatabaseHelper by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val manga = db.getManga(mangaId).executeAsBlocking()
                ?: return error("The specified manga does not exist!")
        val flag: MangaFlag
        try {
            flag = MangaFlag.valueOf((request.params(":flag") ?: "").toUpperCase())
        } catch (e: IllegalArgumentException) {
            return error("Invalid/no flag specified!")
        }

        val flagState = flag.findFlagState((request.params(":state") ?: "").toUpperCase())
                ?: return error("Invalid/no flag state specified!")
        flag.set(manga, flagState)
        db.insertManga(manga).executeAsBlocking()
        return success()
    }
}
