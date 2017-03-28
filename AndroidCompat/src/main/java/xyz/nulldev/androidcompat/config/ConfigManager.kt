package xyz.nulldev.androidcompat.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import mu.KotlinLogging
import xyz.nulldev.androidcompat.config.mods.ApplicationInfoConfigModule
import xyz.nulldev.androidcompat.config.mods.FilesConfigModule
import xyz.nulldev.androidcompat.config.mods.SystemConfigModule
import java.io.File

/**
 * Manages app config.
 */
class ConfigManager {
    lateinit var generatedModules: Map<Class<out ConfigModule>, ConfigModule>
    lateinit var config: Config

    val configFolder: String
        get() = System.getProperty("compat-configdirs") ?: "config"

    val logger = KotlinLogging.logger {}

    /**
     * Get a config module
     */
    fun <T : ConfigModule> module(type: Class<T>): T = generatedModules[type] as T

    /**
     * Load configs
     */
    fun loadConfigs(): Config {
        val configs = mutableListOf<Config>()

        //Load reference config
        configs += ConfigFactory.parseResources("reference.conf")

        //Load custom configs from dir
        File(configFolder).listFiles()?.map {
            ConfigFactory.parseFile(it)
        }?.filterNotNull()?.forEach {
            configs += it.withFallback(configs.last())
        }

        val config = configs.last().resolve()

        logger.debug {
            "Loaded config:\n" + config.root().render(ConfigRenderOptions.concise().setFormatted(true))
        }

        return config
    }

    /**
     * Generate the config modules from each file
     */
    fun configToModules(config: Config) = mutableListOf(
            FilesConfigModule(config.getConfig("files")),
            ApplicationInfoConfigModule(config.getConfig("app")),
            SystemConfigModule(config.getConfig("system"))
    )

    /**
     * Setup and load configs
     */
    fun setup(): ConfigManager {
        config = loadConfigs()
        generatedModules = configToModules(config).associateBy {
            it.javaClass
        }
        return this
    }
}