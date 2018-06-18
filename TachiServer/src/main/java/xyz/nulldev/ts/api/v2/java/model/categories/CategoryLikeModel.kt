package xyz.nulldev.ts.api.v2.java.model.categories

interface CategoryLikeModel {
    val id: Any

    val name: Any?

    val order: Any?

    val flags: Any?

    val manga: Any?

    fun delete()
}