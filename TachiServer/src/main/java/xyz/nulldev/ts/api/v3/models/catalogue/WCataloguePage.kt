package xyz.nulldev.ts.api.v3.models.catalogue

import xyz.nulldev.ts.api.v3.models.manga.WManga

data class WCataloguePage(
        val hasNextPage: Boolean,
        val mangas: List<WManga>
)