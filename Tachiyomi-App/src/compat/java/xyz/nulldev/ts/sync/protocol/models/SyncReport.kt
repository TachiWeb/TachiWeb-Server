package xyz.nulldev.ts.sync.protocol.models

import xyz.nulldev.ts.sync.protocol.models.common.SyncEntity

class SyncReport {
    // Used when building report
    @Transient
    var lastId = 0L

    var entities: MutableList<SyncEntity<*>> = mutableListOf()

    // Sync all entities from this date
    var from: Long = -1 //Datetime in millis since epoch in UTC

    // Sync all entities up to this date
    var to: Long = -1 //Datetime in millis since epoch in UTC

    inline fun <reified M : SyncEntity<*>> findEntity(filter: (M) -> Boolean)
        = entities.filterIsInstance<M>().find(filter)
}