package xyz.nulldev.ts.api.http

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import xyz.nulldev.ts.api.task.TaskManager

class HttpModule {
    fun create() = Kodein.Module {
        bind<TaskManager>() with singleton { TaskManager() }
    }
}
