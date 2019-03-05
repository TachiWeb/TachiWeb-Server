package xyz.nulldev.ts.api.v3

import io.vertx.core.Vertx
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import xyz.nulldev.ts.api.v3.operations.categories.CategoryOperations

class WebAPI {
    private val operations = listOf(
            CategoryOperations()
    )

    fun start() {
        val vertx = Vertx.vertx()

        OpenAPI3RouterFactory.create(vertx, WebAPI::class.java.getResource("openapi.json").toURI().toURL().toString()) { ar ->
            if (ar.succeeded()) {
                // Spec loaded with success
                val routerFactory = ar.result()
                operations.forEach { it.register(routerFactory) }
            } else {
                // Something went wrong during router factory initialization
                throw ar.cause()
            }
        }
    }
}