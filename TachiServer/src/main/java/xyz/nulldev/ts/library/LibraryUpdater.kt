package xyz.nulldev.ts.library

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.util.syncChaptersWithSource
import mu.KotlinLogging
import xyz.nulldev.ts.ext.kInstanceLazy
import java.util.*

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

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 16/08/16
 */
class LibraryUpdater {

    val logger = KotlinLogging.logger {}

    val sourceManager: SourceManager by kInstanceLazy()

    private val db: DatabaseHelper by kInstanceLazy()

    fun updateLibrary(updateAll: Boolean) {
        if(updateAll) {
            db.getMangas()
        } else {
            db.getFavoriteMangas()
        }.executeAsBlocking().forEach {
            silentUpdateMangaInfo(it)
            silentUpdateChapters(it)
        }
    }

    fun silentUpdateMangaInfo(manga: Manga) {
        val source = sourceManager.get(manga.source)
        if(source == null) {
            logger.warn { "Manga ${manga.id} is missing it's source!" }
            return
        }
        try {
            updateMangaInfo(manga, source)
        } catch (e: Exception) {
            logger.error("Error updating manga!", e)
        }
    }

    fun updateMangaInfo(manga: Manga, source: Source) {
        manga.copyFrom(source.fetchMangaDetails(manga).toBlocking().first())
        db.insertManga(manga).executeAsBlocking()
    }

    fun silentUpdateChapters(manga: Manga): Pair<List<Chapter>, List<Chapter>> {
        val source = sourceManager.get(manga.source)
        if(source == null) {
            logger.warn { "Manga ${manga.id} is missing it's source!" }
            return Pair(emptyList(), emptyList())
        }
        try {
            return updateChapters(manga, source).apply {
                //If we find new chapters, update the "last update" field in the manga object
                if(first.isNotEmpty() || second.isNotEmpty()) {
                    manga.last_update = Date().time
                    db.updateLastUpdated(manga).executeAsBlocking()
                }
            }
        } catch (e: Exception) {
            logger.error("Error updating chapters!", e)
            return Pair(emptyList(), emptyList())
        }
    }

    fun updateChapters(manga: Manga, source: Source): Pair<List<Chapter>, List<Chapter>> {
        return syncChaptersWithSource(db,
                source.fetchChapterList(manga).toBlocking().first(),
                manga,
                source)
    }
}
