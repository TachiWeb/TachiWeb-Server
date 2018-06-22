package xyz.nulldev.ts.api.v2.java.impl.extensions

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.extension.model.Extension
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionsController

class ExtensionsControllerImpl : ExtensionsController {

    override fun get(vararg packageNames: String)
            = ExtensionCollectionImpl(packageNames.toList()) // TODO Check these extensions exist

    override fun getAll()
        = ExtensionCollectionImpl(getAllExtensions().map { it.pkgName })

    override fun trust(hash: String) {
        manager.trustSignature(hash)
    }

    override fun reloadAvailable() {
        manager.findAvailableExtensions()
        manager.getAvailableExtensionsObservable().take(2).toBlocking().forEach {}
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
    }
}