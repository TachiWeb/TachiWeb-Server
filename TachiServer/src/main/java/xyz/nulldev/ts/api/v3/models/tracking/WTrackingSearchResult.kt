package xyz.nulldev.ts.api.v3.models.tracking

data class WTrackingSearchResult(
        val chapter: Int?,
        val coverUrl: String?,
        val extra: String?,
        val service: Int,
        val startDate: String?,
        val status: String?,
        val summary: String?,
        val title: String,
        val trackingUrl: String?,
        val type: String?
)