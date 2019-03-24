package xyz.nulldev.ts.api.v3.models.manga

import eu.kanade.tachiyomi.data.database.models.Manga

enum class WMangaDisplayMode(override val value: Int) : WMangaFlag {
    NAME(Manga.DISPLAY_NAME),
    NUMBER(Manga.DISPLAY_NUMBER);

    override val mask = Manga.DISPLAY_MASK
}