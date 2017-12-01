package xyz.nulldev.ts.sync.database.models

class UpdatableField(val parent: Updatable, val id: Int, val field: String, val defValue: Any)