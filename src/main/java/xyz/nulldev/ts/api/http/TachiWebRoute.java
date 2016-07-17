package xyz.nulldev.ts.api.http;

import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.ts.Library;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/07/16
 */
public abstract class TachiWebRoute implements Route {

    private Library library;

    public TachiWebRoute(Library library) {
        this.library = library;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Object masterLock = library.getMasterLock().get();
        if(masterLock != null) {
            synchronized (masterLock) {
                return handleReq(request, response);
            }
        } else {
            return handleReq(request, response);
        }
    }

    public abstract Object handleReq(Request request, Response response) throws Exception;

    public Library getLibrary() {
        return library;
    }
}
