package xyz.nulldev.ts.api.v2.java.impl.util

import com.pushtorefresh.storio.sqlite.queries.Query
import eu.kanade.tachiyomi.data.database.DbProvider
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.tables.ChapterTable

fun DbProvider.getChapters() = db.get()
        .listOfObjects(Chapter::class.java)
        .withQuery(Query.builder()
                .table(ChapterTable.TABLE)
                .build())
        .prepare()
