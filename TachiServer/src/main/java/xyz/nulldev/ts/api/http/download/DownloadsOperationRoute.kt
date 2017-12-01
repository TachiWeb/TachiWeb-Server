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

package xyz.nulldev.ts.api.http.download

import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Pause/resume/clear downloads.
 */
class DownloadsOperationRoute : TachiWebRoute() {

    override fun handleReq(request: Request, response: Response): Any {
        //Get and parse operation
        val operation: Operation
        try {
            operation = Operation.valueOf((request.params(":operation") ?: "").toUpperCase())
        } catch (e: IllegalArgumentException) {
            return error("Invalid/no operation specified!")
        }

        //Perform operation
        when (operation) {
            Operation.PAUSE -> {
                if (!api.downloads.running) {
                    return error("Download manager is already paused!")
                }
                api.downloads.running = false
            }
            Operation.RESUME -> {
                if (api.downloads.running) {
                    return error("Download manager is not paused!")
                }
                api.downloads.running = true
            }
            Operation.CLEAR -> {
                api.downloads.clear()
            }
        }
        return success()
    }

    /**
     * The operation to perform
     */
    enum class Operation {
        PAUSE,
        RESUME,
        CLEAR
    }
}
