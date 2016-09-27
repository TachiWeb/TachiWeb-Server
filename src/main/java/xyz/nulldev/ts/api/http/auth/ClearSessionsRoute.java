package xyz.nulldev.ts.api.http.auth;

import spark.Request;
import spark.Response;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.library.Library;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 27/09/16
 */
public class ClearSessionsRoute extends TachiWebRoute {
    public ClearSessionsRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        getSessionManager().deauthAllSessions();
        getSessionManager().authenticateSession(request.attribute("session"));
        return success();
    }
}
