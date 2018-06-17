package xyz.nulldev.ts.api.v2.java.impl.chapters

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.java.impl.util.getChapters
import xyz.nulldev.ts.api.v2.java.model.chapters.ChaptersController
import xyz.nulldev.ts.ext.kInstanceLazy

class ChaptersControllerImpl : ChaptersController, BaseController() {
    private val db: DatabaseHelper by kInstanceLazy()

    override fun get(vararg chapterIds: Long)
        = ChapterCollectionImpl(chapterIds.toList()) // TODO Check these chapters exist

    override fun getAll()
        = ChapterCollectionImpl(db.getChapters().executeAsBlocking().map {
        it.id!!
    }.toList())
}
