package eu.kanade.tachiyomi.util

import java.io.File

/**
 * Deletes file if exists
 *
 * @return success of file deletion
 */
fun File.deleteIfExists(): Boolean {
    if (this.exists()) {
        this.delete()
        return true
    }
    return false
}
