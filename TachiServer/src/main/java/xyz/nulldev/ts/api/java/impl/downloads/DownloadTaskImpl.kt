package xyz.nulldev.ts.api.java.impl.downloads

import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.download.model.Download
import eu.kanade.tachiyomi.source.model.Page
import xyz.nulldev.ts.api.java.model.downloads.DownloadStatus
import xyz.nulldev.ts.api.java.model.downloads.DownloadTask

class DownloadTaskImpl(private val download: Download) : DownloadTask {
    override val chapter: Chapter
        get() = download.chapter

    override val pages: List<Page>?
        get() = download.pages

    /**
     * Download progress, a float between 0 and 1
     */
    override val progress: Float
        get() {
            val pages = pages

            // Calculate download progress
            val downloadProgressMax: Float
            val downloadProgress: Float
            if(pages != null) {
                downloadProgressMax = pages.size * 100f
                downloadProgress = pages
                        .map { it.progress.toFloat() }
                        .sum()
            } else {
                downloadProgressMax = 1f
                downloadProgress = 0f
            }

            return downloadProgress / downloadProgressMax
        }

    override val status: DownloadStatus
        get() = when(download.status) {
            Download.NOT_DOWNLOADED -> DownloadStatus.NOT_DOWNLOADED
            Download.QUEUE -> DownloadStatus.QUEUE
            Download.DOWNLOADING -> DownloadStatus.DOWNLOADING
            Download.DOWNLOADED -> DownloadStatus.DOWNLOADED
            Download.ERROR -> DownloadStatus.ERROR
            else -> throw IllegalStateException("Unknown download status: ${download.status}!")
        }

}