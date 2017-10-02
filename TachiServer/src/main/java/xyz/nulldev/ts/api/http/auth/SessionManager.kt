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

import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.ext.authPassword
import java.math.BigInteger
import java.security.SecureRandom

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class SessionManager {

    private val usedSessions = mutableListOf<String>()
    private val authenticatedSessions = mutableListOf<String>()
    private val random = SecureRandom()

    @Synchronized fun isAuthenticated(session: String): Boolean {
        return authenticatedSessions.contains(session)
    }

    @Synchronized fun authenticateSession(session: String) {
        if (!authenticatedSessions.contains(session)) {
            authenticatedSessions.add(session)
        }
    }

    @Synchronized fun newSession(): String {
        var session: String
        do {
            session = BigInteger(130, random).toString(32)
        } while (usedSessions.contains(session))
        usedSessions.add(session)
        return session
    }

    @Synchronized fun deauthAllSessions() {
        authenticatedSessions.clear()
    }

    companion object {
        private val preferencesHelper by lazy {
            Injekt.get<PreferencesHelper>()
        }

        fun authEnabled(): Boolean {
            return authPassword().isNotEmpty()
        }

        fun authPassword(): String {
            return preferencesHelper.authPassword().getOrDefault()
        }
    }
}
