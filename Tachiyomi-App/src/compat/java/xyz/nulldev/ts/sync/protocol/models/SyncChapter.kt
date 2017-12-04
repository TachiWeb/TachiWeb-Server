package xyz.nulldev.ts.sync.protocol.models

import xyz.nulldev.ts.sync.protocol.models.common.ChangedField
import xyz.nulldev.ts.sync.protocol.models.common.SyncEntity
import xyz.nulldev.ts.sync.protocol.models.common.SyncRef

class SyncChapter : SyncEntity<SyncChapter>() {
    // Changes
    var read: ChangedField<Boolean>? = null
    var bookmark: ChangedField<Boolean>? = null
    var lastPageRead: ChangedField<Int>? = null

    // Identifiers
    lateinit var manga: SyncRef<SyncManga>
    var chapterNum: Float = -1f
    var sourceOrder: Int = -1
    lateinit var url: String
    lateinit var name: String
}