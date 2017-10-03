package xyz.nulldev.ts.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton

class ConfigKodeinModule {
    fun create() = Kodein.Module {
        //Config module
        bind<ConfigManager>() with singleton { ConfigManager }
    }
}