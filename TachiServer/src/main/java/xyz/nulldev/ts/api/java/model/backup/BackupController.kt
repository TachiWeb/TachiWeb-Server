package xyz.nulldev.ts.api.java.model.backup

import java.io.InputStream

interface BackupController {
    /**
     * Backup the library to a JSON string
     * @param includeCategories Whether or not to include categories in the backup
     * @param includeChapters Whether or not to include chapters in the backup
     * @param includeHistory Whether or not to include history in the backup
     * @param includeTracking Whether or not to include tracking in the backup
     * @return The JSON string
     */
    fun backup(includeCategories: Boolean = true,
               includeChapters: Boolean = true,
               includeHistory: Boolean = true,
               includeTracking: Boolean = true): String

    /**
     * Restore a backup from a JSON string
     * @param backup The JSON string
     */
    fun restore(backup: String)

    /**
     * Restore a backup from a JSON stream
     * @param backup The JSON stream
     */
    fun restore(backup: InputStream)
}