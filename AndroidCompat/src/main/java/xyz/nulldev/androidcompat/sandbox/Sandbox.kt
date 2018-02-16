package xyz.nulldev.androidcompat.sandbox

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import xyz.nulldev.androidcompat.androidimpl.CustomContext
import xyz.nulldev.androidcompat.androidimpl.FakePackageManager
import xyz.nulldev.androidcompat.info.ApplicationInfoImpl
import xyz.nulldev.androidcompat.io.AndroidFiles
import xyz.nulldev.ts.config.ConfigManager
import java.io.File

/**
 * Sandbox used to isolate different contexts from each other
 */
class Sandbox(val configFolder: File) {
    val configManager = SandboxedConfigManager(configFolder.absolutePath)

    val kodein by lazy {
        Kodein {
            extend(Kodein.global)

            //Define sandboxed components

            bind<ConfigManager>(overrides = true) with singleton { configManager }

            bind<AndroidFiles>(overrides = true) with singleton { AndroidFiles(configManager) }

            bind<ApplicationInfoImpl>(overrides = true) with singleton { ApplicationInfoImpl(this) }

            bind<FakePackageManager>(overrides = true) with singleton { FakePackageManager() }

            bind<CustomContext>(overrides = true) with singleton { CustomContext(this) }
            bind<Context>(overrides = true) with singleton { instance<CustomContext>() }
        }
    }
}