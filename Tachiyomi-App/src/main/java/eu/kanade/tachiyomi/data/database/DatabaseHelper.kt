package eu.kanade.tachiyomi.data.database

import android.content.Context
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite
import eu.kanade.tachiyomi.data.database.mappers.*
import eu.kanade.tachiyomi.data.database.models.*
import eu.kanade.tachiyomi.data.database.queries.*
import xyz.nulldev.ts.sync.database.mappers.EntryUpdateMapping
import xyz.nulldev.ts.sync.database.models.EntryUpdate
import xyz.nulldev.ts.sync.database.resolvers.EntryUpdateQueries

/**
 * This class provides operations to manage the database through its interfaces.
 */
open class DatabaseHelper(context: Context)
: MangaQueries, ChapterQueries, TrackQueries, CategoryQueries, MangaCategoryQueries, HistoryQueries, EntryUpdateQueries {

    override val db = DefaultStorIOSQLite.builder()
            .sqliteOpenHelper(DbOpenHelper(context))
            .addTypeMapping(EntryUpdate::class.java, EntryUpdateMapping())
            .addTypeMapping(Manga::class.java, MangaTypeMapping())
            .addTypeMapping(Chapter::class.java, ChapterTypeMapping())
            .addTypeMapping(Track::class.java, TrackTypeMapping())
            .addTypeMapping(Category::class.java, CategoryTypeMapping())
            .addTypeMapping(MangaCategory::class.java, MangaCategoryTypeMapping())
            .addTypeMapping(History::class.java, HistoryTypeMapping())
            .build()

    inline fun inTransaction(block: () -> Unit) = db.inTransaction(block)

    fun lowLevel() = db.lowLevel()

}
