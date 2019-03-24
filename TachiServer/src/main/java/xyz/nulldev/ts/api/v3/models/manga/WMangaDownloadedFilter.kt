package xyz.nulldev.ts.api.v3.models.manga

import eu.kanade.tachiyomi.data.database.models.Manga

enum class WMangaDownloadedFilter(override val value: Int) : WMangaFlag {
    SHOW_DOWNLOADED(Manga.SHOW_DOWNLOADED),
    SHOW_NOT_DOWNLOADED(Manga.SHOW_NOT_DOWNLOADED),
    ALL(Manga.SHOW_ALL);

    override val mask = Manga.DOWNLOADED_MASK
}