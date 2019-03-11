package xyz.nulldev.ts.api.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.google.gson.JsonParser
import xyz.nulldev.ts.api.http.serializer.FilterSerializer
import xyz.nulldev.ts.api.http.serializer.MangaSerializer
import xyz.nulldev.ts.api.task.TaskManager
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig

class HttpModule {
    fun create() = Kodein.Module {
        bind<TaskManager>() with singleton { TaskManager() }

        bind<FilterSerializer>() with singleton { FilterSerializer() }

        bind<MangaSerializer>() with singleton { MangaSerializer() }

        bind<JsonParser>() with singleton { JsonParser() }

        bind<ObjectMapper>() with singleton {
            val serverConfig = this.instance<ConfigManager>().module<ServerConfig>()

            var mapper = ObjectMapper()
                    .registerKotlinModule()
                    .enable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS)
            if (serverConfig.prettyPrintApi) mapper = mapper.enable(SerializationFeature.INDENT_OUTPUT)
            mapper
        }
    }
}
