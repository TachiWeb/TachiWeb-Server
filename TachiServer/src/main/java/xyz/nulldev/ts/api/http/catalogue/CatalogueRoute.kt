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
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonParser
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SManga
import org.json.JSONArray
import org.slf4j.LoggerFactory
import rx.Observable
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.api.http.serializer.FilterSerializer
import xyz.nulldev.ts.api.http.serializer.MangaSerializer
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * List the manga in a source.
 */
class CatalogueRoute : TachiWebRoute() {

    private val sourceManager: SourceManager by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()
    private val mangaSerializer: MangaSerializer by kInstanceLazy()
    private val filtersSerializer: FilterSerializer by kInstanceLazy()
    private val jsonParser: JsonParser by kInstanceLazy()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        try {
            //Get/parse parameters
            val parsedBody = jsonParser.parse(request.body()).nullObj
                    ?: return error("Invalid request!")

            val sourceId = parsedBody["sourceId"].nullLong
                    ?: return error("SourceID must be specified!")
            val page = parsedBody["page"].nullInt
                    ?: return error("Page must be specified!")
            val query = parsedBody["query"].nullString
            val filters = parsedBody["filters"].nullArray

            //Try to resolve source
            val catalogueSource = sourceManager.get(sourceId)
                    ?: return error("The specified source does not exist!")
            if(catalogueSource !is CatalogueSource) {
                return error("The specified source is not a CatalogueSource!")
            }

            //Parse filters
            val filtersObj = catalogueSource.getFilterList()
            filters?.let {
                filtersSerializer.deserialize(filtersObj, it)
            }

            //Get catalogue from source
            val observable: Observable<MangasPage>
            if (!query.isNullOrEmpty() || filters != null) {
                observable = catalogueSource.fetchSearchManga(page, query ?: "", filtersObj)
            } else {
                observable = catalogueSource.fetchPopularManga(page)
            }
            //Actually get manga from catalogue
            val pageObj = observable.toBlocking().first()

            val result = pageObj.mangas.map { networkToLocalManga(it, sourceId) }

            //Generate JSON response
            val toReturn = success()
            val content = JSONArray()
            for (manga in result) {
                content.put(mangaSerializer.serialize(manga, false))
            }
            toReturn.put(KEY_CONTENT, content)
            toReturn.put(KEY_HAS_NEXT_URL, pageObj.hasNextPage)

            return toReturn
        } catch (e: Exception) {
            logger.error("Exception when serving catalogue!", e)
            throw e
        }

    }

    private fun networkToLocalManga(sManga: SManga, sourceId: Long): Manga {
        var localManga = db.getManga(sManga.url, sourceId).executeAsBlocking()
        if (localManga == null) {
            val newManga = Manga.create(sManga.url, sManga.title, sourceId)
            newManga.copyFrom(sManga)
            val result = db.insertManga(newManga).executeAsBlocking()
            newManga.id = result.insertedId()
            localManga = newManga
        }
        return localManga
    }

    companion object {
        val KEY_CONTENT = "content"
        val KEY_HAS_NEXT_URL = "has_next"
    }
}