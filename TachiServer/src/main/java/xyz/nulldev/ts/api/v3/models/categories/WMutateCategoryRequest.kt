package xyz.nulldev.ts.api.v3.models.categories

data class WMutateCategoryRequest(
        val name: String,
        val order: Long?
)