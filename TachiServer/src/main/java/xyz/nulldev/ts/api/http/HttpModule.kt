package xyz.nulldev.ts.api.http

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.google.gson.JsonParser
import xyz.nulldev.ts.api.http.serializer.FilterSerializer
import xyz.nulldev.ts.api.http.serializer.MangaSerializer
import xyz.nulldev.ts.api.task.TaskManager

class HttpModule {
    fun create() = Kodein.Module {
        bind<TaskManager>() with singleton { TaskManager() }

        bind<FilterSerializer>() with singleton { FilterSerializer() }

        bind<MangaSerializer>() with singleton { MangaSerializer() }

        bind<JsonParser>() with singleton { JsonParser() }
    }
}
