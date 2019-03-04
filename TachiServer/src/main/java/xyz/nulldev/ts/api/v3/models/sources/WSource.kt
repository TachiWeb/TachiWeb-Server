package xyz.nulldev.ts.api.v3.models.sources

data class WSource(
        val id: String,
        val lang: String,
        val langDisplayName: String,
        val langName: String,
        val loggedIn: Boolean?,
        val name: String,
        val requiresLogin: Boolean,
        val supportsLatest: Boolean
)