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

package xyz.nulldev.ts.api.http.catalogue

import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.online.LoginSource
import org.json.JSONArray
import org.json.JSONObject
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * List the available sources.
 */
class ListSourcesRoute : TachiWebRoute() {

    private val sourceManager: SourceManager by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any {
        val sources = sourceManager.getCatalogueSources()
        val rootObject = success()
        val contentArray = JSONArray()
        for (source in sources) {
            val sourceObj = JSONObject()
            sourceObj.put(KEY_ID, source.id)
            sourceObj.put(KEY_NAME, source.name)
            if (source is LoginSource) {
                sourceObj.put(KEY_LOGGED_IN, source.isLogged())
            }
            contentArray.put(sourceObj)
        }
        rootObject.put(KEY_CONTENT, contentArray)
        return rootObject
    }

    companion object {
        val KEY_CONTENT = "content"
        val KEY_NAME = "name"
        val KEY_ID = "id"
        val KEY_LOGGED_IN = "logged_in"
    }
}
