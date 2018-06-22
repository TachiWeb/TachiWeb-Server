package eu.kanade.tachiyomi.extension.util

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import eu.kanade.tachiyomi.extension.model.Extension
import xyz.nulldev.androidcompat.pm.PackageController

/**
 * No-op listener
 */
internal class ExtensionInstallReceiver(private val listener: Listener) {
    private val controller by Kodein.global.lazy.instance<PackageController>()

    /**
     * Registers this broadcast receiver
     */
    fun register(context: Context) {
        controller.registerUninstallListener {
            listener.onPackageUninstalled(it)
        }
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
