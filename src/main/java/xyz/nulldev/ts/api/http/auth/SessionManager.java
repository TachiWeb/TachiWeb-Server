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

package xyz.nulldev.ts.api.http.auth;

import xyz.nulldev.ts.DIReplacement;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 26/09/16
 */
public class SessionManager {
    private final List<String> usedSessions = new ArrayList<>();
    private final List<String> authenticatedSessions = new ArrayList<>();
    private final SecureRandom random = new SecureRandom();

    public synchronized boolean isAuthenticated(String session) {
        return authenticatedSessions.contains(session);
    }

    public synchronized void authenticateSession(String session) {
        if(!authenticatedSessions.contains(session)) {
            authenticatedSessions.add(session);
        }
    }

    public synchronized String newSession() {
        String session;
        do {
            session = new BigInteger(130, random).toString(32);
        } while(usedSessions.contains(session));
        usedSessions.add(session);
        return session;
    }

    public synchronized void deauthAllSessions() {
        authenticatedSessions.clear();
    }

    public static boolean authEnabled() {
        return !authPassword().isEmpty();
    }

    public static String authPassword() {
        return DIReplacement.get().injectPreferencesHelper().authPassword().get();
    }
}
