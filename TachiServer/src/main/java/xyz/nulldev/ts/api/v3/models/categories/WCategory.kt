package xyz.nulldev.ts.api.v3.models.categories

data class WCategory(
        val id: Long,
        val manga: List<Long>,
        val name: String,
        val order: Long
)