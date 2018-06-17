package xyz.nulldev.ts.api.v2.java.impl.mangas

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.java.model.mangas.MangasController
import xyz.nulldev.ts.ext.kInstanceLazy

class MangasControllerImpl : MangasController, BaseController() {
    private val db: DatabaseHelper by kInstanceLazy()

    override fun get(vararg mangaIds: Long)
            = MangaCollectionImpl(mangaIds.toList()) // TODO Check these mangas exist

    override fun getAll()
            = MangaCollectionImpl(db.getMangas().executeAsBlocking().map {
        it.id!!
    }.toList())
}