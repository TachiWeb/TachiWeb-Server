package xyz.nulldev.ts.api.v3.models.tracking

data class WMutateTrackRequest(
        val lastChapterRead: Int,
        val score: Int?,
        val status: Int
)