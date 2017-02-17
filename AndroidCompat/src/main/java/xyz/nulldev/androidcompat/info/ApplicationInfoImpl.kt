package xyz.nulldev.androidcompat.info

import android.content.pm.ApplicationInfo
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import xyz.nulldev.androidcompat.config.ConfigManager
import xyz.nulldev.androidcompat.config.mods.ApplicationInfoConfigModule

class ApplicationInfoImpl : ApplicationInfo(), KodeinGlobalAware {
    val configManager: ConfigManager = kodein.instance()

    val appInfoConfig: ApplicationInfoConfigModule
        get() = configManager.module(ApplicationInfoConfigModule::class.java)

    val packageName: String get() = appInfoConfig.packageName
    val debug: Boolean get() = appInfoConfig.debug
}
