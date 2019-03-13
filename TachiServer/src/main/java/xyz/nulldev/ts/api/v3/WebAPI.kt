package xyz.nulldev.ts.api.v3

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.ext.web.handler.CorsHandler
import xyz.nulldev.ts.api.v3.operations.APIOperations
import xyz.nulldev.ts.api.v3.operations.categories.CategoryOperations
import xyz.nulldev.ts.api.v3.operations.chapters.ChapterOperations
import xyz.nulldev.ts.api.v3.operations.manga.MangaOperations
import xyz.nulldev.ts.api.v3.operations.preferences.PreferenceOperations
import xyz.nulldev.ts.api.v3.operations.server.ServerOperations
import xyz.nulldev.ts.api.v3.operations.sources.SourceOperations
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class WebAPIInfo(
        val port: Int
)

class WebAPI {
    val vertx: Vertx = Vertx.vertx()

    private val operations = listOf(
            CategoryOperations(vertx),
            ChapterOperations(vertx),
            MangaOperations(vertx),
            ServerOperations(vertx),
            SourceOperations(vertx),
            APIOperations(vertx),
            PreferenceOperations(vertx)
    )

    suspend fun start() = suspendCoroutine<WebAPIInfo> { cont ->
        OpenAPI3RouterFactory.create(vertx, WebAPI::class.java.getResource(SPEC_LOCATION).toURI().toURL().toString()) { ar ->
            if (ar.succeeded()) {
                // Spec loaded with success
                val routerFactory = ar.result()

                // Allow CORS
                routerFactory.addGlobalHandler(CorsHandler.create("*")
                        .allowedMethods(HttpMethod.values().toSet())
                        .allowedHeader("*")
                        /* TODO .allowCredentials(true) */)

                // Load operations
                operations.forEach { it.register(routerFactory) }

                // TODO All security handlers are currently NO-OP
                routerFactory.addSecurityHandler("account") { it.next() }
                routerFactory.addSecurityHandler("account-cookie") { it.next() }

                // Start http server
                vertx.createHttpServer(HttpServerOptions())
                        .requestHandler(routerFactory.router)
                        .listen(0) {
                            // Select random available port
                            if (it.succeeded()) {
                                val server = it.result()
                                cont.resume(WebAPIInfo(
                                        port = server.actualPort()
                                ))
                            } else {
                                cont.resumeWithException(it.cause())
                            }
                        }
            } else {
                // Something went wrong during router factory initialization
                cont.resumeWithException(ar.cause())
            }
        }
    }

    companion object {
        const val SPEC_LOCATION = "/openapi.json"
    }
}