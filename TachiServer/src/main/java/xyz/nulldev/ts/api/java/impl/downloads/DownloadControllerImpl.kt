package xyz.nulldev.ts.api.java.impl.downloads

import android.content.Context
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.DownloadProvider
import eu.kanade.tachiyomi.data.download.DownloadService
import eu.kanade.tachiyomi.source.SourceManager
import xyz.nulldev.ts.api.java.model.downloads.DownloadController
import xyz.nulldev.ts.api.java.model.downloads.DownloadTask
import xyz.nulldev.ts.api.java.util.*
import xyz.nulldev.ts.ext.kInstanceLazy
import kotlin.reflect.jvm.isAccessible

class DownloadControllerImpl : DownloadController {
    private val sourceManager: SourceManager by kInstanceLazy()
    private val downloadManager: DownloadManager by kInstanceLazy()
    private val context : Context by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()
    internal val downloadProvider by lazy {
        val field = DownloadManager::class.members.find {
            it.name == "provider"
        }!!
        field.isAccessible = true
        field.call(downloadManager) as DownloadProvider
    }

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

        validateOperation(chapter, false)

        DownloadService.start(context)
        downloadManager.downloadChapters(manga, listOf(chapter))
    }

    override fun delete(chapter: Chapter) {
        //Ensure chapter has parent manga
        requireNotNull(chapter.manga_id, { "Manga ID cannot be null!" })

        val manga = db.getManga(chapter.manga_id!!).executeAsBlocking().ensureInDatabase()
        val source = sourceManager.get(manga.source).ensureLoaded()

        validateOperation(chapter, true)

        downloadManager.queue.remove(chapter)
        downloadManager.deleteChapters(listOf(chapter), manga, source)
    }

    private fun validateOperation(chapter: Chapter,
                                  delete: Boolean) {
        //Check for active download
        //TODO Handle other download statuses
        val activeDownload = chapter.download
        if (activeDownload != null) {
            if (delete) {
                throw IllegalStateException("This chapter is currently being downloaded!")
            } else {
                throw IllegalStateException("This chapter is already being downloaded!")
            }
        }

        //Check if chapter is downloaded
        val isChapterDownloaded = isDownloaded(chapter)
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
        return downloadProvider.findChapterDir(chapter, manga, source) != null
    }

    override fun isDownloaded(manga: Manga): Boolean {
        val mangaDirs = downloadProvider.findSourceDir(manga.sourceObj.ensureLoaded())?.listFiles() ?: emptyArray()

        val mangaDirName = downloadProvider.getMangaDirName(manga)
        val mangaDir = mangaDirs.find { it.name == mangaDirName } ?: return false

        return (mangaDir.listFiles() ?: emptyArray()).isNotEmpty()
    }
}