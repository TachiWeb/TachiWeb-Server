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

import eu.kanade.tachiyomi.data.download.DownloadManager
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Pause/resume/clear downloads.
 */
class DownloadsOperationRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager = Injekt.get()

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
                if (!downloadManager.isRunning) {
                    return error("Download manager is already paused!")
                }
                downloadManager.destroySubscriptions()
            }
            Operation.RESUME -> {
                if (downloadManager.isRunning) {
                    return error("Download manager is not paused!")
                }
                //I assume we can restart the download manager without reinitialization (appears to work fine)
                downloadManager.startDownloads()
            }
            Operation.CLEAR -> {
                downloadManager.destroySubscriptions()
                downloadManager.clearQueue()
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
