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
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * List the manga in a source.
 */
class CatalogueRoute : TachiWebRoute() {

    private val sourceManager: SourceManager = Injekt.get()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        try {
            //Get/parse parameters
            val sourceId = request.params(":sourceId")?.toInt()
                    ?: return error("SourceID must be specified!")
            val page = request.params(":page")?.toInt()
                    ?: return error("Page must be specified!")
            val lastUrl = request.queryParams("lurl")
            val query = request.queryParams("query")
            if (page > 1 && lastUrl == null) {
                return error("Is not first page but lastURL not specified!")
            }

            //Try to resolve source
            val onlineSource = sourceManager.get(sourceId)
                    ?: return error("The specified source does not exist!")
            if(onlineSource !is OnlineSource) {
                return error("The specified source is not an OnlineSource!")
            }

            //Parse filters
            val filters = request.queryParamsValues("filter").map { id ->
                return onlineSource.getFilterList().find {
                    it.id == id
                } ?: return@map error("'$id' is not a valid filter ID!")
            }

            //Parse page
            val pageObj = MangasPage(page)
            if (lastUrl != null) {
                pageObj.url = lastUrl
            } else if (page !== 1) {
                return error("Page is not '1' but no last URL provided!")
            }
            //Get catalogue from source
            val observable: Observable<MangasPage>
            if (!query.isNullOrEmpty()) {
                observable = onlineSource.fetchSearchManga(pageObj, query, filters)
            } else {
                observable = onlineSource.fetchPopularManga(pageObj)
            }
            //Actually get manga from catalogue
            val result = observable
                    .flatMap { Observable.from<Manga>(it.mangas) }
                    .map{ this.networkToLocalManga(it) }
                    .toList()
                    .toBlocking()
                    .first()

            //Generate JSON response
            val toReturn = success()
            val content = JSONArray()
            for (manga in result) {
                content.put(JSONObject()
                        .put(KEY_ID, manga.id)
                        .put(KEY_TITLE, manga.title)
                        .put(KEY_FAVORITE, manga.favorite))
            }
            toReturn.put(KEY_CONTENT, content)
            val nextUrl = pageObj.nextPageUrl
            if (!nextUrl.isNullOrEmpty()) {
                toReturn.put(KEY_NEXT_URL, nextUrl)
            }
            return toReturn
        } catch (e: Exception) {
            logger.error("Exception when serving catalogue!", e)
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