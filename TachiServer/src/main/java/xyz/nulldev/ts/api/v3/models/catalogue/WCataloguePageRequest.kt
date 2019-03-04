package xyz.nulldev.ts.api.v3.models.catalogue

data class WCataloguePageRequest(
        val filters: WCatalogueFilters?,
        val page: Int,
        val query: String?
)