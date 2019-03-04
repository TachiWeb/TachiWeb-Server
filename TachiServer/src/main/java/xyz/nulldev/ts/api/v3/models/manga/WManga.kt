package xyz.nulldev.ts.api.v3.models.manga

import xyz.nulldev.ts.api.v3.models.tracking.WTrack

data class WManga(
        val artist: String?,
        val author: String?,
        val categories: List<Long>,
        val description: String?,
        val favorite: Boolean,
        val flags: WMangaFlags,
        val genre: String?,
        val id: Long,
        val initialized: Boolean,
        val lastUpdate: Long,
        val sourceId: Long,
        val status: WMangaStatus,
        val title: String,
        val tracks: List<WTrack>,
        val url: String?,
        val viewer: WMangaViewer
)