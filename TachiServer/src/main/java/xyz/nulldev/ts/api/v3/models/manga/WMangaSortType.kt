package xyz.nulldev.ts.api.v3.models.manga

import eu.kanade.tachiyomi.data.database.models.Manga

enum class WMangaSortType(override val value: Int) : WMangaFlag {
    SOURCE(Manga.SORTING_SOURCE),
    NUMBER(Manga.SORTING_NUMBER);

    override val mask = Manga.SORTING_MASK
}