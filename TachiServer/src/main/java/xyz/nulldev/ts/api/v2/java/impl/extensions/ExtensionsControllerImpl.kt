package xyz.nulldev.ts.api.v2.java.impl.extensions

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.extension.model.Extension
import xyz.nulldev.androidcompat.pm.PackageController
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionsController
import xyz.nulldev.ts.ext.kInstance
import xyz.nulldev.ts.ext.kInstanceLazy
import java.io.File

class ExtensionsControllerImpl : ExtensionsController {
    private val controller by kInstanceLazy<PackageController>()

    override fun get(vararg packageNames: String)
            = ExtensionCollectionImpl(packageNames.toList()) // TODO Check these extensions exist

    override fun getAll()
        = ExtensionCollectionImpl(getAllExtensions().map { it.pkgName })

    override fun trust(hash: String) {
        manager.trustSignature(hash)
    }

    override fun reloadAvailable() {
        manager.findAvailableExtensions()
        manager.getAvailableExtensionsObservable().take(2).toBlocking().subscribe()
    }

    override fun reloadLocal() {
        manager.init(kInstance())
    }

    override fun installExternal(apk: File) {
        controller.installPackage(apk, true)
        reloadLocal()
    }

    companion object {
        private val manager by Kodein.global.lazy.instance<ExtensionManager>()

        internal fun getAllExtensions(): List<Extension> {
            var localExtensions = manager.installedExtensions +
                    manager.untrustedExtensions

            // Get available extensions excluding ones that have already been installed
            localExtensions += manager.availableExtensions.filter { avail ->
                localExtensions.none { it.pkgName == avail.pkgName }
            }

            return localExtensions
        }

        init {
            // Forcibly fill this initially to allow the reloadAvailable endpoint to function
            manager.findAvailableExtensions()
        }
    }
}