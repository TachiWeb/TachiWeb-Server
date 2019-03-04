package xyz.nulldev.ts.api.v3.models.tracking

data class WTrackingOAuthLogin(
        val service: Int,
        val status: WTrackingOAuthLoginStatus
)