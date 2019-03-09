package xyz.nulldev.ts.api.v3.operations

import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.WebAPI
import xyz.nulldev.ts.api.v3.opWithContext

class APIOperations(private val vertx: Vertx) : OperationGroup {
    val spec by lazy {
        this::class.java.getResourceAsStream(WebAPI.SPEC_LOCATION).bufferedReader().use {
            it.readText()
        }
    }

    override fun register(routerFactory: OpenAPI3RouterFactory) {
        routerFactory.opWithContext(::getApiSpec.name, ::getApiSpec)
    }

    suspend fun getApiSpec(context: RoutingContext) {
        context.response().end(spec)
    }
}