package xyz.nulldev.ts.api.v3.operations.sources

import com.github.salomonbrys.kotson.array
import com.google.gson.JsonParser
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.source.online.LoginSource
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import rx.schedulers.Schedulers
import xyz.nulldev.ts.api.http.serializer.FilterSerializer
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.models.WLoginRequest
import xyz.nulldev.ts.api.v3.models.catalogue.WCataloguePage
import xyz.nulldev.ts.api.v3.models.catalogue.WCataloguePageRequest
import xyz.nulldev.ts.api.v3.models.catalogue.WLatestUpdatesRequest
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes.*
import xyz.nulldev.ts.api.v3.models.sources.WSource
import xyz.nulldev.ts.api.v3.op
import xyz.nulldev.ts.api.v3.opWithParams
import xyz.nulldev.ts.api.v3.opWithParamsAndContext
import xyz.nulldev.ts.api.v3.operations.manga.asWeb
import xyz.nulldev.ts.api.v3.util.await
import xyz.nulldev.ts.ext.kInstanceLazy
import java.util.*

private const val SOURCE_ID_PARAM = "sourceId"

class SourceOperations(private val vertx: Vertx) : OperationGroup {
    private val sourceManager: SourceManager by kInstanceLazy()
    private val filtersSerializer: FilterSerializer by kInstanceLazy()
    private val jsonParser: JsonParser by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    override fun register(routerFactory: OpenAPI3RouterFactory) {
        routerFactory.op(::getSources.name, ::getSources)
        routerFactory.op(::getSource.name, ::getSource)
        routerFactory.opWithParamsAndContext(::loginToSource.name, SOURCE_ID_PARAM, ::loginToSource)
        routerFactory.opWithParams(::getSourceCatalogue.name, SOURCE_ID_PARAM, ::getSourceCatalogue)
        routerFactory.opWithParams(::getSourceLatestUpdates.name, SOURCE_ID_PARAM, ::getSourceLatestUpdates)
    }

    suspend fun getSources(): List<WSource> {
        return sourceManager.getCatalogueSources().map {
            it.asWeb()
        }
    }

    suspend fun getSource(sourceId: String): WSource {
        return sourceManager.get(sourceId.toLong())?.asWeb() ?: notFound()
    }

    suspend fun loginToSource(sourceId: String, credentials: WLoginRequest, routingContext: RoutingContext) {
        val source = sourceManager.get(sourceId.toLong()) ?: notFound()
        val loginSource = (source as? LoginSource) ?: abort(400, AUTH_UNSUPPORTED)
        if (loginSource.isLogged()) abort(400, ALREADY_LOGGED_IN)

        val result = try {
            loginSource.login(credentials.username, credentials.password).toSingle().await(Schedulers.io())
        } catch (e: Exception) {
            false
        }

        routingContext.response().statusCode = if (result) 204 else 401
    }

    suspend fun getSourceCatalogue(sourceId: String, request: WCataloguePageRequest): WCataloguePage {
        val source = (sourceManager.get(sourceId.toLong()) ?: notFound()) as? CatalogueSource ?: expectedError(
                500,
                "The specified source is not a catalogue source!",
                NON_CATALOGUE_SOURCE
        )

        val filtersObj = source.getFilterList()
        val filtersAreDefault = request.filters?.let {
            try {
                filtersSerializer.deserialize(filtersObj, jsonParser.parse(it).array)
            } catch (t: Throwable) {
                expectedError(500, FILTER_DESERIALIZATION_FAILED, t)
            }

            filtersObj == source.getFilterList()
        } ?: true


        try {
            val observable = if (!request.query.isNullOrEmpty() || !filtersAreDefault) {
                source.fetchSearchManga(request.page, request.query ?: "", filtersObj)
            } else {
                source.fetchPopularManga(request.page)
            }

            return observable.toSingle().await(Schedulers.io()).asWeb(source.id)
        } catch (t: Throwable) {
            expectedError(500, CATALOGUE_FETCH_ERROR, t)
        }
    }

    suspend fun getSourceLatestUpdates(sourceId: String, request: WLatestUpdatesRequest): WCataloguePage {
        val source = (sourceManager.get(sourceId.toLong()) ?: notFound()) as? CatalogueSource ?: expectedError(
                500,
                "The specified source is not a catalogue source!",
                NON_CATALOGUE_SOURCE
        )

        if (!source.supportsLatest)
            expectedError(
                    500,
                    "The specified source does not support latest updates!",
                    LATEST_UPDATES_UNSUPPORTED
            )

        try {
            return source.fetchLatestUpdates(request.page).toSingle().await(Schedulers.io()).asWeb(source.id)
        } catch (t: Throwable) {
            expectedError(500, CATALOGUE_FETCH_ERROR, t)
        }
    }

    suspend fun MangasPage.asWeb(sourceId: Long) = WCataloguePage(hasNextPage, mangas.map {
        networkToLocalManga(it, sourceId).asWeb(db, sourceManager)
    })

    private suspend fun networkToLocalManga(sManga: SManga, sourceId: Long): Manga {
        var localManga = db.getManga(sManga.url, sourceId).await()
        if (localManga == null) {
            val newManga = Manga.create(sManga.url, sManga.title, sourceId)
            newManga.copyFrom(sManga)
            val result = db.insertManga(newManga).await()
            newManga.id = result.insertedId()
            localManga = newManga
        }
        return localManga
    }

    fun Source.asWeb(): WSource {
        val locale = (this as? HttpSource)?.lang?.let { Locale(it) }

        return WSource(
                id.toString(),
                (this as? HttpSource)?.lang,
                locale?.getDisplayName(locale),
                locale?.getDisplayName(Locale.US),
                (this as? LoginSource)?.isLogged(),
                name,
                this is LoginSource,
                (this as? CatalogueSource)?.supportsLatest ?: false
        )
    }
}