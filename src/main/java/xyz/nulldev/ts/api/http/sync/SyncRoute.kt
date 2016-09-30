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

package xyz.nulldev.ts.api.http.sync

import com.google.gson.JsonParser
import eu.kanade.tachiyomi.data.backup.BackupManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.api.task.Task
import xyz.nulldev.ts.api.task.TaskManager
import xyz.nulldev.ts.library.Library
import xyz.nulldev.ts.sync.LibraryComparer
import xyz.nulldev.ts.sync.conflict.Conflict
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 *
 * POST Sync Route
 */
class SyncRoute : TachiWebRoute() {

    private val backupManager: BackupManager = Injekt.get()
    private val taskManager: TaskManager = Injekt.get()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        //Response is always UTF_8
        response.raw().characterEncoding = StandardCharsets.UTF_8.name()

        //Create new task
        val task = taskManager.newTask()

        //Parse libraries
        updateTaskStatus(task, "Parsing libraries...")
        val requestBody = String(request.bodyAsBytes(), StandardCharsets.UTF_8)

        //Fork sync onto new thread
        thread {
            logger.info("Starting sync...")
            try {
                //Parse response body
                val parsedRequestBody: JSONObject
                try {
                    parsedRequestBody = JSONObject(requestBody)
                } catch (e: JSONException) {
                    updateTaskError(task, "Error parsing request body!")
                    return@thread
                }

                //Get old and new libraries from request
                val oldLibrary = parsedRequestBody.getString(OLD_LIBRARY)
                val newLibrary = parsedRequestBody.getString(NEW_LIBRARY)
                if (oldLibrary == null) {
                    updateTaskError(task, "Old library not specified!")
                    return@thread
                } else if (newLibrary == null) {
                    updateTaskError(task, "New library not specified!")
                    return@thread
                }
                //Favorites only?
                val favoritesOnly = parsedRequestBody.has(FAVORITES_ONLY) && parsedRequestBody.getBoolean(FAVORITES_ONLY)

                //Parse old and new libraries
                val parser = JsonParser()
                val parsedOldLibrary = Library()
                backupManager.restoreFromJson(
                        parser.parse(oldLibrary).asJsonObject,
                        parsedOldLibrary)
                val parsedNewLibrary = Library()
                backupManager.restoreFromJson(
                        parser.parse(newLibrary).asJsonObject,
                        parsedNewLibrary)

                //Compare libraries
                updateTaskStatus(task, "Comparing libraries...")
                val changes = LibraryComparer.compareLibraries(
                        parsedOldLibrary, parsedNewLibrary)

                //Apply changes
                updateTaskStatus(task, "Applying changes...")
                val transaction = library.newTransaction()
                val conflicts = ArrayList<Conflict>()
                for (i in changes.indices) {
                    val change = changes[i]
                    updateTaskStatus(
                            task,
                            "Applying change ${i + 1}/${changes.size}: ${change.toHumanForm()}")
                    logger.info("SYNC: Applying change: {}", change.toHumanForm())
                    //Actually apply change
                    val possibleConflict = change.tryApply(transaction.library)
                    //Log conflicts
                    if (possibleConflict != null) {
                        conflicts.add(possibleConflict)
                    }
                }

                //Write changes to report
                updateTaskStatus(task, "Generating sync report...")
                val result = JSONObject()
                val changeJSON = JSONArray()
                for (change in changes) {
                    changeJSON.put(change.toHumanForm())
                }
                result.put(KEY_CHANGES, changeJSON)

                //Write conflicts to report
                val conflictsJSON = JSONArray()
                for (conflict in conflicts) {
                    //TODO Somehow, we need to be able to handle more complex conflicts
                    conflictsJSON.put(conflict.description)
                }
                result.put(KEY_CONFLICTS, conflictsJSON)

                //Write resulting library to report
                val serializedLibrary = backupManager.backupToString(
                        favoritesOnly, transaction.library)
                result.put(KEY_LIBRARY, serializedLibrary)

                //Looks like everything went smoothly, apply library to server
                transaction.apply()
                updateTaskStatus(task, "Sync complete!", result)
            } catch (t: Throwable) {
                updateTaskError(task, "Sync failed (" + t.message + ")!")
                logger.error("An exception was thrown during synchronization!", t)
            }

            task.complete = true
        }

        val result = TachiWebRoute.success()
        result.put(KEY_TASK_ID, task.taskID)
        return result.toString()
    }

    private fun updateTaskStatus(task: Task, status: String, result: JSONObject? = null) {
        val statusObject = JSONObject()
        statusObject.put(KEY_STATUS, status)
        if (result != null) {
            statusObject.put(KEY_RESULT, result)
        }
        task.taskStatus = statusObject.toString()
    }

    private fun updateTaskError(task: Task, error: String) {
        val errorObject = JSONObject()
        errorObject.put(KEY_ERROR, error)
        task.taskStatus = errorObject.toString()
    }

    companion object {
        val OLD_LIBRARY = "old_library"
        val NEW_LIBRARY = "new_library"
        val FAVORITES_ONLY = "favorites_only"

        //Request keys
        val KEY_TASK_ID = "task_id"

        //Result keys
        val KEY_CHANGES = "changes"
        val KEY_CONFLICTS = "conflicts"
        val KEY_LIBRARY = "library"

        //Task detail keys
        val KEY_STATUS = "status"
        val KEY_RESULT = "result"
        val KEY_ERROR = "error"
    }
}
