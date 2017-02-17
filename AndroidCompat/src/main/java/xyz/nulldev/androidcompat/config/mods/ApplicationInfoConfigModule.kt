package xyz.nulldev.androidcompat.config.mods

import com.typesafe.config.Config
import xyz.nulldev.androidcompat.config.ConfigModule

/**
 * Application info config.
 */

class ApplicationInfoConfigModule(config: Config) : ConfigModule(config) {
    val packageName = config.getString("packageName")!!
    val debug = config.getBoolean("debug")
}
