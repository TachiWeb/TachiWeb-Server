package xyz.nulldev.ts.syncdeploy

import com.typesafe.config.Config
import xyz.nulldev.ts.config.ConfigModule
import xyz.nulldev.ts.ext.ensureMkdirs
import java.io.File

class SyncConfigModule(config: Config): ConfigModule(config) {
    val enable = config.getBoolean("enable")

    val baseUrl = config.getString("baseUrl").removeSuffix("/")
    val syncOnlyMode = config.getBoolean("syncOnlyMode")

    val accountsFolder = File(config.getString("rootDir")!!).ensureMkdirs()
    val sandboxedConfig = File(config.getString("sandboxedConfig")!!)

    val recaptchaSiteKey = config.getString("recaptcha.siteKey")
    val recaptchaSecret = config.getString("recaptcha.secret")

    companion object {
        fun register(config: Config)
            = SyncConfigModule(config.getConfig("ts.syncd"))
    }
}