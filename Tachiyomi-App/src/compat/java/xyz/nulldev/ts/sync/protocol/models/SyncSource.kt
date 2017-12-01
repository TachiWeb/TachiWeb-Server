package xyz.nulldev.ts.sync.protocol.models

import xyz.nulldev.ts.sync.protocol.models.common.SyncEntity

class SyncSource : SyncEntity<SyncSource>() {
    // Identifiers
    var id: Long = -1
    lateinit var name: String
}