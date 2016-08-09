package xyz.nulldev.ts.api.http.settings;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.settings.Preferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 09/08/16
 */
public class SetPreferenceRoute extends TachiWebRoute {
    private Context context;

    public SetPreferenceRoute(Library library, Context context) {
        super(library);
        this.context = context;
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        String type = request.params(":type");
        String key = request.params(":key");
        String value = request.queryParams("value");
        if(type == null) {
            return error("Type not specified!");
        } else if(key == null) {
            return error("Key not specified!");
        }
        SharedPreferences.Editor preferences = context.getSharedPreferences(Preferences.DEFAULT_PREFERENCES, Context.MODE_PRIVATE).edit();
        //Support preference removal
        if(value == null) {
            if(type.equalsIgnoreCase("remove") || type.equalsIgnoreCase("delete")) {
                preferences.remove(key);
            } else {
                return error("Value not specified (and not removing preference)!");
            }
        } else {
            try {
                switch (type.toLowerCase()) {
                    case "boolean":
                        preferences.putBoolean(key, Boolean.parseBoolean(value));
                        break;
                    case "string":
                        preferences.putString(key, value);
                        break;
                    case "float":
                        preferences.putFloat(key, Float.parseFloat(value));
                        break;
                    case "int":
                        preferences.putInt(key, Integer.parseInt(value));
                        break;
                    case "long":
                        preferences.putLong(key, Long.parseLong(value));
                        break;
                    case "string_set":
                        //Stringset is assumed to be in Json array format
                        JSONArray array = new JSONArray(value);
                        Set<String> generatedSet = new HashSet<>();
                        for (int i = 0; i < array.length(); i++) {
                            generatedSet.add(array.getString(i));
                        }
                        preferences.putStringSet(key, generatedSet);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid/unsupported type!");
                }
            } catch (Throwable t) {
                return error("Invalid type/value!");
            }
        }
        preferences.commit();
        return success();
    }
}
