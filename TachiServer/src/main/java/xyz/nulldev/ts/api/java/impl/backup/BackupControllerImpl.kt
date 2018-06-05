package xyz.nulldev.ts.api.java.impl.backup

import android.content.Context
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.set
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import eu.kanade.tachiyomi.data.backup.BackupCreateService
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.backup.BackupManagerInternalForwarder
import eu.kanade.tachiyomi.data.backup.models.Backup
import eu.kanade.tachiyomi.data.backup.models.DHistory
import eu.kanade.tachiyomi.data.database.models.*
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import rx.Observable
import xyz.nulldev.ts.api.java.TachiyomiAPI
import xyz.nulldev.ts.api.java.model.backup.BackupController
import xyz.nulldev.ts.ext.kInstanceLazy
import java.io.InputStream

class BackupControllerImpl : BackupController {
    private val backupManager: BackupManager by kInstanceLazy()
    private val sourceManager: SourceManager by kInstanceLazy()
    private val context: Context by kInstanceLazy()

    private val jsonParser = JsonParser()

    private val bmForwarder by lazy {
        BackupManagerInternalForwarder(backupManager)
    }

    override fun backup(includeCategories: Boolean,
                        includeChapters: Boolean,
                        includeHistory: Boolean,
                        includeTracking: Boolean): String {
        // Assemble backup flags
        var options = 0
        if(includeCategories)
            options = options or BackupCreateService.BACKUP_CATEGORY
        if(includeChapters)
            options = options or BackupCreateService.BACKUP_CHAPTER
        if(includeHistory)
            options = options or BackupCreateService.BACKUP_HISTORY
        if(includeTracking)
            options = options or BackupCreateService.BACKUP_TRACK

        // Create root object
        val root = JsonObject()

        // Create information object
        val information = JsonObject()

        // Create manga array
        val mangaEntries = JsonArray()

        // Create category array
        val categoryEntries = JsonArray()

        // Add value's to root
        root[Backup.VERSION] = Backup.CURRENT_VERSION
        root[Backup.MANGAS] = mangaEntries
        root[Backup.CATEGORIES] = categoryEntries

        TachiyomiAPI.database.inTransaction {
            // Get manga from database
            val mangas = bmForwarder.getFavoriteManga()

            // Backup library manga and its dependencies
            mangas.forEach { manga ->
                mangaEntries.add(bmForwarder.backupMangaObject(manga, options))
            }

            // Backup categories
            bmForwarder.backupCategories(categoryEntries)
        }

        return backupManager.parser.toJson(root)
    }

    override fun restore(backup: String) {
        val json = jsonParser.parse(backup).asJsonObject
        restore(json)
    }

    override fun restore(backup: InputStream) {
        val reader = JsonReader(backup.bufferedReader())
        val json = jsonParser.parse(reader).asJsonObject
        restore(json)
    }

    private fun restore(json: JsonObject) {
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

        val restored = Observable.from(mangasJson)
                .concatMap {
                    val obj = it.asJsonObject
                    val manga = backupManager.parser.fromJson<MangaImpl>(obj.get(Backup.MANGA))
                    val chapters = backupManager.parser.fromJson<List<ChapterImpl>>(obj.get(Backup.CHAPTERS) ?: JsonArray())
                    val categories = backupManager.parser.fromJson<List<String>>(obj.get(Backup.CATEGORIES) ?: JsonArray())
                    val history = backupManager.parser.fromJson<List<DHistory>>(obj.get(Backup.HISTORY) ?: JsonArray())
                    val tracks = backupManager.parser.fromJson<List<TrackImpl>>(obj.get(Backup.TRACK) ?: JsonArray())

                    val observable = getMangaRestoreObservable(manga, chapters, categories, history, tracks)
                    observable ?: Observable.just(manga)
                }.toBlocking().iterator.forEach {} // Go through all restores
    }
}