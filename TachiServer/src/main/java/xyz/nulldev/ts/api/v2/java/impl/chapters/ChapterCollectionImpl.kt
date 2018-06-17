package xyz.nulldev.ts.api.v2.java.impl.chapters

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.ts.api.v2.java.impl.util.DbMapper
import xyz.nulldev.ts.api.v2.java.impl.util.ProxyList
import xyz.nulldev.ts.api.v2.java.model.chapters.ChapterCollection
import xyz.nulldev.ts.api.v2.java.model.chapters.ChapterModel
import xyz.nulldev.ts.api.v2.java.model.chapters.ReadingStatus
import xyz.nulldev.ts.ext.kInstanceLazy

class ChapterCollectionImpl(override val id: List<Long>): ChapterCollection,
        List<ChapterModel> by ProxyList(id, { ChapterCollectionProxy(it) }) {
    private val db: DatabaseHelper by kInstanceLazy()

    private val dbMapper = DbMapper(
            id,
            dbGetter = { db.getChapter(it).executeAsBlocking() },
            dbSetter = { db.insertChapter(it).executeAsBlocking() }
    )

    override var readingStatus: List<ReadingStatus?>
        get() = dbMapper.mapGet {
            ReadingStatus(it.last_page_read, it.read)
        }
        set(value) = dbMapper.mapSet(value) { chapter, status ->
            status.lastPageRead?.let {
                chapter.last_page_read = it
            }

            status.read?.let {
                chapter.read = it
            }
        }

    override val size = id.size
}

class ChapterCollectionProxy(override val id: Long) : ChapterModel {
    private val collection = ChapterCollectionImpl(listOf(id))

    override var readingStatus: ReadingStatus?
        get() = collection.readingStatus[0]
        set(value) { collection.readingStatus = listOf(value) }
}