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

package xyz.nulldev.ts.api.http.sync

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.database.models.ChapterImpl
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.database.models.MangaCategory
import eu.kanade.tachiyomi.data.database.models.MangaImpl
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import uy.kohesive.injekt.injectLazy
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.library.Library
import xyz.nulldev.ts.sync.LibraryDiff
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 29/11/16
 */

class DiffSyncRoute : TachiWebRoute() {

    private val backupManager: BackupManager by injectLazy()

    private val gson by lazy { GsonBuilder().create() }
    private val jsonParser by lazy { JsonParser() }

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any? {
        //Response is always UTF_8
        response.raw().characterEncoding = StandardCharsets.UTF_8.name()

        //Parse diff
        val requestBody = String(request.bodyAsBytes(), StandardCharsets.UTF_8)
        val root = jsonParser.parse(requestBody).asJsonObject!! //TODO Replace with error

        val trans = library.newTransaction()

        //Insert modified categories
        root.get(LibraryDiff::modifiedCategories.name)?.let {
            backupManager.restoreCategories(it.asJsonArray, trans.library)
        }

        //Remove removed categories
        root.get(LibraryDiff::removedCategories.name)?.let {
            for(category in it.asJsonArray) {
                trans.library.deleteCategory(category.asString)
            }
        }

        //Insert modified manga
        root.get(LibraryDiff::modifiedManga.name)?.let {
            for(manga in it.asJsonArray) {
                restoreManga(gson.fromJson(manga, MangaImpl::class.java), trans.library)
            }
        }

        //Insert modified chapters
        root.get(LibraryDiff::modifiedChapters.name)?.let {
            for(modifiedChapter in it.asJsonArray) {
                val pair = pairFromJson(modifiedChapter)
                val manga = gson.fromJson(pair.first, LibraryDiff.MangaReference::class.java).findInLibrary(trans.library)
                if(manga != null) {
                    backupManager.restoreChaptersForManga(manga, listOf(gson.fromJson(pair.second, ChapterImpl::class.java)), trans.library)
                }
            }
        }

        //Insert modified manga categories
        root.get(LibraryDiff::addedMangaCategoryMappings.name)?.let {
            val toInsert = ArrayList<MangaCategory>()
           for(mangaCategoryPair in it.asJsonArray) {
               toInsert += pairToMangaCategory(pairFromJson(mangaCategoryPair), trans.library) ?: continue
           }
            trans.library.insertMangasCategories(toInsert)
        }

        //Delete old manga categories
        root.get(LibraryDiff::removedMangaCategoryMappings.name)?.let {
            val toDelete = ArrayList<MangaCategory>()
            for(mangaCategoryPair in it.asJsonArray) {
                toDelete += pairToMangaCategory(pairFromJson(mangaCategoryPair), trans.library) ?: continue
            }
            trans.library.deleteMangaCategories(toDelete)
        }
        trans.apply()
        //TODO Return data
        return success()
    }

    fun pairToMangaCategory(pair: Pair<JsonElement, JsonElement>, library: Library): MangaCategory? {
           //Find category
           val cat = library.findCategory(pair.first.string) ?: return null
           //Find manga
           val manga = gson.fromJson(pair.second, LibraryDiff.MangaReference::class.java).findInLibrary(library) ?: return null
           return MangaCategory.create(manga, cat)
    }

    fun LibraryDiff.MangaReference.findInLibrary(library: Library) = library.getManga(this.url, this.source)

    fun pairFromJson(element: JsonElement)
            = Pair(element[Pair<JsonElement, JsonElement>::first.name],
                   element[Pair<JsonElement, JsonElement>::second.name])

    fun restoreManga(manga: Manga, library: Library) {
        // Try to find existing manga in db
        val dbManga = library.getManga(manga.url, manga.source)
        if (dbManga == null) {
            // Let the db assign the id
            manga.id = null
            val result = library.insertManga(manga)
            manga.id = result
        } else {
            // If it exists already, we copy only the values related to the source from the db
            // (they can be up to date). Local values (flags) are kept from the backup.
            manga.id = dbManga.id
            manga.copyFrom(dbManga)
            library.insertManga(manga)
        }
    }
}