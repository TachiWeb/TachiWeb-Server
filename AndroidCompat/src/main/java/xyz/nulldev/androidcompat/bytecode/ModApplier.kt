package xyz.nulldev.androidcompat.bytecode

import javassist.CtClass
import mu.KotlinLogging

/**
 * Applies Javassist modifications
 */

class ModApplier {

    val forceOverrideMod by lazy { ForceOverrideMod() }

    val logger = KotlinLogging.logger {}

    fun apply() {
        logger.info { "Applying Javassist mods..." }
        val modifiedClasses = mutableListOf<CtClass>()

        modifiedClasses += forceOverrideMod.apply("xyz.nulldev.androidcompat.androidimpl.CustomContext")

        modifiedClasses.forEach {
            it.toClass()
        }
    }
}