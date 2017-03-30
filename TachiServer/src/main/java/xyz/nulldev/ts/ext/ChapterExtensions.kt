package xyz.nulldev.ts.ext

import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.source.online.fetchPageListFromCacheThenNet

private val downloadManager: DownloadManager by kInstanceLazy()

fun Chapter.isDownloaded(source: Source, manga: Manga) = downloadManager.findChapterDir(source, manga, this) != null

fun Chapter.getPageList(source: Source, manga: Manga): List<Page> {
    return if (isDownloaded(source, manga)) {
        // Fetch the page list from disk.
        downloadManager.buildPageList(source, manga, this)
    } else {
        (source as? HttpSource)?.fetchPageListFromCacheThenNet(this)
                ?: source.fetchPageList(this)
    }.toBlocking().first()
}

fun DownloadManager.getDownload(chapter: Chapter)
    = queue.find { it.chapter.id == chapter.id }
