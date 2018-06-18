package xyz.nulldev.ts.api.v2.http.categories

import xyz.nulldev.ts.api.v2.java.model.categories.CategoryModel

data class CategoryName(val id: Int,
                        val name: String?)

data class CategoryOrder(val id: Int,
                         val order: Int?)

data class CategoryFlags(val id: Int,
                         val flags: Int?)

data class CategoryManga(val id: Int,
                         val manga: List<Long>?)

data class CategoryTemplate(val name: String,
                            val order: Int?,
                            val flags: Int?)

data class SerializableCategoryModel(
    val id: Int,
    var name: String?,
    var order: Int?,
    var flags: Int?,
    var manga: List<Long>?
) {
    constructor(category: CategoryModel):
            this(category.id,
                    category.name,
                    category.order,
                    category.flags,
                    category.manga?.map { it.id!! })
}
