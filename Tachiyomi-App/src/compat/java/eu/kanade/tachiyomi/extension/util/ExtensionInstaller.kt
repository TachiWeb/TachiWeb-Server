package eu.kanade.tachiyomi.extension.util

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import eu.kanade.tachiyomi.extension.model.Extension
import eu.kanade.tachiyomi.extension.model.InstallStep
import eu.kanade.tachiyomi.network.asObservableSuccess
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.nulldev.androidcompat.pm.PackageController
import java.io.File

/**
 * The installer which installs, updates and uninstalls the extensions.
 *
 * @param context The application context.
 */
internal class ExtensionInstaller(private val context: Context) {
    private val controller: PackageController by Kodein.global.lazy.instance()

    /**
     * Adds the given extension to the downloads queue and returns an observable containing its
     * step in the installation process.
     *
     * @param url The url of the apk.
     * @param extension The extension to install.
     */
    fun downloadAndInstall(url: String, extension: Extension) = OkHttpClient()
            .newCall(Request.Builder().url(url).build())
            .asObservableSuccess()
            .map {
                val tmp = File.createTempFile("extension-dl", ".apk")
                tmp.deleteOnExit()

                try {
                    it.body().byteStream().use { input ->
                        tmp.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    controller.installPackage(tmp, true)
                } finally {
                    tmp.delete()
                }
            }.map { InstallStep.Installed }

    /**
     * Starts an intent to uninstall the extension by the given package name.
     *
     * @param pkgName The package name of the extension to uninstall
     */
    fun uninstallApk(pkgName: String) {
        val pkg = controller.findPackage(pkgName)
            ?: throw IllegalArgumentException("Package not installed!")

        controller.deletePackage(pkg)
    }

    /**
     * Sets the result of the installation of an extension.
     *
     * @param downloadId The id of the download.
     * @param result Whether the extension was installed or not.
     */
    fun setInstallationResult(downloadId: Long, result: Boolean) {
    }
}
