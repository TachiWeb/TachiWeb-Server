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

package xyz.nulldev.ts.api.http.task

import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.api.task.TaskManager

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class TaskStatusRoute : TachiWebRoute() {

    private val taskManager: TaskManager = Injekt.get()

    override fun handleReq(request: Request, response: Response): Any {
        //Parse arguments
        val taskId = request.params(":taskId")?.toLong()
                ?: return error("TaskID must be specified!")
        //Get task
        val task = taskManager.getTask(taskId)
                ?: return error("No task found with the specified task ID!")
        val result = success()
        result.put(KEY_COMPELTE, task.complete)
        result.put(KEY_DETAILS, task.taskStatus)
        return result
    }

    companion object {
        val KEY_COMPELTE = "complete"
        val KEY_DETAILS = "details"
    }
}