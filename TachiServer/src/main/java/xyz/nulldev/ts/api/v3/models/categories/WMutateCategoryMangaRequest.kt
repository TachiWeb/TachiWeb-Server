package xyz.nulldev.ts.api.v3.models.categories

data class WMutateCategoryMangaRequest(
        val add: List<Long>,
        val remove: List<Long>
)