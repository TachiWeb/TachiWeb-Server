package xyz.nulldev.ts.api.http.library

import com.fasterxml.jackson.databind.ObjectMapper
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Category
import org.json.JSONObject
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Edit categories
 */

class EditCategoriesRoute : TachiWebRoute() {

    private val db: DatabaseHelper by kInstanceLazy()
    private val mapper: ObjectMapper by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any? {
        val operation = request.params(":operation")
                ?: return error("No operation specified!")
        val enum = try {
            CategoryOperation.valueOf(operation.toUpperCase())
        } catch(e: IllegalArgumentException) {
            return error("Invalid operation!")
        }

        when(enum) {
            CategoryOperation.REORDER -> {
                val categories = request.queryParams("categories")
                    ?: return error("Categories not specified!")

                val catArray = mapper.readTree(categories).map { it.intValue() }

                val ordered = db.getCategories().executeAsBlocking()
                ordered.forEach {
                    it.order = catArray.indexOf(it.id)
                }

                db.insertCategories(ordered).executeAsBlocking()

                return success().put(KEY_CONTENT, JSONObject().apply {
                    ordered.forEach {
                        put(it.id.toString(), it.order)
                    }
                })
            }
            CategoryOperation.RENAME -> {
                val id = request.queryParams("id")?.toIntOrNull()
                    ?: return error("Category id not specified!")
                val name = request.queryParams("name")
                        ?: return error("New category name not specified!")

                val categories = db.getCategories().executeAsBlocking()

                if (categories.any { it.name.equals(name, true) }) {
                    return error("A category with this name already exists!")
                }

                categories.find { it.id == id }?.apply {
                    this.name = name
                    db.insertCategory(this).executeAsBlocking()
                } ?: return error("No category with the specified id exists!")

                return success()
            }
            CategoryOperation.CREATE -> {
                val name = request.queryParams("name")
                    ?: return error("Category name not specified!")

                val categories = db.getCategories().executeAsBlocking()
                if (categories.any { it.name.equals(name, true) }) {
                    return error("A category with this name already exists!")
                }

                val category = Category.create(name)
                category.order = categories.map { it.order + 1 }.max() ?: 0

                category.id = db.insertCategory(category).executeAsBlocking().insertedId()?.toInt()

                return success().put(KEY_CONTENT,
                        JSONObject()
                                .put(KEY_ID, category.id)
                                .put(KEY_ORDER, category.order))
            }
            CategoryOperation.DELETE -> {
                val id = request.queryParams("id")?.toIntOrNull()
                        ?: return error("Category id not specified!")

                db.getCategories().executeAsBlocking()
                        .find { it.id == id }?.apply {
                    db.deleteCategory(this).executeAsBlocking()
                } ?: return error("No category with the specified id exists!")

                return success()
            }
            else -> return error("Unrecognized operation!")
        }
    }

    enum class CategoryOperation {
        REORDER, RENAME, DELETE, CREATE
    }

    companion object {
        val KEY_CONTENT = "content"

        val KEY_ID = "id"
        val KEY_ORDER = "order"
    }
}