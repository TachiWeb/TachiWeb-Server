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

import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.online.LoginSource
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class SourceLoginRoute : TachiWebRoute() {

    private val sourceManager: SourceManager by kInstanceLazy()
    private val preferencesHelper: PreferencesHelper by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any {
        val sourceID = request.params(":sourceId")?.toLong()
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        if (sourceID == null) {
            return error("No source ID/invalid source ID specified!")
        } else if (username == null) {
            return error("Username not specified!")
        } else if (password == null) {
            return error("Password not specified!")
        }
        val source: Source
        try {
            source = sourceManager.get(sourceID)!!
            if (source !is LoginSource) {
                throw IllegalArgumentException()
            }
        } catch (e: Exception) {
            return error("The specified source is not loaded/invalid!")
        }

        val successObj = source.login(username, password).toBlocking().first()
        val success: Boolean
        if (successObj == null) {
            success = false
        } else {
            success = successObj
        }
        //Store login credentials on success
        if (success) {
            preferencesHelper.setSourceCredentials(source, username, password)
        }
        return if (success) success() else error("Username/password incorrect!")
    }
}
