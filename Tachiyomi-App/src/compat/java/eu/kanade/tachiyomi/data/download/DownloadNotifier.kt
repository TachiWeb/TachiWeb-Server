package eu.kanade.tachiyomi.data.download

import android.content.Context
import eu.kanade.tachiyomi.data.download.model.Download
import eu.kanade.tachiyomi.data.download.model.DownloadQueue

/**
 * DownloadNotifier is used to show notifications when downloading one or multiple chapters.
 *
 * @param context context of application
 */
class DownloadNotifier(private val context: Context) {
    /**
     * Status of download. Used for correct notification icon.
     */
    private var isDownloading = false

    /**
     * The size of queue on start download.
     */
    var initialQueueSize = 0
        get() = field
        set(value) {
            if (value != 0){
                isSingleChapter = (value == 1)
            }
            field = value
        }

    /**
     * Simultaneous download setting > 1.
     */
    var multipleDownloadThreads = false

    /**
     * Updated when error is thrown
     */
    var errorThrown = false

    /**
     * Updated when only single page is downloaded
     */
    var isSingleChapter = false

    /**
     * Updated when paused
     */
    var paused = false


    /**
     * Dismiss the downloader's notification. Downloader error notifications use a different id, so
     * those can only be dismissed by the user.
     */
    fun dismiss() {
    }

    /**
     * Called when download progress changes.
     * Note: Only accepted when multi download active.
     *
     * @param queue the queue containing downloads.
     */
    fun onProgressChange(queue: DownloadQueue) {
        if (multipleDownloadThreads) {
            doOnProgressChange(null, queue)
        }
    }

    /**
     * Called when download progress changes.
     * Note: Only accepted when single download active.
     *
     * @param download download object containing download information.
     * @param queue the queue containing downloads.
     */
    fun onProgressChange(download: Download, queue: DownloadQueue) {
        if (!multipleDownloadThreads) {
            doOnProgressChange(download, queue)
        }
    }

    /**
     * Show notification progress of chapter.
     *
     * @param download download object containing download information.
     * @param queue the queue containing downloads.
     */
    private fun doOnProgressChange(download: Download?, queue: DownloadQueue) {
        // Check if first call.
        if (!isDownloading) {
            isDownloading = true
        }
        if (multipleDownloadThreads) {
            // Reset the queue size if the download progress is negative
            if ((initialQueueSize - queue.size) < 0)
                initialQueueSize = queue.size
        }
    }

    /**
     * Show notification when download is paused.
     */
    fun onDownloadPaused() {

        // Reset initial values
        isDownloading = false
        initialQueueSize = 0
    }

    /**
     * Called when chapter is downloaded.
     *
     * @param download download object containing download information.
     */
    fun onDownloadCompleted(download: Download, queue: DownloadQueue) {
        // Check if last download
        if (!queue.isEmpty()) {
            return
        }

        // Reset initial values
        isDownloading = false
        initialQueueSize = 0
    }

    /**
     * Called when the downloader receives a warning.
     *
     * @param reason the text to show.
     */
    fun onWarning(reason: String) {
        // Reset download information
        isDownloading = false
    }

    /**
     * Called when the downloader receives an error. It's shown as a separate notification to avoid
     * being overwritten.
     *
     * @param error string containing error information.
     * @param chapter string containing chapter title.
     */
    fun onError(error: String? = null, chapter: String? = null) {
        // Reset download information
        errorThrown = true
        isDownloading = false
    }
}
