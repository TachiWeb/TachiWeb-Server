package xyz.nulldev.ts.api.v3.models.manga

data class WLibraryManga(
        val lastReadIndex: Int?,
        val manga: WManga,
        val totalChaptersIndex: Int?,
        val totalDownloaded: Int?,
        val totalUnread: Int
)