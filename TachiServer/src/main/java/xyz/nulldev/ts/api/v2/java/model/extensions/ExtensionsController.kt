package xyz.nulldev.ts.api.v2.java.model.extensions

import java.io.File

interface ExtensionsController {
    fun get(vararg packageNames: String): ExtensionCollection

    fun getAll(): ExtensionCollection

    fun trust(hash: String)

    fun reloadAvailable()

    fun installExternal(apk: File)
}