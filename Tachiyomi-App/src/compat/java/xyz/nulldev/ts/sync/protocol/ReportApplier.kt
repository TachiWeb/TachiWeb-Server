package xyz.nulldev.ts.sync.protocol

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.History
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.SourceManager
import uy.kohesive.injekt.injectLazy
import xyz.nulldev.ts.sync.protocol.models.SyncChapter
import xyz.nulldev.ts.sync.protocol.models.SyncHistory
import xyz.nulldev.ts.sync.protocol.models.SyncManga
import xyz.nulldev.ts.sync.protocol.models.SyncReport

class ReportApplier {
    private val db: DatabaseHelper by injectLazy()
    private val sources: SourceManager by injectLazy()

    fun apply(report: SyncReport) {
        //TODO Match sources
        db.inTransaction {
            //Must be in order as some entites depend on previous entities to already be
            //in DB!
            applyManga(report)
            applyChapters(report)
            applyHistory(report)
        }
    }

    private fun applyManga(report: SyncReport) {
        report.findEntities<SyncManga>().forEach {
            val source = it.source.resolve(report)
            val dbManga = db.getManga(it.url, source.id).executeAsBlocking() ?: Manga.create(source.id).apply {
                url = it.url
                title = it.name
            }

            it.favorite?.let { dbManga.favorite = it.value }
            it.viewer?.let { dbManga.viewer = it.value }
            it.chapterFlags?.let { dbManga.chapter_flags = it.value }

            db.insertManga(dbManga)
        }
    }

    private fun applyChapters(report: SyncReport) {
        report.findEntities<SyncChapter>().forEach {
            val dbChapter = db.getChapter(it.url).executeAsBlocking() ?: Chapter.create().apply {
                val manga = it.manga.resolve(report)
                val source = manga.source.resolve(report)
                val dbManga = db.getManga(manga.url, source.id).executeAsBlocking()!! //TODO

                url = it.url
                name = it.name
                chapter_number = it.chapterNum
                manga_id = dbManga.id
                source_order = it.sourceOrder
            }

            it.read?.let { dbChapter.read = it.value }
            it.bookmark?.let { dbChapter.bookmark = it.value }
            it.lastPageRead?.let { dbChapter.last_page_read = it.value }

            db.insertChapter(dbChapter)
        }
    }

    private fun applyHistory(report: SyncReport) {
        report.findEntities<SyncHistory>().forEach {
            val chapter = it.chapter.resolve(report)
            val dbHistory = db.getHistoryByChapterUrl(chapter.url).executeAsBlocking() ?: run {
                val dbChapter = db.getChapter(chapter.url).executeAsBlocking()!! //TODO
                History.create(dbChapter)
            }

            it.lastRead?.let { dbHistory.last_read = it.value }

            db.insertHistory(dbHistory)
        }
    }
}
