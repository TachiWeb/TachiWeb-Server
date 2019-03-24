package xyz.nulldev.ts.api.v3.models

import eu.kanade.tachiyomi.data.database.models.Manga
import xyz.nulldev.ts.api.v3.models.manga.WMangaFlag

enum class WSortDirection(override val value: Int) : WMangaFlag {
    ASC(Manga.SORT_ASC),
    DESC(Manga.SORT_DESC);

    override val mask = Manga.SORT_MASK
}