package xyz.nulldev.ts.api.java.model.downloads

import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.source.model.Page

interface DownloadTask {
    /**
     * The chapter being downloaded
     */
    val chapter: Chapter

    /**
     * The pages being downloaded
     */
    val pages: List<Page>?

    /**
     * Download progress 0 to 1
     */
    val progress: Float

    /**
     * The status of the download
     */
    val status: DownloadStatus
}