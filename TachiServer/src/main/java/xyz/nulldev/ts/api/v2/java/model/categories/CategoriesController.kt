package xyz.nulldev.ts.api.v2.java.model.categories

interface CategoriesController {
    fun get(vararg categoryIds: Int): CategoryCollection

    fun getAll(): CategoryCollection

    fun add(name: String,
            order: Int? = null,
            flags: Int = 0): CategoryModel
}