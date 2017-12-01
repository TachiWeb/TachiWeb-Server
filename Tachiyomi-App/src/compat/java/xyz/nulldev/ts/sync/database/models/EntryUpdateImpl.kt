package xyz.nulldev.ts.sync.database.models

class EntryUpdateImpl : EntryUpdate {
    override var id: Long? = null

    override var updatedRow: Long = -1L

    override var datetime: Long = -1L

    override lateinit var field: UpdatableField
}