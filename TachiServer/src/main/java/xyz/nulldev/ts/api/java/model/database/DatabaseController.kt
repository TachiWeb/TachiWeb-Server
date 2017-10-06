package xyz.nulldev.ts.api.java.model.database

import com.pushtorefresh.storio.sqlite.StorIOSQLite
import eu.kanade.tachiyomi.data.database.queries.*

interface DatabaseController : MangaQueries,
        ChapterQueries,
        TrackQueries,
        CategoryQueries,
        MangaCategoryQueries,
        HistoryQueries {
    fun inTransaction(block: () -> Unit)

    fun lowLevel(): StorIOSQLite.LowLevel
}