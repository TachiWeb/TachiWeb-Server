package xyz.nulldev.androidcompat

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import xyz.nulldev.androidcompat.androidimpl.CustomContext
import xyz.nulldev.androidcompat.config.ConfigManager
import xyz.nulldev.androidcompat.info.ApplicationInfoImpl
import xyz.nulldev.androidcompat.io.AndroidFiles

/**
 * AndroidCompatModule
 */

class AndroidCompatModule {
    fun create() = Kodein.Module {
        bind<ConfigManager>() with singleton { ConfigManager().setup() }
        bind<AndroidFiles>() with singleton { AndroidFiles() }
        bind<ApplicationInfoImpl>() with singleton { ApplicationInfoImpl() }
        bind<CustomContext>() with singleton { CustomContext() }
        bind<Context>() with singleton { Kodein.global.instance<CustomContext>() }
    }
}
