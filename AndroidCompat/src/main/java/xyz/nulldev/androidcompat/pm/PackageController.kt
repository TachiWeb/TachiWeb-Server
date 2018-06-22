package xyz.nulldev.androidcompat.pm

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import net.dongliu.apk.parser.ApkParsers
import xyz.nulldev.androidcompat.io.AndroidFiles
import java.io.File

class PackageController {
    private val androidFiles by Kodein.global.lazy.instance<AndroidFiles>()

    private fun findRoot(apk: File): File {
        val pn = ApkParsers.getMetaInfo(apk).packageName

        return File(androidFiles.packagesDir, pn)
    }

    fun installPackage(apk: File, allowReinstall: Boolean) {
        val root = findRoot(apk)

        try {
            if (root.exists()) {
                if (allowReinstall) {
                    throw IllegalStateException("Package already installed!")
                } else {
                    // TODO Compare past and new signature
                    root.deleteRecursively()
                }
            }

            root.mkdirs()

            val installed = InstalledPackage(root)
            apk.copyTo(installed.apk)
            installed.writeIcon()
            installed.writeJar()

            if (!installed.jar.exists()) {
                throw IllegalStateException("Failed to translate APK dex!")
            }
        } finally {
            root.deleteRecursively()
        }
    }

    fun listInstalled(): List<InstalledPackage> {
        return androidFiles.packagesDir.listFiles().orEmpty().filter {
            it.isDirectory
        }.map {
            InstalledPackage(it)
        }
    }

    fun deletePackage(pack: InstalledPackage) {
        pack.root.deleteRecursively()
    }

    fun findPackage(packageName: String): InstalledPackage? {
        val file = File(androidFiles.packagesDir, packageName)
        return if(file.exists())
            InstalledPackage(file)
        else
            null
    }
}