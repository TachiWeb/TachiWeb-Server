package xyz.nulldev.ts.sync.database.mappers

import android.content.ContentValues
import android.database.Cursor
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.InsertQuery
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery
import xyz.nulldev.ts.sync.database.SyncUpdatesTable.COL_DATETIME
import xyz.nulldev.ts.sync.database.SyncUpdatesTable.COL_FIELD
import xyz.nulldev.ts.sync.database.SyncUpdatesTable.COL_ID
import xyz.nulldev.ts.sync.database.SyncUpdatesTable.COL_UPDATED_ROW
import xyz.nulldev.ts.sync.database.SyncUpdatesTable.TABLE
import xyz.nulldev.ts.sync.database.models.EntryUpdate
import xyz.nulldev.ts.sync.database.models.EntryUpdateImpl
import xyz.nulldev.ts.sync.database.models.UpdateTarget

class EntryUpdateMapping : SQLiteTypeMapping<EntryUpdate>(
        EntryUpdatePutResolver(),
        EntryUpdateGetResolver(),
        EntryUpdateDeleteResolver()
)

class EntryUpdatePutResolver : DefaultPutResolver<EntryUpdate>() {

    override fun mapToInsertQuery(obj: EntryUpdate) = InsertQuery.builder()
            .table(TABLE)
            .build()

    override fun mapToUpdateQuery(obj: EntryUpdate) = UpdateQuery.builder()
            .table(TABLE)
            .where("$COL_ID = ?")
            .whereArgs(obj.id)
            .build()

    override fun mapToContentValues(obj: EntryUpdate) = ContentValues(4).apply {
        put(COL_ID, obj.id)
        put(COL_UPDATED_ROW, obj.updatedRow)
        put(COL_DATETIME, obj.datetime)
        put(COL_FIELD, obj.field.id)
    }
}

class EntryUpdateGetResolver : DefaultGetResolver<EntryUpdate>() {

    override fun mapFromCursor(cursor: Cursor): EntryUpdate = EntryUpdateImpl().apply {
        id = cursor.getLong(cursor.getColumnIndex(COL_ID))
        updatedRow = cursor.getLong(cursor.getColumnIndex(COL_UPDATED_ROW))
        datetime = cursor.getLong(cursor.getColumnIndex(COL_DATETIME))
        field = UpdateTarget.find(cursor.getInt(cursor.getColumnIndex(COL_FIELD)))!!
    }
}

class EntryUpdateDeleteResolver : DefaultDeleteResolver<EntryUpdate>() {

    override fun mapToDeleteQuery(obj: EntryUpdate) = DeleteQuery.builder()
            .table(TABLE)
            .where("$COL_ID = ?")
            .whereArgs(obj.id)
            .build()
}
