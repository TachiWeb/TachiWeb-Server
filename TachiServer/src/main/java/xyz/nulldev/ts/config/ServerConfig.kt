package xyz.nulldev.ts.config

import com.typesafe.config.Config
import xyz.nulldev.ts.config.util.get
import java.io.File

class ServerConfig(config: Config) : ConfigModule(config) {
    val rootDir = registerFile(config["rootDir"])
    val patchesDir = registerFile(config["patchesDir"])

    fun registerFile(file: String): File {
        return File(file).apply {
            mkdirs()
        }
    }
}