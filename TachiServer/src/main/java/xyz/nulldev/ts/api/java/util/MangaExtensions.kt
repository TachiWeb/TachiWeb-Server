package xyz.nulldev.ts.api.java.util

import eu.kanade.tachiyomi.data.database.models.Manga
import xyz.nulldev.ts.api.java.TachiyomiAPI

/**
 * Get the source of a manga
 */
val Manga.sourceObj
    get() = TachiyomiAPI.catalogue.getSource(source)

/**
 * Update a manga's info
 */
fun Manga.updateInfo() = TachiyomiAPI.catalogue.updateMangaInfo(this)

/**
 * Update a manga's chapters
 * @returns A list of inserted and deleted chapters
 */
fun Manga.updateChapters() = TachiyomiAPI.catalogue.updateMangaChapters(this)

/**
 * Get a manga's chapters
 */
val Manga.chapters
    get() = TachiyomiAPI.database.getChapters(this).executeAsBlocking()

val Manga.isDownloaded
    get() = TachiyomiAPI.downloads.isDownloaded(this)
