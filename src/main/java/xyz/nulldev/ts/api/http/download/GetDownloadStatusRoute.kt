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
import eu.kanade.tachiyomi.data.source.model.Page
import org.json.JSONArray
import org.json.JSONObject
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class GetDownloadStatusRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager = Injekt.get()

    override fun handleReq(request: Request, response: Response): Any {
        val builtResponse = success()
        val array = JSONArray()
        for (download in downloadManager.queue) {
            val downloadJson = JSONObject()
            val downloadPages = download.pages
            val downloadProgressMax: Float
            var downloadProgress: Float
            if (downloadPages != null) {
                downloadProgressMax = (downloadPages.size * 100).toFloat()
                downloadProgress = 0f
                var downloadedImages = 0
                for (page in download.pages!!) {
                    if (page.status == Page.READY) {
                        downloadedImages++
                    }
                    downloadProgress += page.progress.toFloat()
                }
                downloadJson.put(KEY_DOWNLOADED_IMAGES, downloadedImages)
                        .put(KEY_TOTAL_IMAGES, downloadPages.size)
            } else {
                downloadProgressMax = 1f
                downloadProgress = 0f
            }
            downloadJson.put(KEY_PROGRESS, (downloadProgress / downloadProgressMax).toDouble())
                    .put(KEY_MANGA_TITLE, download.manga.title)
                    .put(KEY_CHAPTER_NAME, download.chapter.name)
            array.put(downloadJson)
        }
        builtResponse.put(KEY_DOWNLOADS, array)
                .put(KEY_PAUSED, !downloadManager.isRunning)
        return builtResponse
    }

    companion object {
        val KEY_MANGA_TITLE = "manga_title"
        val KEY_CHAPTER_NAME = "chapter_name"
        val KEY_PROGRESS = "progress"
        val KEY_DOWNLOADED_IMAGES = "downloaded_images"
        val KEY_TOTAL_IMAGES = "total_images"
        val KEY_DOWNLOADS = "downloads"
        val KEY_PAUSED = "paused"
    }
}
