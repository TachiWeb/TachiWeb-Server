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

package xyz.nulldev.ts.api.http.sync;

import com.google.gson.JsonParser;
import eu.kanade.tachiyomi.data.backup.BackupManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import uy.kohesive.injekt.InjektKt;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.api.task.Task;
import xyz.nulldev.ts.api.task.TaskManager;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.LibraryComparer;
import xyz.nulldev.ts.sync.conflict.Conflict;
import xyz.nulldev.ts.sync.operation.Operation;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/08/16
 *
 * POST Sync route
 **/
public class SyncRoute extends TachiWebRoute {
    public static final String OLD_LIBRARY = "old_library";
    public static final String NEW_LIBRARY = "new_library";
    public static final String FAVORITES_ONLY = "favorites_only";

    //Request keys
    public static final String KEY_TASK_ID = "task_id";

    //Result keys
    public static final String KEY_CHANGES = "changes";
    public static final String KEY_CONFLICTS = "conflicts";
    public static final String KEY_LIBRARY = "library";

    //Task detail keys
    public static final String KEY_STATUS = "status";
    public static final String KEY_RESULT = "result";
    public static final String KEY_ERROR = "error";

    private static Logger logger = LoggerFactory.getLogger(SyncRoute.class);

    private BackupManager backupManager = InjektKt.getInjekt().getInstance(BackupManager.class);
    private TaskManager taskManager = InjektKt.getInjekt().getInstance(TaskManager.class);

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        //Response is always UTF_8
        response.raw().setCharacterEncoding(StandardCharsets.UTF_8.name());

        //Create new task
        Task task = taskManager.newTask();

        //Parse libraries
        updateTaskStatus(task, "Parsing libraries...");
        String requestBody = new String(request.bodyAsBytes(), StandardCharsets.UTF_8);

        //Fork sync onto new thread
        new Thread(
                        () -> {
                            logger.info("Starting sync...");
                            try {
                                JSONObject parsedRequestBody;
                                try {
                                    parsedRequestBody = new JSONObject(requestBody);
                                } catch (JSONException e) {
                                    updateTaskError(task, "Error parsing request body!");
                                    return;
                                }
                                String oldLibrary = parsedRequestBody.getString(OLD_LIBRARY);
                                String newLibrary = parsedRequestBody.getString(NEW_LIBRARY);
                                if (oldLibrary == null) {
                                    updateTaskError(task, "Old library not specified!");
                                    return;
                                } else if (newLibrary == null) {
                                    updateTaskError(task, "New library not specified!");
                                    return;
                                }
                                boolean favoritesOnly = parsedRequestBody.has(FAVORITES_ONLY) && parsedRequestBody.getBoolean(FAVORITES_ONLY);

                                JsonParser parser = new JsonParser();
                                Library parsedOldLibrary = new Library();
                                backupManager.restoreFromJson(
                                        parser.parse(oldLibrary).getAsJsonObject(),
                                        parsedOldLibrary);
                                Library parsedNewLibrary = new Library();
                                backupManager.restoreFromJson(
                                        parser.parse(newLibrary).getAsJsonObject(),
                                        parsedNewLibrary);

                                //Compare libraries
                                updateTaskStatus(task, "Comparing libraries...");
                                List<Operation> changes =
                                        LibraryComparer.compareLibraries(
                                                parsedOldLibrary, parsedNewLibrary);

                                //Apply changes
                                updateTaskStatus(task, "Applying changes...");
                                Library.LibraryTransaction transaction =
                                        getLibrary().newTransaction();
                                List<Conflict> conflicts = new ArrayList<>();
                                for (int i = 0; i < changes.size(); i++) {
                                    Operation change = changes.get(i);
                                    updateTaskStatus(
                                            task,
                                            "Applying change "
                                                    + (i + 1)
                                                    + "/"
                                                    + changes.size()
                                                    + ": "
                                                    + change.toHumanForm());
                                    logger.info("SYNC: Applying change: {}", change.toHumanForm());
                                    Conflict possibleConflict =
                                            change.tryApply(transaction.getLibrary());
                                    if (possibleConflict != null) {
                                        conflicts.add(possibleConflict);
                                    }
                                }

                                //Write changes to report
                                updateTaskStatus(task, "Generating sync report...");
                                JSONObject result = new JSONObject();
                                JSONArray changeJSON = new JSONArray();
                                for (Operation change : changes) {
                                    changeJSON.put(change.toHumanForm());
                                }
                                result.put(KEY_CHANGES, changeJSON);

                                //Write conflicts to report
                                JSONArray conflictsJSON = new JSONArray();
                                for (Conflict conflict : conflicts) {
                                    //TODO Somehow, we need to be able to handle more complex conflicts
                                    conflictsJSON.put(conflict.getDescription());
                                }
                                result.put(KEY_CONFLICTS, conflictsJSON);

                                //Write resulting library to report
                                String serializedLibrary =
                                        backupManager.backupToString(
                                                favoritesOnly, transaction.getLibrary());
                                result.put(KEY_LIBRARY, serializedLibrary);

                                //Looks like everything went smoothly, apply library to server
                                transaction.apply();
                                updateTaskStatus(task, "Sync complete!", result);
                            } catch (Throwable t) {
                                updateTaskError(task, "Sync failed (" + t.getMessage() + ")!");
                                logger.error("An exception was thrown during synchronization!", t);
                            }
                            task.setComplete(true);
                        })
                .start();

        JSONObject result = success();
        result.put(KEY_TASK_ID, task.getTaskID());
        return result.toString();
    }

    private void updateTaskStatus(Task task, String status) {
        updateTaskStatus(task, status, null);
    }

    private void updateTaskStatus(Task task, String status, JSONObject result) {
        JSONObject object = new JSONObject();
        object.put(KEY_STATUS, status);
        if (result != null) {
            object.put(KEY_RESULT, result);
        }
        task.setTaskStatus(object.toString());
    }

    private void updateTaskError(Task task, String error) {
        JSONObject object = new JSONObject();
        object.put(KEY_ERROR, error);
        task.setTaskStatus(object.toString());
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(
                    URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
