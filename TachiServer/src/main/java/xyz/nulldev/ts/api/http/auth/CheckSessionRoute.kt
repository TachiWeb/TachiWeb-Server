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

package xyz.nulldev.ts.api.http.auth

import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Simple route to check if an auth password is correct.
 *
 * If the auth password is correct, the user is authenticated.
 */
class CheckSessionRoute: TachiWebRoute(false /* No auth */) {
    override fun handleReq(request: Request, response: Response): Any {
        val session: String = request.attribute("session")
        val password = request.queryParams("password")
        
        val authPw = SessionManager.authPassword()

        val valid = if(password.isEmpty())
            authPw.isEmpty()
        else
            authPw.isNotEmpty() && PasswordHasher.check(password, authPw)

        return if (valid) {
            sessionManager.authenticateSession(session)
            success().put(KEY_TOKEN, session)
        } else {
            error("Incorrect password!")
        }
    }

    companion object {
        val KEY_TOKEN = "token"
    }
}
