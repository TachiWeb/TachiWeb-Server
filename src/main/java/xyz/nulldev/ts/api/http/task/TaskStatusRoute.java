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

package xyz.nulldev.ts.api.http.task;

import org.json.JSONObject;
import spark.Request;
import spark.Response;
import uy.kohesive.injekt.InjektKt;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.api.task.Task;
import xyz.nulldev.ts.api.task.TaskManager;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.util.LeniantParser;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 27/08/16
 */
public class TaskStatusRoute extends TachiWebRoute {

    public static final String KEY_COMPELTE = "complete";
    public static final String KEY_DETAILS = "details";

    private TaskManager taskManager = InjektKt.getInjekt().getInstance(TaskManager.class);

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        //Parse arguments
        Long taskId = LeniantParser.parseLong(request.params(":taskId"));
        if (taskId == null) {
            return error("TaskID must be specified!");
        }
        //Get task
        Task task = taskManager.getTask(taskId);
        if(task == null) {
            return error("No task found with the specified task ID!");
        }
        JSONObject result = success();
        result.put(KEY_COMPELTE, task.isComplete());
        result.put(KEY_DETAILS, task.getTaskStatus());
        return result;
    }
}
