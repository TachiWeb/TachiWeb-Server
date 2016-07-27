package xyz.nulldev.ts.api.http;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.ts.Library;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/07/16
 */
public abstract class TachiWebRoute implements Route {

    private Library library;

    private static Logger logger = LoggerFactory.getLogger(TachiWebRoute.class);

    public TachiWebRoute(Library library) {
        this.library = library;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            response.header("Access-Control-Allow-Origin", "*");
            ReentrantLock masterLock = library.getMasterLock().get();
            if (masterLock != null) {
                masterLock.lock();
                try {
                    Object toReturn = handleReq(request, response);
                    masterLock.unlock();
                    return toReturn;
                } catch (Throwable e) {
                    masterLock.unlock();
                    throw e;
                }
            } else {
                return handleReq(request, response);
            }
        } catch (Exception e) {
            logger.error("Exception handling route!", e);
            throw e;
        }
    }

    public abstract Object handleReq(Request request, Response response) throws Exception;

    public Library getLibrary() {
        return library;
    }

    public static String error(String message) {
        JSONObject object = success(false);
        object.put("error", message);
        return object.toString();
    }

    public static JSONObject success() {
        return success(true);
    }

    public static JSONObject success(boolean success) {
        JSONObject object = new JSONObject();
        object.put("success", success);
        return object;
    }
}
