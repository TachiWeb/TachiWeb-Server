package xyz.nulldev.ts.api.v2.java.model.extensions

interface ExtensionLikeModel {
    val name: Any

    val pkgName: Any

    val status: Any

    val versionName: Any

    val versionCode: Any

    val signatureHash: Any?

    val lang: Any?

    // TODO API source model
    val sources: Any?

    val hasUpdate: Any?

    fun delete()

    fun install()
}