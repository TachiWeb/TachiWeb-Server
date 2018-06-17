package xyz.nulldev.ts.api.v2.http

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.ApiBuilder.*
import io.javalin.Javalin
import io.javalin.translator.json.JavalinJacksonPlugin
import xyz.nulldev.ts.api.http.auth.AuthController

class HttpApplication {
    val app = Javalin.create()
            .enableStandardRequestLogging()
            .enableCorsForAllOrigins() // TODO Should we really enable CORs?
            .enableDynamicGzip()
            .port(4567)

    init {
        configureJackson()

        app.routes {
            path("auth") {
                post(AuthController::login)
                delete(AuthController::invalidateSession)
                post("clearall", AuthController::invalidateAll)
                get(AuthController::getSession)
            }
        }
    }

    fun configureJackson() {
        JavalinJacksonPlugin.configure(jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        })
    }
}