package xyz.nulldev.ts.api.v3.models.manga

import eu.kanade.tachiyomi.data.database.models.Manga

enum class WMangaBookmarkedFilter(override val value: Int) : WMangaFlag {
    SHOW_BOOKMARKED(Manga.SHOW_BOOKMARKED),
    SHOW_NOT_BOOKMARKED(Manga.SHOW_NOT_BOOKMARKED),
    SHOW_ALL(Manga.SHOW_ALL);

    override val mask = Manga.BOOKMARKED_MASK
}