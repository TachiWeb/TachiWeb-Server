package xyz.nulldev.ts.sync.protocol.models

import xyz.nulldev.ts.sync.protocol.models.common.ChangedField
import xyz.nulldev.ts.sync.protocol.models.common.SyncEntity
import xyz.nulldev.ts.sync.protocol.models.common.SyncRef

class SyncHistory : SyncEntity<SyncHistory>() {
    // Changes
    var lastRead: ChangedField<Long>? = null

    // Identifiers
    lateinit var chapter: SyncRef<SyncChapter>
}