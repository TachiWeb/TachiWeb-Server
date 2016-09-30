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

import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class FaveRoute : TachiWebRoute() {
    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val manga = library.getManga(mangaId)
                ?: return error("The specified manga does not exist!")
        val fave = request.queryParams("fave")
                ?: return error("Parameter 'fave' was not specified!")
        if (fave.equals("true", ignoreCase = true)) {
            manga.favorite = true
        } else if (fave.equals("false", ignoreCase = true)) {
            manga.favorite = false
        } else {
            return error("Parameter 'fave' is not a valid boolean!")
        }
        return success()
    }
}