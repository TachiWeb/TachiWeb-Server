package xyz.nulldev.ts.api.v3.models.extensions

data class WExtension(
        val hasUpdate: Boolean?,
        val lang: String,
        val name: String,
        val pkgName: String,
        val signatureHash: String?,
        val sources: List<String>?,
        val status: WExtensionStatus,
        val versionCode: Int,
        val versionName: String
)