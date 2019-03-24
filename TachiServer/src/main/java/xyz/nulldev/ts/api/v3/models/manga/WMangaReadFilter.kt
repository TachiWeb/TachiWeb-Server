package xyz.nulldev.ts.api.v3.models.manga

import eu.kanade.tachiyomi.data.database.models.Manga

enum class WMangaReadFilter(override val value: Int) : WMangaFlag {
    SHOW_READ(Manga.SHOW_READ),
    SHOW_UNREAD(Manga.SHOW_UNREAD),
    ALL(Manga.SHOW_ALL);

    override val mask = Manga.READ_MASK
}