package xyz.nulldev.ts.api.http.catalogue

import com.github.salomonbrys.kotson.set
import com.google.gson.JsonArray
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceManager
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.api.http.serializer.FilterSerializer
import xyz.nulldev.ts.ext.gson
import xyz.nulldev.ts.ext.kInstanceLazy

class GetFiltersRoute : TachiWebRoute() {

    val serializer: FilterSerializer by kInstanceLazy()

    val sourceManager: SourceManager by kInstanceLazy()

    val serializationCache = mutableMapOf<Long, JsonArray>()

    override fun handleReq(request: Request, response: Response): Any? {
        //Get/parse parameters
        val sourceId = request.params(":sourceId")?.toLong()
                ?: return error("SourceID must be specified!")

        val serialized = serializationCache[sourceId] ?: let {
            //Try to resolve source
            val source = sourceManager.get(sourceId)
                    ?: return error("The specified source does not exist!")
            if (source !is CatalogueSource) {
                return error("The specified source is not a CatalogueSource!")
            }

            val serialized = serializer.serialize(source.getFilterList())
            serializationCache[sourceId] = serialized
            serialized
        }

        //TODO Make "success" and "error" functions return GSON objects by default
        return success().gson().apply {
            this[KEY_CONTENT] = serialized
        }
    }

    companion object {
        val KEY_CONTENT = "content"
    }
}
