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

package xyz.nulldev.ts.api.http.settings

import eu.kanade.tachiyomi.data.source.SourceManager
import eu.kanade.tachiyomi.data.source.online.LoginSource
import org.json.JSONArray
import org.json.JSONObject
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class ListLoginSourceRoute : TachiWebRoute() {

    val sourceManager: SourceManager = Injekt.get()

    override fun handleReq(request: Request, response: Response): Any {
        val builtResponse = success()
        val content = JSONArray()
        for (source in sourceManager.getOnlineSources()
                .filter {
                    normalSource -> normalSource is LoginSource
                }
                .map {
                    loginSource -> loginSource
                }) {
            val sourceJson = JSONObject()
            sourceJson.put(KEY_NAME, source.name)
            sourceJson.put(KEY_ID, source.id)
            sourceJson.put(KEY_LOGGED_IN, (source as LoginSource).isLogged())
            content.put(sourceJson)
        }
        builtResponse.put(KEY_CONTENT, content)
        return builtResponse
    }

    companion object {
        val KEY_CONTENT = "content"
        val KEY_NAME = "name"
        val KEY_ID = "id"
        val KEY_LOGGED_IN = "logged_in"
    }
}
