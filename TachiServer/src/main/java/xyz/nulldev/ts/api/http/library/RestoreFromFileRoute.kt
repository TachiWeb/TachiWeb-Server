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

import android.content.Context
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import eu.kanade.tachiyomi.data.backup.BackupManagerInternalForwarder
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.backup.models.Backup
import eu.kanade.tachiyomi.data.backup.models.DHistory
import eu.kanade.tachiyomi.data.database.models.*
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import org.slf4j.LoggerFactory
import rx.Observable
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy
import javax.servlet.MultipartConfigElement

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class RestoreFromFileRoute : TachiWebRoute() {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val context: Context by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any {
        request.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement("/tmp"))
        try {
            request.raw().getPart("uploaded_file").inputStream
                    .use { stream ->
                        val reader = JsonReader(stream.bufferedReader())
                        val json = JsonParser().parse(reader).asJsonObject

                        // Get parser version
                        val version = json.get(Backup.VERSION)?.asInt ?: 1

                        // Initialize manager
                        val backupManager = BackupManager(context, version)
                        val bmForwarder = BackupManagerInternalForwarder(backupManager)

                        val mangasJson = json.get(Backup.MANGAS).asJsonArray

                        // Restore categories
                        json.get(Backup.CATEGORIES)?.let {
                            bmForwarder.restoreCategories(it.asJsonArray)
                        }

                        /**
                         * [Observable] that fetches chapter information
                         *
                         * @param source source of manga
                         * @param manga manga that needs updating
                         * @return [Observable] that contains manga
                         */
                        fun chapterFetchObservable(source: Source, manga: Manga, chapters: List<Chapter>): Observable<Pair<List<Chapter>, List<Chapter>>> {
                            return backupManager.restoreChapterFetchObservable(source, manga, chapters)
                                    // If there's any error, return empty update and continue.
                                    .onErrorReturn {
                                        //TODO Handle error
                                        Pair(emptyList<Chapter>(), emptyList<Chapter>())
                                    }
                        }

                        /**
                         * [Observable] that fetches manga information
                         *
                         * @param manga manga that needs updating
                         * @param chapters chapters of manga that needs updating
                         * @param categories categories that need updating
                         */
                        fun mangaFetchObservable(source: Source, manga: Manga, chapters: List<Chapter>,
                                                 categories: List<String>, history: List<DHistory>,
                                                 tracks: List<Track>): Observable<Manga> {
                            return backupManager.restoreMangaFetchObservable(source, manga)
                                    .onErrorReturn {
                                        //TODO Handle error
                                        manga
                                    }
                                    .filter { it.id != null }
                                    .flatMap { manga ->
                                        chapterFetchObservable(source, manga, chapters)
                                                // Convert to the manga that contains new chapters.
                                                .map { manga }
                                    }
                                    .doOnNext {
                                        // Restore categories
                                        bmForwarder.restoreCategoriesForManga(it, categories)

                                        // Restore history
                                        bmForwarder.restoreHistoryForManga(history)

                                        // Restore tracking
                                        bmForwarder.restoreTrackForManga(it, tracks)
                                    }
                        }

                        fun mangaNoFetchObservable(source: Source, backupManga: Manga, chapters: List<Chapter>,
                                                   categories: List<String>, history: List<DHistory>,
                                                   tracks: List<Track>): Observable<Manga> {

                            return Observable.just(backupManga)
                                    .flatMap { manga ->
                                        if (!bmForwarder.restoreChaptersForManga(manga, chapters)) {
                                            chapterFetchObservable(source, manga, chapters)
                                                    .map { manga }
                                        } else {
                                            Observable.just(manga)
                                        }
                                    }
                                    .doOnNext {
                                        // Restore categories
                                        bmForwarder.restoreCategoriesForManga(it, categories)

                                        // Restore history
                                        bmForwarder.restoreHistoryForManga(history)

                                        // Restore tracking
                                        bmForwarder.restoreTrackForManga(it, tracks)
                                    }
                        }

                        /**
                         * Returns a manga restore observable
                         *
                         * @param manga manga data from json
                         * @param chapters chapters data from json
                         * @param categories categories data from json
                         * @param history history data from json
                         * @param tracks tracking data from json
                         * @return [Observable] containing manga restore information
                         */
                        fun getMangaRestoreObservable(manga: Manga, chapters: List<Chapter>,
                                                      categories: List<String>, history: List<DHistory>,
                                                      tracks: List<Track>): Observable<Manga>? {
                            // Get source
                            val source = sourceManager.get(manga.source) ?: return null
                            val dbManga = bmForwarder.getMangaFromDatabase(manga)

                            if (dbManga == null) {
                                // Manga not in database
                                return mangaFetchObservable(source, manga, chapters, categories, history, tracks)
                            } else { // Manga in database
                                // Copy information from manga already in database
                                backupManager.restoreMangaNoFetch(manga, dbManga)
                                // Fetch rest of manga information
                                return mangaNoFetchObservable(source, manga, chapters, categories, history, tracks)
                            }
                        }

                        return Observable.from(mangasJson)
                                .concatMap {
                                    val obj = it.asJsonObject
                                    val manga = backupManager.parser.fromJson<MangaImpl>(obj.get(Backup.MANGA))
                                    val chapters = backupManager.parser.fromJson<List<ChapterImpl>>(obj.get(Backup.CHAPTERS) ?: JsonArray())
                                    val categories = backupManager.parser.fromJson<List<String>>(obj.get(Backup.CATEGORIES) ?: JsonArray())
                                    val history = backupManager.parser.fromJson<List<DHistory>>(obj.get(Backup.HISTORY) ?: JsonArray())
                                    val tracks = backupManager.parser.fromJson<List<TrackImpl>>(obj.get(Backup.TRACK) ?: JsonArray())

                                    val observable = getMangaRestoreObservable(manga, chapters, categories, history, tracks)
                                    if (observable != null) {
                                        observable
                                    } else {
                                        //TODO Handle error
                                        Observable.just(manga)
                                    }
                                }
                                .toList()
                                .map { success() }
                                .doOnError { error ->
                                    //TODO Handle error
                                }
                                .onErrorReturn { /*emptyList()*/ error("Unknown error!") }
                                .toBlocking()
                                .first()
                    }
        } catch (e: Exception) {
            logger.error("Restore failed!", e)
            return error("Restore failed!")
        }
    }
}

