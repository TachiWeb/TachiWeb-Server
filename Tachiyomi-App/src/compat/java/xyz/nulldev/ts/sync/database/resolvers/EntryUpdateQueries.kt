package xyz.nulldev.ts.sync.database.resolvers

import com.pushtorefresh.storio.sqlite.queries.Query
import eu.kanade.tachiyomi.data.database.DbProvider
import xyz.nulldev.ts.sync.database.SyncUpdatesTable
import xyz.nulldev.ts.sync.database.models.EntryUpdate
import xyz.nulldev.ts.sync.database.models.UpdatableField
import xyz.nulldev.ts.sync.protocol.models.SyncReport

interface EntryUpdateQueries : DbProvider {

    fun getEntryUpdates() = db.get()
            .listOfObjects(EntryUpdate::class.java)
            .withQuery(Query.builder()
                    .table(SyncUpdatesTable.TABLE)
                    .orderBy(SyncUpdatesTable.COL_DATETIME)
                    .build())
            .prepare()

    fun getEntryUpdatesForField(report: SyncReport, field: UpdatableField) = db.get()
            .listOfObjects(EntryUpdate::class.java)
            .withQuery(Query.builder()
                    .table(SyncUpdatesTable.TABLE)
                    .orderBy(SyncUpdatesTable.COL_DATETIME)
                    .where("${SyncUpdatesTable.COL_DATETIME} BETWEEN ? and ? AND ${SyncUpdatesTable.COL_FIELD} = ?")
                    .whereArgs(report.from, report.to, field.id)
                    .build())
            .prepare()

    fun insertEntryUpdate(update: EntryUpdate) = db.put().`object`(update).prepare()

    fun deleteEntryUpdate(update: EntryUpdate) = db.delete().`object`(update).prepare()
}
