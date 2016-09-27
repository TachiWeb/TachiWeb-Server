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
    public CheckSessionRoute(Library library) {
        super(library, false); //No auth on this route
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
