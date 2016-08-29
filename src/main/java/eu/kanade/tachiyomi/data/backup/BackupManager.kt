package eu.kanade.tachiyomi.data.backup

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.*
import com.google.gson.stream.JsonReader
import eu.kanade.tachiyomi.data.backup.serializer.IdExclusion
import eu.kanade.tachiyomi.data.backup.serializer.IntegerSerializer
import eu.kanade.tachiyomi.data.database.models.*
import xyz.nulldev.ts.library.Library
import java.io.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * This class provides the necessary methods to create and restore backups for the data of the
 * application. The backup follows a JSON structure, with the following scheme:
 *
 * {
 *     "mangas": [
 *         {
 *             "manga": {"id": 1, ...},
 *             "chapters": [{"id": 1, ...}, {...}],
 *             "sync": [{"id": 1, ...}, {...}],
 *             "categories": ["cat1", "cat2", ...]
 *         },
 *         { ... }
 *     ],
 *     "categories": [
 *         {"id": 1, ...},
 *         {"id": 2, ...}
 *     ]
 * }
 */
//TODO KEEP THIS UPDATED
class BackupManager() {

    private val MANGA = "manga"
    private val MANGAS = "mangas"
    private val CHAPTERS = "chapters"
    private val MANGA_SYNC = "sync"
    private val CATEGORIES = "categories"

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    private val gson = GsonBuilder()
            .registerTypeAdapter(Integer::class.java, IntegerSerializer())
            .setExclusionStrategies(IdExclusion())
            .create()

    /**
     * Backups the data of the application to a file.
     *
     * @param file the file where the backup will be saved.
     * @throws IOException if there's any IO error.
     */
    @Throws(IOException::class)
    fun backupToFile(file: File, library: Library, favesOnly: Boolean = true) {
        val root = backupToJson(favesOnly, library)

        FileWriter(file).use {
            gson.toJson(root, it)
        }
    }

    fun backupToString(favesOnly: Boolean = true, library: Library): String = gson.toJson(backupToJson(favesOnly, library))

    /**
     * Creates a JSON object containing the backup of the app's data.
     *
     * @return the backup as a JSON object.
     */
    fun backupToJson(favesOnly: Boolean = true, library: Library): JsonObject {
        val lock = ReentrantLock()
        lock.lock()
        library.masterLock.set(lock)
        val root : JsonObject
        try {
            root = JsonObject()

            // Backup library mangas and its dependencies
            val mangaEntries = JsonArray()
            root.add(MANGAS, mangaEntries)
            val toBackup = if(favesOnly)
                library.favoriteMangas
            else
                library.mangas
            for (manga in toBackup) {
                mangaEntries.add(backupManga(manga, library))
            }

            // Backup categories
            val categoryEntries = JsonArray()
            root.add(CATEGORIES, categoryEntries)
            for (category in library.categories) {
                categoryEntries.add(backupCategory(category))
            }
        } catch(e: Throwable) {
            library.masterLock.set(null)
            lock.unlock()
            throw e
        }
        library.masterLock.set(null)
        lock.unlock()

        return root
    }

    /**
     * Backups a manga and its related data (chapters, categories this manga is in, sync...).
     *
     * @param manga the manga to backup.
     * @return a JSON object containing all the data of the manga.
     */
    private fun backupManga(manga: Manga, library: Library): JsonObject {
        // Entry for this manga
        val entry = JsonObject()

        // Backup manga fields
        entry.add(MANGA, gson.toJsonTree(manga))

        // Backup all the chapters
        val chapters = library.getChapters(manga)
        if (!chapters.isEmpty()) {
            entry.add(CHAPTERS, gson.toJsonTree(chapters))
        }

        // Backup manga sync
        val mangaSync = library.getMangasSync(manga)
        if (!mangaSync.isEmpty()) {
            entry.add(MANGA_SYNC, gson.toJsonTree(mangaSync))
        }

        // Backup categories for this manga
        val categoriesForManga = library.getCategoriesForManga(manga)
        if (!categoriesForManga.isEmpty()) {
            val categoriesNames = ArrayList<String>()
            for (category in categoriesForManga) {
                categoriesNames.add(category.name)
            }
            entry.add(CATEGORIES, gson.toJsonTree(categoriesNames))
        }

        return entry
    }

    /**
     * Backups a category.
     *
     * @param category the category to backup.
     * @return a JSON object containing the data of the category.
     */
    private fun backupCategory(category: Category): JsonElement {
        return gson.toJsonTree(category)
    }

    /**
     * Restores a backup from a file.
     *
     * @param file the file containing the backup.
     * @throws IOException if there's any IO error.
     */
    @Throws(IOException::class)
    fun restoreFromFile(file: File, library: Library) {
        JsonReader(FileReader(file)).use {
            val root = JsonParser().parse(it).asJsonObject
            restoreFromJson(root, library)
        }
    }

    /**
     * Restores a backup from an input stream.
     *
     * @param stream the stream containing the backup.
     * @throws IOException if there's any IO error.
     */
    @Throws(IOException::class)
    fun restoreFromStream(stream: InputStream, library: Library) {
        JsonReader(InputStreamReader(stream)).use {
            val root = JsonParser().parse(it).asJsonObject
            restoreFromJson(root, library)
        }
    }

    /**
     * Restores a backup from a JSON object. Everything executes in a single transaction so that
     * nothing is modified if there's an error.
     *
     * @param root the root of the JSON.
     */
    fun restoreFromJson(root: JsonObject, library: Library) {
        val lock = ReentrantLock()
        lock.lock()
        library.masterLock.set(lock)
        try {
            val trans = library.newTransaction()
            // Restore categories
            root.get(CATEGORIES)?.let {
                restoreCategories(it.asJsonArray, trans.library)
            }

            // Restore mangas
            root.get(MANGAS)?.let {
                restoreMangas(it.asJsonArray, trans.library)
            }
            trans.apply()
        } catch(e: Throwable) {
            library.masterLock.set(null)
            lock.unlock()
            throw e
        }
        library.masterLock.set(null)
        lock.unlock()
    }

    /**
     * Restores the categories.
     *
     * @param jsonCategories the categories of the json.
     */
    private fun restoreCategories(jsonCategories: JsonArray, library: Library) {
        // Get categories from file and from db
        val dbCategories = library.categories
        val backupCategories = gson.fromJson<List<CategoryImpl>>(jsonCategories)

        // Iterate over them
        for (category in backupCategories) {
            // Used to know if the category is already in the db
            var found = false
            for (dbCategory in dbCategories) {
                // If the category is already in the db, assign the id to the file's category
                // and do nothing
                if (category.nameLower == dbCategory.nameLower) {
                    category.id = dbCategory.id
                    found = true
                    break
                }
            }
            // If the category isn't in the db, remove the id and insert a new category
            // Store the inserted id in the category
            if (!found) {
                // Let the db assign the id
                category.id = null
                val result = library.insertCategory(category)
                category.id = result
            }
        }
    }

    /**
     * Restores all the mangas and its related data.
     *
     * @param jsonMangas the mangas and its related data (chapters, sync, categories) from the json.
     */
    private fun restoreMangas(jsonMangas: JsonArray, library: Library) {
        for (backupManga in jsonMangas) {
            // Map every entry to objects
            val element = backupManga.asJsonObject
            val manga = gson.fromJson(element.get(MANGA), MangaImpl::class.java)
            val chapters = gson.fromJson<List<ChapterImpl>>(element.get(CHAPTERS) ?: JsonArray())
            val sync = gson.fromJson<List<MangaSyncImpl>>(element.get(MANGA_SYNC) ?: JsonArray())
            val categories = gson.fromJson<List<String>>(element.get(CATEGORIES) ?: JsonArray())

            // Restore everything related to this manga
            restoreManga(manga, library)
            restoreChaptersForManga(manga, chapters, library)
            restoreSyncForManga(manga, sync, library)
            restoreCategoriesForManga(manga, categories, library)
        }
    }

    /**
     * Restores a manga.
     *
     * @param manga the manga to restore.
     */
    private fun restoreManga(manga: Manga, library: Library) {
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
            manga.favorite = true
            library.insertManga(manga)
        }
    }

    /**
     * Restores the chapters of a manga.
     *
     * @param manga the manga whose chapters have to be restored.
     * @param chapters the chapters to restore.
     */
    private fun restoreChaptersForManga(manga: Manga, chapters: List<Chapter>, library: Library) {
        // Fix foreign keys with the current manga id
        for (chapter in chapters) {
            chapter.manga_id = manga.id
        }

        val dbChapters = library.getChapters(manga)
        val chaptersToUpdate = ArrayList<Chapter>()
        for (backupChapter in chapters) {
            // Try to find existing chapter in db
            val pos = dbChapters.indexOf(backupChapter)
            if (pos != -1) {
                // The chapter is already in the db, only update its fields
                val dbChapter = dbChapters[pos]
                // If one of them was read, the chapter will be marked as read
                dbChapter.read = backupChapter.read || dbChapter.read
                dbChapter.last_page_read = Math.max(backupChapter.last_page_read, dbChapter.last_page_read)
                chaptersToUpdate.add(dbChapter)
            } else {
                // Insert new chapter. Let the db assign the id
                backupChapter.id = null
                chaptersToUpdate.add(backupChapter)
            }
        }

        // Update database
        if (!chaptersToUpdate.isEmpty()) {
            library.insertChapters(chaptersToUpdate)
        }
    }

    /**
     * Restores the categories a manga is in.
     *
     * @param manga the manga whose categories have to be restored.
     * @param categories the categories to restore.
     */
    private fun restoreCategoriesForManga(manga: Manga, categories: List<String>, library: Library) {
        val dbCategories = library.categories
        val mangaCategoriesToUpdate = ArrayList<MangaCategory>()
        for (backupCategoryStr in categories) {
            for (dbCategory in dbCategories) {
                if (backupCategoryStr.toLowerCase() == dbCategory.nameLower) {
                    mangaCategoriesToUpdate.add(MangaCategory.create(manga, dbCategory))
                    break
                }
            }
        }

        // Update database
        if (!mangaCategoriesToUpdate.isEmpty()) {
            val mangaAsList = ArrayList<Manga>()
            mangaAsList.add(manga)
            library.deleteOldMangasCategories(mangaAsList)
            library.insertMangasCategories(mangaCategoriesToUpdate)
        }
    }

    /**
     * Restores the sync of a manga.
     *
     * @param manga the manga whose sync have to be restored.
     * @param sync the sync to restore.
     */
    private fun restoreSyncForManga(manga: Manga, sync: List<MangaSync>, library: Library) {
        // Fix foreign keys with the current manga id
        for (mangaSync in sync) {
            mangaSync.manga_id = manga.id!!
        }

        val dbSyncs = library.getMangasSync(manga)
        val syncToUpdate = ArrayList<MangaSync>()
        for (backupSync in sync) {
            // Try to find existing chapter in db
            val pos = dbSyncs.indexOf(backupSync)
            if (pos != -1) {
                // The sync is already in the db, only update its fields
                val dbSync = dbSyncs[pos]
                // Mark the max chapter as read and nothing else
                dbSync.last_chapter_read = Math.max(backupSync.last_chapter_read, dbSync.last_chapter_read)
                syncToUpdate.add(dbSync)
            } else {
                // Insert new sync. Let the db assign the id
                backupSync.id = null
                syncToUpdate.add(backupSync)
            }
        }

        // Update database
        if (!syncToUpdate.isEmpty()) {
            library.insertMangasSync(syncToUpdate)
        }
    }

}
