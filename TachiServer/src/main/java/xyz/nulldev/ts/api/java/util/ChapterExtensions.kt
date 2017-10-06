package xyz.nulldev.ts.api.java.util

import eu.kanade.tachiyomi.data.database.models.Chapter
import xyz.nulldev.ts.api.java.TachiyomiAPI

/**
 * Get any existing download of a chapter
 */
val Chapter.download
    get() = TachiyomiAPI.downloads.downloads.find { it.chapter.id == id }

/**
 * Get the manga of a chapter
 */
val Chapter.manga
    get() = manga_id?.let {
        TachiyomiAPI.database.getManga(it).executeAsBlocking()
    }

val Chapter.isDownloaded
    get() = TachiyomiAPI.downloads.isDownloaded(this)

val Chapter.pageList
    get() = TachiyomiAPI.catalogue.getPageList(this)