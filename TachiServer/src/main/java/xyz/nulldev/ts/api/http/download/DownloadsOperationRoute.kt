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

import android.content.Context
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.DownloadService
import spark.Request
import spark.Response
import uy.kohesive.injekt.injectLazy
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Pause/resume/clear downloads.
 */
class DownloadsOperationRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager by kInstanceLazy()
    private val context : Context by injectLazy()

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
                if (!downloadManager.runningRelay.value) {
                    return error("Download manager is already paused!")
                }
                DownloadService.stop(context)
            }
            Operation.RESUME -> {
                if (downloadManager.runningRelay.value) {
                    return error("Download manager is not paused!")
                }
                DownloadService.start(context)
            }
            Operation.CLEAR -> {
                DownloadService.stop(context)
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
