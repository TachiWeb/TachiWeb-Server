package eu.kanade.tachiyomi.data.backup

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hippo.unifile.UniFile
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.data.backup.models.Backup
import timber.log.Timber

/**
 * [IntentService] used to backup [Manga] information to [JsonArray]
 */
class BackupCreateService {
    companion object {
        // Name of class
        private const val NAME = "BackupCreateService"

        // Backup called from job
        private const val EXTRA_IS_JOB = "${BuildConfig.APPLICATION_ID}.$NAME.EXTRA_IS_JOB"
        // Options for backup
        private const val EXTRA_FLAGS = "${BuildConfig.APPLICATION_ID}.$NAME.EXTRA_FLAGS"

        // Filter options
        const val BACKUP_CATEGORY = 0x1
        const val BACKUP_CATEGORY_MASK = 0x1
        const val BACKUP_CHAPTER = 0x2
        const val BACKUP_CHAPTER_MASK = 0x2
        const val BACKUP_HISTORY = 0x4
        const val BACKUP_HISTORY_MASK = 0x4
        const val BACKUP_TRACK = 0x8
        const val BACKUP_TRACK_MASK = 0x8
        const val BACKUP_ALL = 0xF
    }
}