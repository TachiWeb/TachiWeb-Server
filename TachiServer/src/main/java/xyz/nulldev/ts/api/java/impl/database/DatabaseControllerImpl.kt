package xyz.nulldev.ts.api.java.impl.database

import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.ts.api.java.model.database.DatabaseController
import xyz.nulldev.ts.ext.kInstanceLazy

class DatabaseControllerImpl : DatabaseController {
    private val database: DatabaseHelper by kInstanceLazy()

    override val db: DefaultStorIOSQLite
        get() = database.db

    override fun inTransaction(block: () -> Unit) = database.inTransaction(block)

    override fun lowLevel() = database.lowLevel()
}