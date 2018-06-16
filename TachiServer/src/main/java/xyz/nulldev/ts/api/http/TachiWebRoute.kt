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

package xyz.nulldev.ts.api.http

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import org.json.JSONObject
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.api.http.auth.SessionManager
import xyz.nulldev.ts.api.java.TachiyomiAPI
import xyz.nulldev.ts.api.java.model.ServerAPIInterface
import java.util.*

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
abstract class TachiWebRoute(
        val requiresAuth: Boolean = true
) : Route {

    protected val api: ServerAPIInterface = TachiyomiAPI

    @Throws(Exception::class)
    override fun handle(request: Request, response: Response): Any? {
        try {
            response.header("Access-Control-Allow-Origin", "*")
            response.header("Access-Control-Allow-Credentials", "true")
            var session: String? = request.cookie("session")
            if (session.isNullOrBlank()) {
                //Try session header if session is not provided in cookie
                session = request.headers(SESSION_HEADER)
                if(session.isNullOrBlank()) {
                    session = sessionManager.newSession()
                    response.removeCookie("session")
                    response.cookie("session", session)
                }
            }
            request.attribute("session", session)
            //Auth all sessions if auth not enabled
            if (!SessionManager.authEnabled()) {
                sessionManager.authenticateSession(session!!)
            }
            if (!sessionManager.isAuthenticated(session!!) && requiresAuth) {
                //Not authenticated!
                return error("Not authenticated!")
            } else {
                val res = try {
                    handleReq(request, response)
                } catch(t: Throwable) {
                    val uuid = UUID.randomUUID()
                    logger.error("Route handler failure (error ID: $uuid)!", t)
                    return error("Unknown API handler error!", uuid)
                }

                // Filter response
                val jsonWhitelist = (request.queryParamsValues("jw") ?: emptyArray()).map {
                    it.trim()
                }.filter { it.isNotBlank() }.map {
                    JsonPath.compile(it)
                }

                val jsonBlacklist = (request.queryParamsValues("jb") ?: emptyArray()).map {
                    it.trim()
                }.filter { it.isNotBlank() }.map {
                    JsonPath.compile(it)
                }

                return if(jsonWhitelist.isNotEmpty() || jsonBlacklist.isNotEmpty()) {
                    val parsed = try {
                        JsonPath.parse(res.toString())
                    } catch(e: Exception) {
                        logger.warn("Json path filtering failed on route: ${this::class.qualifiedName}", e)
                        return res
                    }

                    // Handle blacklist
                    for(path in jsonBlacklist)
                        try {
                            parsed.delete(path)
                        } catch(t: Throwable) {}

                    // Handle whitelist
                    if(jsonWhitelist.isNotEmpty()) {
                        val pathObtainer = JsonPath.using(Configuration.builder()
                                .options(Option.AS_PATH_LIST, Option.ALWAYS_RETURN_LIST)
                                .build()).parse(parsed.jsonString())

                        val valid = jsonWhitelist.flatMap {
                            try {
                                pathObtainer.read<List<String>>(it)
                            } catch(t: Throwable) { null } ?: emptyList()
                        }

                        pathObtainer.read<List<String>>("$..*").forEach { path ->
                            if(path !in valid && !valid.any { it.startsWith(path) })
                                try {parsed.delete(path)} catch(t: Throwable) {}
                        }
                    }

                    parsed.jsonString()
                } else res
            }
        } catch (e: Exception) {
            val uuid = UUID.randomUUID()
            logger.error("Exception handling route (error ID: $uuid)!", e)
            return error("Unknown internal server error!", uuid)
        }
    }

    abstract fun handleReq(request: Request, response: Response): Any?

    companion object {

        const val SESSION_HEADER = "TW-Session"

        private val logger = LoggerFactory.getLogger(TachiWebRoute::class.java)

        val sessionManager = SessionManager()

        fun error(message: String, uuid: UUID? = null): JSONObject
                = success(false).put("error", message).apply {
            if(uuid != null)
                put("uuid", uuid.toString())
        }

        @JvmOverloads fun success(success: Boolean = true): JSONObject
                = JSONObject().put("success", success)
    }
}