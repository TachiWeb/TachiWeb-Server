package xyz.nulldev.ts.api.http.sync

import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import java.io.File

class LastSyncDb {
    private val serverConfig = ConfigManager.module<ServerConfig>()
    private val dbFile = File(serverConfig.rootDir, "last_sync.db")

    val lastSyncs by lazy {
        dbFile.readLines().associate {
            Pair(it.substringAfter(':'),
                    it.substringBefore(':').toLong())
        }.toMutableMap()
    }

    @Synchronized
    fun save() {
        val text = lastSyncs.map {
            "${it.value}:${it.key}"
        }.joinToString(separator = "\n")

        dbFile.writeText(text)
    }
}