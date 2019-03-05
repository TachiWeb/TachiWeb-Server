package xyz.nulldev.ts.api.v3.operations.server

import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.opWithContext
import kotlin.system.exitProcess

class ServerOperations : OperationGroup {
    override fun register(routerFactory: OpenAPI3RouterFactory) {
        routerFactory.opWithContext(::stopServer.name, ::stopServer)
    }

    suspend fun stopServer(context: RoutingContext) {
        // Ensure response is sent before shutdown
        context.response().end()
        exitProcess(0)
    }
}