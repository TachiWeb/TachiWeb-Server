package xyz.nulldev.androidcompat.io

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import xyz.nulldev.androidcompat.config.ConfigManager
import xyz.nulldev.androidcompat.config.mods.FilesConfigModule
import java.io.File

/**
 * Android file constants.
 */
class AndroidFiles : KodeinGlobalAware {
    val configManager: ConfigManager = kodein.instance()

    val filesConfig: FilesConfigModule
        get() = configManager.module(FilesConfigModule::class.java)

    val dataDir: File get() = File(filesConfig.dataDir).apply { mkdirs() }
    val filesDir: File get() = File(filesConfig.filesDir).apply { mkdirs() }
    val noBackupFilesDir: File get() = File(filesConfig.noBackupFilesDir).apply { mkdirs() }
    val externalFilesDirs: List<File> get() = filesConfig.externalFilesDirs.map(::File).apply {
        forEach {
            it.mkdirs()
        }
    }
    val obbDirs: List<File> get() = filesConfig.obbDirs.map(::File).apply {
        forEach {
            it.mkdirs()
        }
    }
    val cacheDir: File get() = File(filesConfig.cacheDir).apply { mkdirs() }
    val codeCacheDir: File get() = File(filesConfig.codeCacheDir).apply { mkdirs() }
    val externalCacheDirs: List<File> get() = filesConfig.externalCacheDirs.map(::File).apply {
        forEach {
            it.mkdirs()
        }
    }
    val externalMediaDirs: List<File> get() = filesConfig.externalMediaDirs.map(::File).apply {
        forEach {
            it.mkdirs()
        }
    }
    val rootDir: File get() = File(filesConfig.rootDir).apply { mkdirs() }
    val externalStorageDir: File get() = File(filesConfig.externalStorageDir).apply { mkdirs() }
    val downloadCacheDir: File get() = File(filesConfig.downloadCacheDir).apply { mkdirs() }
    val databasesDir: File get() = File(filesConfig.databasesDir).apply { mkdirs() }

    val prefsDir: File get() = File(filesConfig.prefsDir).apply { mkdirs() }
}
