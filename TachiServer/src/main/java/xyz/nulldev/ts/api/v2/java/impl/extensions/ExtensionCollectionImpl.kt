package xyz.nulldev.ts.api.v2.java.impl.extensions

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.extension.model.Extension
import eu.kanade.tachiyomi.source.Source
import xyz.nulldev.androidcompat.pm.PackageController
import xyz.nulldev.ts.api.v2.java.impl.util.ProxyList
import xyz.nulldev.ts.api.v2.java.model.PreparedInputStream
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionCollection
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionModel
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionStatus
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files

class ExtensionCollectionImpl(override val pkgName: List<String>): ExtensionCollection,
        List<ExtensionModel> by ProxyList(pkgName, { ExtensionCollectionProxy(it) }) {
    private val manager by Kodein.global.lazy.instance<ExtensionManager>()
    private val controller by Kodein.global.lazy.instance<PackageController>()

    override val name: List<String>
        get() = mapPkgNames { it.name }

    override val status: List<ExtensionStatus>
        get() = mapPkgNames {
            when(it) {
                is Extension.Available -> ExtensionStatus.AVAILABLE
                is Extension.Installed -> ExtensionStatus.INSTALLED
                is Extension.Untrusted -> ExtensionStatus.UNTRUSTED
            }
        }

    override val versionName: List<String>
        get() = mapPkgNames { it.versionName }

    override val versionCode: List<Int>
        get() = mapPkgNames { it.versionCode }

    override val signatureHash: List<String?>
        get() = mapPkgNames { (it as? Extension.Untrusted)?.signatureHash }

    override val lang: List<String?>
        get() = mapPkgNames { it.lang }

    // TODO API source model
    override val sources: List<List<Source>?>
        get() = mapPkgNames { (it as? Extension.Installed)?.sources }

    override val hasUpdate: List<Boolean?>
        get() = mapPkgNames { (it as? Extension.Installed)?.hasUpdate }

    override val icon: List<Pair<String, PreparedInputStream>?>
        get() = mapPkgNames {
            when(it) {
                is Extension.Available -> URLConnection.guessContentTypeFromName(it.iconUrl) to
                        PreparedInputStream.from {
                            URL(it.iconUrl).openStream()
                        }
                is Extension.Installed,
                is Extension.Untrusted -> controller.findPackage(it.pkgName)?.icon?.let {
                    if(!it.exists()) return@let null

                    Files.probeContentType(it.toPath()) to PreparedInputStream.from {
                        it.inputStream()
                    }
                }
            }
        }

    override fun delete() {
        pkgName.forEach {
            val pack = controller.findPackage(it)
                    ?: error("Package $it is not installed!")

            controller.deletePackage(pack)
        }
    }

    override fun install() {
        mapPkgNames {
            (if (it is Extension.Available) {
                manager.installExtension(it)
            } else if(it is Extension.Installed && it.hasUpdate) {
                manager.updateExtension(it)
            } else {
                error("Extension ${it.pkgName} is not an installable extension!")
            }).toBlocking().single()
        }
    }

    private fun <T> mapPkgNames(transform: (Extension) -> T): List<T> {
        val allExtensions = ExtensionsControllerImpl.getAllExtensions()

        return pkgName.map { pkgName ->
            allExtensions.find { it.pkgName == pkgName }
                    ?: error("Cannot find extension with package name $pkgName!")
        }.map(transform)
    }
}

class ExtensionCollectionProxy(override val pkgName: String): ExtensionModel {
    private val collection = ExtensionCollectionImpl(listOf(pkgName))

    override val name: String
        get() = collection.name[0]

    override val status: ExtensionStatus
        get() = collection.status[0]

    override val versionName: String
        get() = collection.versionName[0]

    override val versionCode: Int
        get() = collection.versionCode[0]

    override val signatureHash: String?
        get() = collection.signatureHash[0]

    override val lang: String?
        get() = collection.lang[0]

    override val sources: List<Source>?
        get() = collection.sources[0]

    override val hasUpdate: Boolean?
        get() = collection.hasUpdate[0]

    override val icon: Pair<String, PreparedInputStream>?
        get() = collection.icon[0]

    override fun delete() = collection.delete()

    override fun install() = collection.install()
}
