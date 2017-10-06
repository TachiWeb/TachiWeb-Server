package xyz.nulldev.ts.api.java.impl.categories

import xyz.nulldev.ts.api.java.TachiyomiAPI
import xyz.nulldev.ts.api.java.model.categories.CategoriesEditOperation
import xyz.nulldev.ts.api.java.model.categories.Category

class CategoriesEditOperationImpl(private val categories: MutableList<Category>,
                                  private val parent: CategoriesImpl,
                                  private val editSnapshot: Long)
    : CategoriesEditOperation, MutableList<Category> by categories {
    private var saved = false

    override fun createCategory(name: String) = CategoryImpl(name, true)

    @Synchronized
    override fun save() {
        //Saving twice is not supported
        if(saved)
            throw IllegalStateException("This operation has already been committed!")

        //Only one operation committed at the same time
        synchronized(parent, {
            //Ensure that the parent categories have not changed since this edit operation was opened
            if(editSnapshot != parent.editIndex)
                throw IllegalStateException("The categories have changed since this operation was opened!")

            TachiyomiAPI.database.inTransaction {
                //Transform original categories list into lookup map
                val categoriesMap = TachiyomiAPI.database
                        .getCategories()
                        .executeAsBlocking()
                        .associateBy { it.id }
                        .toMutableMap()

                val newCategories = categories.mapIndexed { index, category ->
                    //Do not save custom implementations of Category
                    category as? CategoryImpl
                        ?: throw IllegalStateException("Custom implementations of categories cannot be used!")

                    val newCategory = categoriesMap.remove(category.id)
                            ?: eu.kanade.tachiyomi.data.database.models.Category.create(category.name)

                    newCategory.order = index

                    newCategory
                }

                //Apply changes to DB
                //Delete deleted categories
                TachiyomiAPI.database.deleteCategories(categoriesMap.values.toList())
                TachiyomiAPI.database.insertCategories(newCategories)
            }

            //Update edit snapshot and operation status
            parent.editIndex++
            saved = true
        })
    }
}