package xyz.nulldev.ts.api.java.model.downloads

import eu.kanade.tachiyomi.data.database.models.Chapter

interface DownloadController {
    /**
     * Whether or not the downloader is running
     */
    var running: Boolean

    /**
     * A list of downloads in the queue
     */
    val downloads: List<DownloadTask>

    /**
     * Add a chapter to the download queue and start the downloader
     */
    fun add(chapter: Chapter)

    /**
     * Delete a chapter from the download queue
     */
    fun delete(chapter: Chapter)

    /**
     * Clear the download queue and stop the downloader
     */
    fun clear()

    /**
     * Check if a chapter is downloaded
     *
     * @param chapter The chapter to check
     * @return Whether or not the chapter is downloaded
     */
    fun isDownloaded(chapter: Chapter): Boolean
}