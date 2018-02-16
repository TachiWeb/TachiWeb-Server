package xyz.nulldev.androidcompat.info

import android.content.pm.ApplicationInfo
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import xyz.nulldev.androidcompat.config.ApplicationInfoConfigModule
import xyz.nulldev.ts.config.ConfigManager

class ApplicationInfoImpl(override val kodein: Kodein = Kodein.global) : ApplicationInfo(), KodeinAware {
    val configManager: ConfigManager = kodein.instance()

    val appInfoConfig: ApplicationInfoConfigModule
        get() = configManager.module()

    val debug: Boolean get() = appInfoConfig.debug

    init {
        super.packageName = appInfoConfig.packageName
    }
}
