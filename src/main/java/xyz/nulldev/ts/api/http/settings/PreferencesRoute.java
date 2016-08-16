package xyz.nulldev.ts.api.http.settings;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.android.JsonSharedPreferences;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.settings.Preferences;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 09/08/16
 *
 * Direct access to a persistent storage mechanism for persisting data across clients
 */
public class PreferencesRoute extends TachiWebRoute {
    public static final String KEY_PREFS = "prefs";

    private Context context;

    public PreferencesRoute(Context context, Library library) {
        super(library);
        this.context = context;
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        SharedPreferences preferences = context.getSharedPreferences(Preferences.DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
        if(preferences instanceof JsonSharedPreferences) {
            JsonSharedPreferences casted = (JsonSharedPreferences) preferences;
            JSONObject parent = success();
            parent.put(KEY_PREFS, casted.saveToJSONObject());
            return parent;
        } else {
            return error("Preferences is not an instance of JsonSharedPreferences (internal server error).");
        }
    }
}
