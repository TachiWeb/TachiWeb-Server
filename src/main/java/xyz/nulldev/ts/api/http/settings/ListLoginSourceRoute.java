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

import eu.kanade.tachiyomi.data.source.online.LoginSource;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;

import java.util.stream.Collectors;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 23/07/16
 */
public class ListLoginSourceRoute extends TachiWebRoute {
    public static final String KEY_CONTENT = "content";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_LOGGED_IN = "logged_in";

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        JSONObject object = success();
        JSONArray content = new JSONArray();
        for (LoginSource source :
                DIReplacement.get()
                        .injectSourceManager()
                        .getSourcesMap()
                        .values()
                        .stream()
                        .filter(normalSource -> LoginSource.class.isAssignableFrom(normalSource.getClass()))
                        .map(loginSource -> (LoginSource) loginSource)
                        .collect(Collectors.toSet())) {
            JSONObject sourceJson = new JSONObject();
            sourceJson.put(KEY_NAME, source.getName());
            sourceJson.put(KEY_ID, source.getId());
            sourceJson.put(KEY_LOGGED_IN, source.isLogged());
            content.put(sourceJson);
        }
        object.put(KEY_CONTENT, content);
        return object;
    }
}
