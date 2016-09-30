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

import android.content.Context
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.android.JsonSharedPreferences
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.settings.Preferences

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 *
 * Direct access to a persistent storage mechanism for persisting data across clients
 */
class PreferencesRoute : TachiWebRoute() {

    private val context: Context = Injekt.get()

    override fun handleReq(request: Request, response: Response): Any {
        val preferences = context.getSharedPreferences(Preferences.DEFAULT_PREFERENCES, Context.MODE_PRIVATE)
        if (preferences is JsonSharedPreferences) {
            val parent = success()
            parent.put(KEY_PREFS, preferences.saveToJSONObject())
            return parent
        } else {
            return error("Preferences is not an instance of JsonSharedPreferences (internal server error).")
        }
    }

    companion object {
        val KEY_PREFS = "prefs"
    }
}