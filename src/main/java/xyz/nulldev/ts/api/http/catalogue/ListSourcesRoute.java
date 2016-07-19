package xyz.nulldev.ts.api.http.catalogue;

import eu.kanade.tachiyomi.data.source.online.OnlineSource;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 19/07/16
 */
public class ListSourcesRoute extends TachiWebRoute {
    public static final String KEY_CONTENT = "content";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";

    public ListSourcesRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        List<OnlineSource> sources = DIReplacement.get().injectSourceManager().getOnlineSources();
        JSONObject rootObject = success(true);
        JSONArray contentArray = new JSONArray();
        for(OnlineSource source : sources) {
            JSONObject sourceObj = new JSONObject();
            sourceObj.put(KEY_ID, source.getId());
            sourceObj.put(KEY_NAME, source.getName());
            contentArray.put(sourceObj);
        }
        rootObject.put(KEY_CONTENT, contentArray);
        return rootObject;
    }
}
