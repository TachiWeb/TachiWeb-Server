package xyz.nulldev.ts.api.v2.java.impl

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.inTransactionReturn
import xyz.nulldev.ts.api.v2.java.impl.chapters.ChaptersControllerImpl
import xyz.nulldev.ts.api.v2.java.impl.library.LibraryControllerImpl
import xyz.nulldev.ts.api.v2.java.impl.mangas.MangasControllerImpl
import xyz.nulldev.ts.api.v2.java.model.ServerAPI
import xyz.nulldev.ts.ext.kInstanceLazy

class ServerAPIImpl : ServerAPI {
    private val db: DatabaseHelper by kInstanceLazy()

    override val library = LibraryControllerImpl()
    override val chapters = ChaptersControllerImpl()
    override val mangas = MangasControllerImpl()

    fun <T> transaction(block: () -> T) {
        db.db.inTransactionReturn(block)
    }
}