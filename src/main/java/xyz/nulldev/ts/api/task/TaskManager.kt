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

package xyz.nulldev.ts.api.task

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class TaskManager {

    private val tasks = ConcurrentHashMap<Long, Task>()
    private val taskIdGenerator = Random()

    /**
     * Get a new task, thread safe.
     * @return A new task
     */
    @Synchronized fun newTask(): Task {
        //Delete old tasks
        cleanOldTasks()
        //Generate task ID
        var taskId = -1L
        while (taskId == -1L || tasks.containsKey(taskId)) {
            taskId = taskIdGenerator.nextLong()
        }
        val task = Task(taskId)
        tasks.put(taskId, task)
        return task
    }

    /**
     * Delete old, uneeded tasks
     */
    @Synchronized fun cleanOldTasks() {
        tasks.values
                .filter(Task::complete)
                .sortedByDescending(Task::creationTime) //Sort in reverse
                .drop(OLD_TASK_LIMIT)
                .forEach { task -> tasks.remove(task.taskID) }
    }

    /**
     * Get a task by it's ID
     * @param id The ID of the task to get
     * *
     * @return The task with the supplied ID (or null if not found)
     */
    fun getTask(id: Long): Task? {
        return tasks[id]
    }

    companion object {
        val OLD_TASK_LIMIT = 100
    }
}
