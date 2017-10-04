package xyz.nulldev.ts.config

import com.typesafe.config.Config
import java.io.File

class ServerConfig(config: Config) : ConfigModule(config) {
    val rootDir = registerFile(config.getString("rootDir"))
    val patchesDir = registerFile(config.getString("patchesDir"))

    fun registerFile(file: String): File {
        return File(file).apply {
            mkdirs()
        }
    }
}