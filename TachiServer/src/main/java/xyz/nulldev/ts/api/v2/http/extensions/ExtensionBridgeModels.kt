package xyz.nulldev.ts.api.v2.http.extensions

import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionModel
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionStatus as InternalExtensionStatus

data class ExtensionName(val pkgName: String,
                         val name: String)

data class ExtensionStatus(val pkgName: String,
                           val status: InternalExtensionStatus)

data class ExtensionVersionName(val pkgName: String,
                                val versionName: String)

data class ExtensionVersionCode(val pkgName: String,
                                val versionCode: Int)

data class ExtensionSignatureHash(val pkgName: String,
                                  val signatureHash: String?)

data class ExtensionLang(val pkgName: String,
                         val lang: String?)

data class ExtensionSources(val pkgName: String,
                            val sources: List<String>?)

data class ExtensionHasUpdate(val pkgName: String,
                              val hasUpdate: Boolean?)

data class SerializableExtensionModel(
        val pkgName: String,
        val name: String,
        val status: InternalExtensionStatus,
        val versionName: String,
        val versionCode: Int,
        val signatureHash: String?,
        val lang: String?,
        val sources: List<String>?,
        val hasUpdate: Boolean?
) {
    constructor(extension: ExtensionModel):
            this(extension.pkgName,
                    extension.name,
                    extension.status,
                    extension.versionName,
                    extension.versionCode,
                    extension.signatureHash,
                    extension.lang,
                    extension.sources?.map { it.id.toString() },
                    extension.hasUpdate)
}

