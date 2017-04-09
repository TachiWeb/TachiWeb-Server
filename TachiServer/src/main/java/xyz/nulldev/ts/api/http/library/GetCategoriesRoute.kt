package xyz.nulldev.ts.api.http.library

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import org.json.JSONArray
import org.json.JSONObject
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Get categories
 */

class GetCategoriesRoute : TachiWebRoute() {

    private val db: DatabaseHelper by kInstanceLazy()

    override fun handleReq(request: Request, response: Response): Any? {
        return success().put(KEY_CONTENT,
                JSONArray().apply {
                    db.getCategories().executeAsBlocking().map {
                        JSONObject().apply {
                            put(KEY_ID, it.id)
                            put(KEY_NAME, it.name)
                            put(KEY_ORDER, it.order)
                        }
                    }.forEach {
                        put(it)
                    }
                })
    }

    companion object {
        val KEY_CONTENT = "content"

        val KEY_ID = "id"
        val KEY_NAME = "name"
        val KEY_ORDER = "order"
    }
}
