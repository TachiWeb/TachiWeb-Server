package xyz.nulldev.androidcompat.bytecode

import javassist.CtClass
import org.slf4j.LoggerFactory

/**
 * Applies Javassist modifications
 */

class ModApplier {

    val forceOverrideMod by lazy { ForceOverrideMod() }

    val logger = LoggerFactory.getLogger(ModApplier::class.java)

    fun apply() {
        logger.info("Applying Javassist mods...")
        val modifiedClasses = mutableListOf<CtClass>()

        modifiedClasses += forceOverrideMod.apply("xyz.nulldev.androidcompat.androidimpl.CustomContext")
    }
}