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
import android.preference.PreferenceManager
import eu.kanade.tachiyomi.data.preference.PreferenceKeys
import org.json.JSONArray
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.api.http.auth.PasswordHasher
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import xyz.nulldev.ts.ext.authPassword
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class SetPreferenceRoute : TachiWebRoute() {
    private val context: Context by kInstanceLazy()

    private val serverConfig by lazy { ConfigManager.module<ServerConfig>() }

    override fun handleReq(request: Request, response: Response): Any {
        //Do not allow changing configuration in demo mode
        if (!serverConfig.allowConfigChanges) {
            return error("The configuration cannot be changed in demo mode!")
        }
        val type = request.params(":type")
        val key = request.params(":key")
        val value = request.queryParams("value")
        if (type == null) {
            return error("Type not specified!")
        } else if (key == null) {
            return error("Key not specified!")
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
        //Support preference removal
        if (value == null) {
            if (type.equals("remove", ignoreCase = true) || type.equals("delete", ignoreCase = true)) {
                preferences.remove(key)
            } else {
                return error("Value not specified (and not removing preference)!")
            }
        } else {
            try {
                //Special case server password
                when(key) {
                    PreferenceKeys.authPassword -> {
                        preferences.putString(key,
                                if(value.isNotEmpty())
                                    PasswordHasher.getSaltedHash(value)
                                else ""
                        )
                    }

                    else -> {
                        //Map value to type
                        when (type.toLowerCase()) {
                            "boolean" -> preferences.putBoolean(key, java.lang.Boolean.parseBoolean(value))
                            "string" -> preferences.putString(key, value)
                            "float" -> preferences.putFloat(key, java.lang.Float.parseFloat(value))
                            "int" -> preferences.putInt(key, Integer.parseInt(value))
                            "long" -> preferences.putLong(key, java.lang.Long.parseLong(value))
                            "string_set" -> {
                                //Stringset is assumed to be in Json array format
                                val array = JSONArray(value)
                                val generatedSet = (0 until array.length())
                                        .map { array.getString(it) }
                                        .toSet()
                                preferences.putStringSet(key, generatedSet)
                            }
                            else -> throw IllegalArgumentException("Invalid/unsupported type!")
                        }
                    }
                }
            } catch (t: Throwable) {
                return error("Invalid type/value!")
            }

        }
        preferences.commit()
        return success()
    }
}
