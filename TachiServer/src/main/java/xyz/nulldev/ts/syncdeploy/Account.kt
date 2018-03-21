package xyz.nulldev.ts.syncdeploy

import xyz.nulldev.ts.config.GlobalConfigManager
import java.io.File
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class Account(val name: String) {
    val lock = ReentrantLock()
    val folder = File(ROOT, name)
    val configFolder = File(folder, "config")
    val pwFile = File(folder, "auth")
    val syncDataFolder = File(folder, "tachiserver-data")
    val token = UUID.randomUUID()
    var lastUsedTime: Long = System.currentTimeMillis()
    val configured
        get() = folder.exists()

    companion object {
        private val ROOT = GlobalConfigManager.module<SyncConfigModule>().accountsFolder
    }
}