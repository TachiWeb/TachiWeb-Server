package xyz.nulldev.androidcompat

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import xyz.nulldev.androidcompat.bytecode.ModApplier
import xyz.nulldev.androidcompat.config.ApplicationInfoConfigModule
import xyz.nulldev.androidcompat.config.FilesConfigModule
import xyz.nulldev.androidcompat.config.SystemConfigModule
import xyz.nulldev.ts.config.GlobalConfigManager

/**
 * Initializes the Android compatibility module
 */
class AndroidCompatInitializer {

    val modApplier by lazy { ModApplier() }

    fun init() {
        modApplier.apply()

        Kodein.global.addImport(AndroidCompatModule().create())

        //Register config modules
        GlobalConfigManager.registerModules(
            FilesConfigModule(GlobalConfigManager.config.getConfig("android.files")),
            ApplicationInfoConfigModule(GlobalConfigManager.config.getConfig("android.app")),
            SystemConfigModule(GlobalConfigManager.config.getConfig("android.system"))
        )
    }
}
