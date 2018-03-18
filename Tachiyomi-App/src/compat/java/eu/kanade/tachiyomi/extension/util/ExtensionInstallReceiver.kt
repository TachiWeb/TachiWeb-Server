package eu.kanade.tachiyomi.extension.util

import android.content.Context
import eu.kanade.tachiyomi.extension.model.Extension

/**
 * No-op listener
 */
internal class ExtensionInstallReceiver(private val listener: Listener) {
    /**
     * Registers this broadcast receiver
     */
    fun register(context: Context) {
        //Do nothing
    }

    /**
     * Listener that receives extension installation events.
     */
    interface Listener {
        fun onExtensionInstalled(extension: Extension.Installed)
        fun onExtensionUpdated(extension: Extension.Installed)
        fun onExtensionUntrusted(extension: Extension.Untrusted)
        fun onPackageUninstalled(pkgName: String)
    }
}
