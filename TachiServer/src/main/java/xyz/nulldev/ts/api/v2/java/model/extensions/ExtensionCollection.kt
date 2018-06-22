package xyz.nulldev.ts.api.v2.java.model.extensions

import eu.kanade.tachiyomi.source.Source

interface ExtensionCollection : ExtensionLikeModel, List<ExtensionModel> {
    override val name: List<String>

    override val pkgName: List<String>

    override val status: List<ExtensionStatus>

    override val versionName: List<String>

    override val versionCode: List<Int>

    override val signatureHash: List<String?>

    override val lang: List<String?>

    // TODO API source model
    override val sources: List<List<Source>?>

    override val hasUpdate: List<Boolean?>
}