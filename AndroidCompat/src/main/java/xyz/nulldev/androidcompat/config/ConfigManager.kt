package xyz.nulldev.androidcompat.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import xyz.nulldev.androidcompat.config.mods.FilesConfigModule
import java.io.File

/**
 * Manages app config.
 */
class ConfigManager {
    lateinit var generatedModules: Map<Class<out ConfigModule>, ConfigModule>
    lateinit var config: Config

    val configFolder: String
        get() = System.getProperty("tw-configdirs") ?: "config"

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

        return configs.last()
    }

    /**
     * Generate the config modules from each file
     */
    fun configToModules(config: Config) = mutableListOf<ConfigModule>(
            FilesConfigModule(config.getConfig("files"))
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