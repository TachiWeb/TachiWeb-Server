package xyz.nulldev.ts.sync.protocol.models

import xyz.nulldev.ts.sync.protocol.models.common.ChangedField
import xyz.nulldev.ts.sync.protocol.models.common.SyncEntity

class SyncCategory : SyncEntity<SyncCategory>() {
    // Changes
    var addedManga: ChangedField<List<SyncManga>>? = null
    var deletedManga: ChangedField<List<SyncManga>>? = null
    var flags: ChangedField<Int>? = null

    // Identifiers
    lateinit var name: String
}