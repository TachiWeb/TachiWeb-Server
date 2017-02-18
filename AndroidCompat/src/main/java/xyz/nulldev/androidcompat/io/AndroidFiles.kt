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

    val dataDir: File get() = File(filesConfig.dataDir)
    val filesDir: File get() = File(filesConfig.filesDir)
    val noBackupFilesDir: File get() = File(filesConfig.noBackupFilesDir)
    val externalFilesDirs: List<File> get() = filesConfig.externalFilesDirs.map(::File)
    val obbDirs: List<File> get() = filesConfig.obbDirs.map(::File)
    val cacheDir: File get() = File(filesConfig.cacheDir)
    val codeCacheDir: File get() = File(filesConfig.codeCacheDir)
    val externalCacheDirs: List<File> get() = filesConfig.externalCacheDirs.map(::File)
    val externalMediaDirs: List<File> get() = filesConfig.externalMediaDirs.map(::File)
    val rootDir: File get() = File(filesConfig.rootDir)
    val externalStorageDir: File get() = File(filesConfig.externalStorageDir)
    val downloadCacheDir: File get() = File(filesConfig.downloadCacheDir)

    val prefsDir: File get() = File(filesConfig.prefsDir)
}
