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

package eu.kanade.tachiyomi.data.mangasync.anilist.model

import eu.kanade.tachiyomi.data.database.models.MangaSync
import eu.kanade.tachiyomi.data.mangasync.MangaSyncManager

data class ALManga(
        val id: Int,
        val title_romaji: String,
        val type: String,
        val total_chapters: Int) {

    fun toMangaSync() = MangaSync.create(MangaSyncManager.ANILIST).apply {
        remote_id = this@ALManga.id
        title = title_romaji
        total_chapters = this@ALManga.total_chapters
    }
}