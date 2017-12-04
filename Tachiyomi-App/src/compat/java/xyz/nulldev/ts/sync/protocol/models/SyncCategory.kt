package xyz.nulldev.ts.sync.protocol.models

import xyz.nulldev.ts.sync.protocol.models.common.ChangedField
import xyz.nulldev.ts.sync.protocol.models.common.SyncEntity
import xyz.nulldev.ts.sync.protocol.models.common.SyncRef

class SyncCategory : SyncEntity<SyncCategory>() {
    // Changes
    var addedManga: ChangedField<List<SyncRef<SyncManga>>>? = null
    var deletedManga: ChangedField<List<SyncRef<SyncManga>>>? = null
    var flags: ChangedField<Int>? = null

    // Identifiers
    lateinit var name: String
}