package xyz.nulldev.ts.api.http

import io.javalin.ApiBuilder.*
import io.javalin.Javalin
import xyz.nulldev.ts.api.http.auth.AuthController

class HttpApplication {
    val app = Javalin.create()
            .enableStandardRequestLogging()
            .enableCorsForAllOrigins() // TODO Should we really enable CORs?
            .enableDynamicGzip()
            .port(4567)

    init {
        app.routes {
            path("auth") {
                post(AuthController::login)
                delete(AuthController::invalidateSession)
                post("clearall", AuthController::invalidateAll)
                get(AuthController::getSession)
            }
        }
    }
}