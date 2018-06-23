package xyz.nulldev.ts.api.v2.java.model.extensions

import eu.kanade.tachiyomi.source.Source
import xyz.nulldev.ts.api.v2.java.model.PreparedInputStream

interface ExtensionModel : ExtensionLikeModel {
    override val name: String

    override val pkgName: String

    override val status: ExtensionStatus

    override val versionName: String

    override val versionCode: Int

    override val signatureHash: String?

    override val lang: String?

    // TODO API source model
    override val sources: List<Source>?

    override val hasUpdate: Boolean?

    override val icon: Pair<String, PreparedInputStream>?
}
