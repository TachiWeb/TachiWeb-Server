package xyz.nulldev.ts.api.v3.models.tracking

data class WTrackingService(
        val id: Int,
        val loggedIn: Boolean,
        val name: String,
        val oauthUrl: String?,
        val possibleScores: List<WTrackingScore>,
        val possibleStatuses: List<WTrackingStatus>,
        val themeColor: String,
        val username: String?
)