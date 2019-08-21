package xyz.nulldev.ts.api.v3.models.categories

data class WBatchMutateCategoryRequest(
        val id: Int,
        override val name: String?,
        override val order: Int?
) : WMutateCategoryRequestBase