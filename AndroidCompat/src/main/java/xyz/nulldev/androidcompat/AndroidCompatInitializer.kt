package xyz.nulldev.androidcompat

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import xyz.nulldev.androidcompat.bytecode.ModApplier
import xyz.nulldev.androidcompat.config.ApplicationInfoConfigModule
import xyz.nulldev.androidcompat.config.FilesConfigModule
import xyz.nulldev.androidcompat.config.SystemConfigModule
import xyz.nulldev.ts.config.ConfigManager

/**
 * Initializes the Android compatibility module
 */
class AndroidCompatInitializer {

    val modApplier by lazy { ModApplier() }

    fun init() {
        modApplier.apply()

        Kodein.global.addImport(AndroidCompatModule().create())

        //Register config modules
        ConfigManager.registerModules(
            FilesConfigModule(ConfigManager.config.getConfig("android.files")),
            ApplicationInfoConfigModule(ConfigManager.config.getConfig("android.app")),
            SystemConfigModule(ConfigManager.config.getConfig("android.system"))
        )
    }
}
