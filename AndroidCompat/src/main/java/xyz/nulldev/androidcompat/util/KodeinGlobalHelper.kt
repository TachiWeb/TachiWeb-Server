package xyz.nulldev.androidcompat.util

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global

/**
 * Helper class to allow access to Kodein from Java
 */
class KodeinGlobalHelper {
    companion object {
        /**
         * Get the Kodein object
         */
        fun kodein() = Kodein.global

        /**
         * Get a dependency
         */
        fun <T> instance(type: Class<T>): T = kodein().typed.instance(type) as T
    }
}
