package xyz.nulldev.ts.api.v3.models.tracking

data class WTrack(
        val displayScore: String?,
        val lastChapterRead: Int,
        val score: Int?,
        val service: Int,
        val status: Int,
        val totalChapters: Int,
        val trackingUrl: String?
)