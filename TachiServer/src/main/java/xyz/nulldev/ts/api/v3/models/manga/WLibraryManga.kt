package xyz.nulldev.ts.api.v3.models.manga

data class WLibraryManga(
        val manga: WManga,
        val totalChapters: Int,
        val totalDownloaded: Int?,
        val totalUnread: Int
)