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

package xyz.nulldev.ts.api.task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: WebLinkedServer
 * Created: 28/03/16
 * Author: nulldev
 *
 * Simple task manager, manages creation and storage of active tasks, automatically cleans old tasks
 */
public class TaskManager {
    public static final int OLD_TASK_LIMIT = 100;

    private ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private Random taskIdGenerator = new Random();

    /**
     * Get a new task, thread safe.
     * @return A new task
     */
    public synchronized Task newTask() {
        //Delete old tasks
        cleanOldTasks();
        //Generate task ID
        long taskId = -1;
        while (taskId == -1 || tasks.containsKey(taskId)) {
            taskId = taskIdGenerator.nextLong();
        }
        Task task = new Task(taskId);
        tasks.put(taskId, task);
        return task;
    }

    /**
     * Delete old, uneeded tasks
     */
    public synchronized void cleanOldTasks() {
        tasks.values()
                .stream()
                .filter(Task::isComplete)
                .sorted((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime())) //Sort in reverse
                .skip(OLD_TASK_LIMIT)
                .forEach(task -> tasks.remove(task.getTaskID()));
    }

    /**
     * Get a task by it's ID
     * @param id The ID of the task to get
     * @return The task with the supplied ID (or null if not found)
     */
    public Task getTask(long id) {
        return tasks.get(id);
    }
}
