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

package xyz.nulldev.ts.api.http.manga

import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.source.SourceManager
import eu.kanade.tachiyomi.data.source.online.OnlineSource
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
class MangaRoute : TachiWebRoute() {

    private val sourceManager: SourceManager = Injekt.get()

    override fun handleReq(request: Request, response: Response): Any {
        val mangaId = request.params(":mangaId")?.toLong()
                ?: return error("MangaID must be specified!")
        val manga = library.getManga(mangaId)
                ?: return error("The specified manga does not exist!")
        val builtResponse = success()
        builtResponse.put(KEY_TITLE, manga.title)
                .put(KEY_CHAPTER_COUNT, library.getChapters(manga).size)
        val source = sourceManager.get(manga.source)
        var url = ""
        if (source != null) {
            builtResponse.put(KEY_SOURCE_NAME, source.name)
            if (source is OnlineSource) {
                url = source.baseUrl + manga.url
            }
        }
        builtResponse.put(KEY_BROWSER_URL, url)
        if (!manga.artist.isNullOrEmpty()) {
            builtResponse.put(KEY_ARTIST, manga.artist)
        }
        if (!manga.author.isNullOrEmpty()) {
            builtResponse.put(KEY_AUTHOR, manga.author)
        }
        if (!manga.description.isNullOrEmpty()) {
            builtResponse.put(KEY_DESCRIPTION, manga.description)
        }
        if (!manga.genre.isNullOrEmpty()) {
            builtResponse.put(KEY_GENRES, manga.genre)
        }
        builtResponse.put(KEY_STATUS, statusToString(manga.status))
        builtResponse.put(KEY_FAVORITE, manga.favorite)
        //Send flags
        val flagObject = JSONObject()
        for (flag in MangaFlag.values()) {
            flagObject.put(flag.name, flag[manga]!!.name)
        }
        builtResponse.put(KEY_FLAGS, flagObject)
        return builtResponse
    }

    companion object {
        val KEY_TITLE = "title"
        val KEY_CHAPTER_COUNT = "chapters"
        val KEY_SOURCE_NAME = "source"
        val KEY_BROWSER_URL = "url"
        val KEY_ARTIST = "artist"
        val KEY_AUTHOR = "author"
        val KEY_DESCRIPTION = "description"
        val KEY_GENRES = "genres"
        val KEY_STATUS = "status"
        val KEY_FAVORITE = "favorite"
        val KEY_FLAGS = "flags"
        
        private fun statusToString(i: Int): String {
            when (i) {
                Manga.ONGOING -> return "Ongoing"
                Manga.COMPLETED -> return "Completed"
                Manga.LICENSED -> return "Licensed"
                Manga.UNKNOWN -> return "Unknown"
                else -> return "Unknown"
            }
        }
    }
}
