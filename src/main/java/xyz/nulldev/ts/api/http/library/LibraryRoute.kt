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

package xyz.nulldev.ts.api.http.library

import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.source.Source
import eu.kanade.tachiyomi.data.source.SourceManager
import org.json.JSONArray
import org.json.JSONObject
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.util.MangaUtils

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class LibraryRoute : TachiWebRoute() {

    private val downloadManager: DownloadManager = Injekt.get()
    private val sourceManager: SourceManager = Injekt.get()

    override fun handleReq(request: Request, response: Response): Any {
        val array = JSONArray()
        for (manga in library.favoriteMangas) {
            val source = sourceManager.get(manga.source)
            val mangaJson = JSONObject()
            mangaJson.put(KEY_ID, manga.id)
                    .put(KEY_TITLE, manga.title)
                    .put(KEY_UNREAD, MangaUtils.getUnreadCount(manga))
            if (source != null) {
                mangaJson.put(KEY_DOWNLOADED, isMangaDownloaded(source, manga))
            }
            val categoriesJson = JSONArray()
            for (category in library.getCategoriesForManga(manga)) {
                categoriesJson.put(category.name)
            }
            mangaJson.put(KEY_CATEGORIES, categoriesJson)
            array.put(mangaJson)
        }
        return success().put(KEY_CONTENT, array)
    }

    private fun isMangaDownloaded(source: Source, manga: Manga): Boolean {
        val mangaDir = downloadManager.getAbsoluteMangaDirectory(source, manga)

        if (mangaDir.exists()) {
            for (file in mangaDir.listFiles() ?: emptyArray()) {
                if (file.isDirectory && (file.listFiles() ?: emptyArray()).size >= 1) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        val KEY_TITLE = "title"
        val KEY_ID = "id"
        val KEY_UNREAD = "unread"
        val KEY_DOWNLOADED = "downloaded"
        val KEY_CATEGORIES = "categories"
        val KEY_CONTENT = "content"
    }
}
