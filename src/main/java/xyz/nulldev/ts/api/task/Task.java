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

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Project: WebLinkedServer
 * Created: 28/03/16
 * Author: nulldev
 *
 * Simple task with creation time, completion status and task details
 */
public class Task {
    private final long taskID;
    private final AtomicBoolean complete = new AtomicBoolean(false);
    private final AtomicReference<String> taskStatus = new AtomicReference<>("");
    private final LocalDateTime creationTime;

    Task(long taskID) {
        this.taskID = taskID;
        this.creationTime = LocalDateTime.now();
    }

    public long getTaskID() {
        return taskID;
    }

    public boolean isComplete() {
        return complete.get();
    }

    public void setComplete(boolean complete) {
        this.complete.set(complete);
    }

    public String getTaskStatus() {
        return taskStatus.get();
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus.set(taskStatus);
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return taskID == task.taskID
                && (complete != null
                        ? complete.equals(task.complete)
                        : task.complete == null
                                && (taskStatus != null
                                        ? taskStatus.equals(task.taskStatus)
                                        : task.taskStatus == null
                                                && creationTime.equals(task.creationTime)));
    }

    @Override
    public int hashCode() {
        int result = (int) (taskID ^ (taskID >>> 32));
        result = 31 * result + (complete != null ? complete.hashCode() : 0);
        result = 31 * result + (taskStatus != null ? taskStatus.hashCode() : 0);
        result = 31 * result + creationTime.hashCode();
        return result;
    }
}
