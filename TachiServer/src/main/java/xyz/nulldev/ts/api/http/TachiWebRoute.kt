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

import org.json.JSONObject
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.api.http.auth.SessionManager
import xyz.nulldev.ts.api.java.TachiyomiAPI
import xyz.nulldev.ts.api.java.model.ServerAPIInterface

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
abstract class TachiWebRoute : Route {

    private val requiresAuth: Boolean
    protected val api: ServerAPIInterface = TachiyomiAPI

    constructor() {
        requiresAuth = true
    }

    constructor(requiresAuth: Boolean) {
        this.requiresAuth = requiresAuth
    }

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
                return handleReq(request, response)
            }
        } catch (e: Exception) {
            logger.error("Exception handling route!", e)
            throw e
        }
    }

    abstract fun handleReq(request: Request, response: Response): Any?

    companion object {

        val SESSION_HEADER = "TW-Session"

        private val logger = LoggerFactory.getLogger(TachiWebRoute::class.java)

        val sessionManager = SessionManager()

        fun error(message: String): JSONObject
                = success(false).put("error", message)

        @JvmOverloads fun success(success: Boolean = true): JSONObject
                = JSONObject().put("success", success)
    }
}