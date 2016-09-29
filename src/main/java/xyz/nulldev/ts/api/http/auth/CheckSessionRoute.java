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

import spark.Request;
import spark.Response;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.library.Library;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 26/09/16
 */
public class CheckSessionRoute extends TachiWebRoute {
    public CheckSessionRoute() {
        super(false); //No auth on this route
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        String session = request.attribute("session");
        String password = request.queryParams("password");
        if(SessionManager.authPassword().equals(password)) {
            getSessionManager().authenticateSession(session);
            return success();
        } else {
            return error("Incorrect password!");
        }
    }
}
