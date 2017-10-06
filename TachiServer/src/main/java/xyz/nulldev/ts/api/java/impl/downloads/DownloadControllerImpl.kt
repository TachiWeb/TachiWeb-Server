package xyz.nulldev.ts.api.java.impl.downloads

import android.content.Context
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.DownloadService
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import xyz.nulldev.ts.api.java.model.downloads.DownloadController
import xyz.nulldev.ts.api.java.model.downloads.DownloadTask
import xyz.nulldev.ts.api.java.util.ensureInDatabase
import xyz.nulldev.ts.api.java.util.ensureLoaded
import xyz.nulldev.ts.api.java.util.manga
import xyz.nulldev.ts.api.java.util.sourceObj
import xyz.nulldev.ts.ext.getDownload
import xyz.nulldev.ts.ext.isDownloaded
import xyz.nulldev.ts.ext.kInstanceLazy

class DownloadControllerImpl : DownloadController {
    private val sourceManager: SourceManager by kInstanceLazy()
    private val downloadManager: DownloadManager by kInstanceLazy()
    private val context : Context by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    override var running: Boolean
        get() = downloadManager.runningRelay.value
        set(value) {
            //Check if this is already the current value
            if(downloadManager.runningRelay.value == value)
                return

            if(value)
                DownloadService.start(context)
            else
                DownloadService.stop(context)
        }

    override val downloads: List<DownloadTask>
        get() = downloadManager.queue.map { DownloadTaskImpl(it) }

    override fun add(chapter: Chapter) {
        //Ensure chapter has parent manga
        requireNotNull(chapter.manga_id, { "Manga ID cannot be null!" })

        val manga = db.getManga(chapter.manga_id!!).executeAsBlocking().ensureInDatabase()
        val source = sourceManager.get(manga.source).ensureLoaded()

        validateOperation(chapter, manga, source, false)

        DownloadService.start(context)
        downloadManager.downloadChapters(manga, listOf(chapter))
    }

    override fun delete(chapter: Chapter) {
        //Ensure chapter has parent manga
        requireNotNull(chapter.manga_id, { "Manga ID cannot be null!" })

        val manga = db.getManga(chapter.manga_id!!).executeAsBlocking().ensureInDatabase()
        val source = sourceManager.get(manga.source).ensureLoaded()

        validateOperation(chapter, manga, source, true)

        downloadManager.queue.remove(chapter)
        downloadManager.deleteChapter(source, manga, chapter)
    }

    private fun validateOperation(chapter: Chapter,
                                  manga: Manga,
                                  source: Source,
                                  delete: Boolean) {
        //Check for active download
        //TODO Handle other download statuses
        val activeDownload = downloadManager.getDownload(chapter)
        if (activeDownload != null) {
            if (delete) {
                throw IllegalStateException("This chapter is currently being downloaded!")
            } else {
                throw IllegalStateException("This chapter is already being downloaded!")
            }
        }

        //Check if chapter is downloaded
        val isChapterDownloaded = chapter.isDownloaded(source, manga)
        if (!delete && isChapterDownloaded) {
            throw IllegalStateException("This chapter is already downloaded!")
        }
        if (delete && !isChapterDownloaded) {
            throw IllegalStateException("This chapter is not downloaded!")
        }
    }

    override fun clear() {
        DownloadService.stop(context)
        downloadManager.clearQueue()
    }

    override fun isDownloaded(chapter: Chapter): Boolean {
        val manga = chapter.manga.ensureInDatabase()
        val source = manga.sourceObj.ensureLoaded()
        return downloadManager.findChapterDir(source, manga, chapter) != null
    }
}