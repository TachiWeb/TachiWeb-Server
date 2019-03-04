package xyz.nulldev.ts.api.v3.models

data class WError(
        val message: String,
        val stackTrace: String?,
        val type: String
)