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

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
data class Task internal constructor(val taskID: Long) {
    private val internalComplete = AtomicBoolean(false)
    private val internalTaskStatus = AtomicReference("")
    val creationTime: LocalDateTime = LocalDateTime.now()

    var complete: Boolean
        get() = internalComplete.get()
        set(complete) = this.internalComplete.set(complete)

    var taskStatus: String
        get() = internalTaskStatus.get()
        set(newTaskStatus) = this.internalTaskStatus.set(newTaskStatus)
}