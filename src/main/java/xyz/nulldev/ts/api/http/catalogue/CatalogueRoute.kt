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

package xyz.nulldev.ts.api.http.catalogue

import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.source.SourceManager
import eu.kanade.tachiyomi.data.source.model.MangasPage
import eu.kanade.tachiyomi.data.source.online.OnlineSource
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import rx.Observable
import spark.Request
import spark.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.DIReplacement
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.util.LeniantParser
import xyz.nulldev.ts.util.StringUtils

/**
 * List the manga in a source.
 */
class CatalogueRoute : TachiWebRoute() {

    private val sourceManager: SourceManager = Injekt.get()

    @Throws(Exception::class)
    override fun handleReq(request: Request, response: Response): Any {
        try {
            val sourceId = LeniantParser.parseInteger(request.params(":sourceId"))
            val page = LeniantParser.parseInteger(request.params(":page"))
            val lastUrl = request.queryParams("lurl")
            val query = request.queryParams("query")
            if (sourceId == null) {
                return error("SourceID must be specified!")
            } else if (page == null) {
                return error("Page must be specified!")
            } else if (page > 1 && lastUrl == null) {
                return error("Is not first page but lastURL not specified!")
            }
            val onlineSource: OnlineSource?
            try {
                onlineSource = sourceManager.get(sourceId) as OnlineSource?
            } catch (e: ClassCastException) {
                return error("The specified source is not an OnlineSource!")
            }

            if (onlineSource == null) {
                return error("The specified source does not exist!")
            }
            val pageObj = MangasPage(page)
            if (lastUrl != null) {
                pageObj.url = lastUrl
            } else if (page !== 1) {
                return error("Page is not '1' but no last URL provided!")
            }
            val observable: Observable<MangasPage>
            if (StringUtils.notNullOrEmpty(query)) {
                observable = onlineSource.fetchSearchManga(pageObj, query)
            } else {
                observable = onlineSource.fetchPopularManga(pageObj)
            }
            val result = observable
                    .flatMap { Observable.from<Manga>(it.mangas) }
                    .map{ this.networkToLocalManga(it) }
                    .toList()
                    .toBlocking()
                    .first()
            val toReturn = success()
            val content = JSONArray()
            for (manga in result) {
                val mangaJson = JSONObject()
                mangaJson.put(KEY_ID, manga.id)
                mangaJson.put(KEY_TITLE, manga.title)
                mangaJson.put(KEY_FAVORITE, manga.favorite)
                content.put(mangaJson)
            }
            toReturn.put(KEY_CONTENT, content)
            val nextUrl = pageObj.nextPageUrl
            if (StringUtils.notNullOrEmpty(nextUrl)) {
                toReturn.put(KEY_NEXT_URL, nextUrl)
            }
            return toReturn
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    private fun networkToLocalManga(networkManga: Manga): Manga {
        var localManga: Manga? = library.getManga(networkManga.url, networkManga.source)
        if (localManga == null) {
            networkManga.id = library.insertManga(networkManga)
            localManga = networkManga
        }
        return localManga
    }

    companion object {
        val KEY_CONTENT = "content"
        val KEY_TITLE = "title"
        val KEY_ID = "id"
        val KEY_NEXT_URL = "lurl"
        val KEY_FAVORITE = "favorite"
    }
}