package xyz.nulldev.ts.api.java.impl.categories

import xyz.nulldev.ts.api.java.TachiyomiAPI
import xyz.nulldev.ts.api.java.impl.util.LazyList
import xyz.nulldev.ts.api.java.model.categories.Categories
import xyz.nulldev.ts.api.java.model.categories.Category

// Required for delegate
private val categories = LazyList({
    TachiyomiAPI.database.getCategories().executeAsBlocking().map {
        CategoryImpl(it.name, true, it.id)
    }
})

class CategoriesImpl : Categories, List<Category> by categories {
    internal var editIndex = 0L

    override fun edit() = CategoriesEditOperationImpl(toMutableList(), this, editIndex)
}