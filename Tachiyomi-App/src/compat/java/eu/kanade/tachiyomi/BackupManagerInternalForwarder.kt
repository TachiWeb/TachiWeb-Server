package eu.kanade.tachiyomi

import com.google.gson.JsonArray
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.backup.models.DHistory
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.database.models.Track

/**
 * Expose BackupManager's internal methods
 */

class BackupManagerInternalForwarder(private val backupManager: BackupManager) {
    fun getFavoriteManga()
            = backupManager.getFavoriteManga()

    fun backupMangaObject(manga: Manga, options: Int)
            = backupManager.backupMangaObject(manga, options)

    fun backupCategories(root: JsonArray)
            = backupManager.backupCategories(root)

    fun restoreCategories(jsonCategories: JsonArray)
            = backupManager.restoreCategories(jsonCategories)

    fun getMangaFromDatabase(manga: Manga)
            = backupManager.getMangaFromDatabase(manga)

    fun restoreCategoriesForManga(manga: Manga, categories: List<String>)
            = backupManager.restoreCategoriesForManga(manga, categories)

    fun restoreHistoryForManga(history: List<DHistory>)
            = backupManager.restoreHistoryForManga(history)

    fun restoreTrackForManga(manga: Manga, tracks: List<Track>)
            = backupManager.restoreTrackForManga(manga, tracks)

    fun restoreChaptersForManga(manga: Manga, chapters: List<Chapter>)
            = backupManager.restoreChaptersForManga(manga, chapters)
}