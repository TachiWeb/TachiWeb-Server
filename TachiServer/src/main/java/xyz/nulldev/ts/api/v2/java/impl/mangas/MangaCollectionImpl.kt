package xyz.nulldev.ts.api.v2.java.impl.mangas

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.ts.api.v2.java.impl.util.DbMapper
import xyz.nulldev.ts.api.v2.java.impl.util.ProxyList
import xyz.nulldev.ts.api.v2.java.model.mangas.MangaCollection
import xyz.nulldev.ts.api.v2.java.model.mangas.MangaModel
import xyz.nulldev.ts.api.v2.java.model.mangas.MangaViewer
import xyz.nulldev.ts.ext.kInstanceLazy

class MangaCollectionImpl(override val id: List<Long>): MangaCollection,
    List<MangaModel> by ProxyList(id, { MangaCollectionProxy(it) }) {
    private val db: DatabaseHelper by kInstanceLazy()

    private val dbMapper = DbMapper(
            id,
            dbGetter = { db.getManga(it).executeAsBlocking() },
            dbSetter = { db.insertManga(it).executeAsBlocking() }
    )

    override var viewer: List<MangaViewer?>
        get() = dbMapper.mapGet {
            MangaViewer.values()[it.viewer]
        }
        set(value) = dbMapper.mapSet(value) { manga, viewer ->
            manga.viewer = viewer.ordinal
        }
}

class MangaCollectionProxy(override val id: Long): MangaModel {
    private val collection = MangaCollectionImpl(listOf(id))

    override var viewer: MangaViewer?
        get() = collection.viewer[0]
        set(value) { collection.viewer = listOf(value) }
}