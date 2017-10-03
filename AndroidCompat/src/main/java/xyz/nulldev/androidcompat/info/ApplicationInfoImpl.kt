package xyz.nulldev.androidcompat.info

import android.content.pm.ApplicationInfo
import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.androidcompat.config.ApplicationInfoConfigModule

class ApplicationInfoImpl : ApplicationInfo(), KodeinGlobalAware {
    val configManager: ConfigManager = kodein.instance()

    val appInfoConfig: ApplicationInfoConfigModule
        get() = configManager.module()

    val debug: Boolean get() = appInfoConfig.debug

    init {
        super.packageName = appInfoConfig.packageName
    }
}
