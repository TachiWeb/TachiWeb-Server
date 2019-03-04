package xyz.nulldev.ts.api.v3.models.manga

import xyz.nulldev.ts.api.v3.models.WSortDirection

data class WMangaFlags(
        val displayMode: WMangaDisplayMode,
        val downloadedFilter: WMangaDownloadedFilter,
        val readFilter: WMangaReadFilter,
        val sortDirection: WSortDirection,
        val sortType: WMangaSortType
)