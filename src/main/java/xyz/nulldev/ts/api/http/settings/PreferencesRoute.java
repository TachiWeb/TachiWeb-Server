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

package xyz.nulldev.ts.api.http.settings;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import uy.kohesive.injekt.InjektKt;
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

    private Context context = InjektKt.getInjekt().getInstance(Context.class);

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
