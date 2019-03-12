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

package xyz.nulldev.ts.api.http.serializer

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.LibraryManga
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import org.json.JSONArray
import org.json.JSONObject
import xyz.nulldev.ts.api.http.manga.MangaFlag
import xyz.nulldev.ts.api.java.util.isDownloaded
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 19/10/16
 */

class MangaSerializer {

    private val db: DatabaseHelper by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()
    private val downloadManager: DownloadManager by kInstanceLazy()

    fun serialize(manga: Manga, fromLibrary: Boolean = false): JSONObject {
        val builtResponse = JSONObject()
        builtResponse.put(KEY_TITLE, manga.title)
                .put(KEY_CHAPTER_COUNT, db.getChapters(manga).executeAsBlocking().size)
                .put(KEY_ID, manga.id)

        if (fromLibrary) {
            manga as LibraryManga
            builtResponse.put(KEY_UNREAD, manga.unread)
        }

        val source = sourceManager.get(manga.source)
        var url = ""
        if (source != null) {
            builtResponse.put(KEY_SOURCE_NAME, source.name)
            if (source is HttpSource) {
                url = source.baseUrl + manga.url
            }
            builtResponse.put(KEY_DOWNLOADED, manga.isDownloaded)
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
        if(!manga.thumbnail_url.isNullOrBlank()) {
            builtResponse.put(KEY_THUMBNAIL_URL, manga.thumbnail_url)
        }
        builtResponse.put(KEY_STATUS, statusToString(manga.status))
        builtResponse.put(KEY_FAVORITE, manga.favorite)
        //Send flags
        val flagObject = JSONObject()
        for (flag in MangaFlag.values()) {
            flagObject.put(flag.name, flag[manga]!!.name)
        }
        builtResponse.put(KEY_FLAGS, flagObject)
        //Categories
        //TODO Is it possible to get this directly from the categories field?
        val categoriesJson = JSONArray()
        for (category in db.getCategoriesForManga(manga).executeAsBlocking()) {
            categoriesJson.put(JSONObject()
                    .put(KEY_CATEGORY_ID, category.id)
                    .put(KEY_CATEGORY_NAME, category.name))
        }
        builtResponse.put(KEY_CATEGORIES, categoriesJson)
        return builtResponse
    }

    companion object {
        val KEY_ID = "id"
        val KEY_THUMBNAIL_URL = "thumbnail_url"
        val KEY_UNREAD = "unread"
        val KEY_DOWNLOADED = "downloaded"
        val KEY_CATEGORIES = "categories"
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

        val KEY_CATEGORY_ID = "id"
        val KEY_CATEGORY_NAME = "name"

        private fun statusToString(i: Int): String {
            return when (i) {
                SManga.ONGOING -> "Ongoing"
                SManga.COMPLETED -> "Completed"
                SManga.LICENSED -> "Licensed"
                SManga.UNKNOWN -> "Unknown"
                else -> "Unknown"
            }
        }
    }
}
