package xyz.nulldev.androidcompat

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import xyz.nulldev.androidcompat.bytecode.ModApplier

/**
 * Initializes the Android compatibility module
 */
class AndroidCompatInitializer {

    val modApplier by lazy { ModApplier() }

    fun init() {
        modApplier.apply()

        Kodein.global.addImport(AndroidCompatModule().create())
    }
}
