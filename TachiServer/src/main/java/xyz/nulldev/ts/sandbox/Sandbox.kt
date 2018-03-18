package xyz.nulldev.ts.sandbox

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.androidcompat.androidimpl.CustomContext
import xyz.nulldev.androidcompat.androidimpl.FakePackageManager
import xyz.nulldev.androidcompat.config.ApplicationInfoConfigModule
import xyz.nulldev.androidcompat.config.FilesConfigModule
import xyz.nulldev.androidcompat.config.SystemConfigModule
import xyz.nulldev.androidcompat.info.ApplicationInfoImpl
import xyz.nulldev.androidcompat.io.AndroidFiles
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import java.io.Closeable
import java.io.File

/**
 * Sandbox used to isolate different contexts from each other
 */
class Sandbox(configFolder: File) : Closeable {
    val configManager = SandboxedConfigManager(configFolder.absolutePath).apply {
        // Re-register config modules
        registerModules(
                ServerConfig.register(this.config),
                FilesConfigModule.register(this.config),
                ApplicationInfoConfigModule.register(this.config),
                SystemConfigModule.register(this.config)
        )
    }

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

            bind<DatabaseHelper>(overrides = true) with singleton { DatabaseHelper(instance()) }
        }
    }

    /**
     * Clean-up sandbox
     */
    override fun close() {
        kodein.instance<DatabaseHelper>().db.close()
    }
}