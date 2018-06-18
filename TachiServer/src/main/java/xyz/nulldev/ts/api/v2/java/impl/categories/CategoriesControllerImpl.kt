package xyz.nulldev.ts.api.v2.java.impl.categories

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Category
import xyz.nulldev.ts.api.v2.java.model.categories.CategoriesController
import xyz.nulldev.ts.api.v2.java.model.categories.CategoryModel
import xyz.nulldev.ts.ext.kInstanceLazy

class CategoriesControllerImpl : CategoriesController {
    private val db: DatabaseHelper by kInstanceLazy()

    override fun get(vararg categoryIds: Int)
            = CategoryCollectionImpl(categoryIds.toList()) // TODO Check these categories exist

    override fun getAll()
            = CategoryCollectionImpl(db.getCategories().executeAsBlocking().map {
        it.id!!
    }.toList())

    override fun add(name: String, order: Int?, flags: Int): CategoryModel {
        if(getAll().any { it.name.equals(name, true) }) {
            error("A category with this name already exists!")
        }

        val newCat = Category.create(name)
        newCat.flags = flags
        newCat.order = order ?: ((getAll().maxBy { it.order ?: 0 }?.order ?: 0) + 1)

        val result = db.insertCategory(newCat).executeAsBlocking()

        return CategoryCollectionImpl(listOf(result.insertedId()!!.toInt())).first()
    }

}