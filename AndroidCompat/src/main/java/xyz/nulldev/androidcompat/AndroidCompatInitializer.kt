package xyz.nulldev.androidcompat

import xyz.nulldev.androidcompat.bytecode.ModApplier

/**
 * Initializes the Android compatibility module
 */
class AndroidCompatInitializer {

    val modApplier by lazy { ModApplier() }

    fun init() {
        modApplier.apply()
    }
}
