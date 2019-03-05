package xyz.nulldev.ts.ext

import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.source.Source
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

fun Source.fetchPageListFromCacheThenNet(chapter: Chapter) = Injekt.get<ChapterCache>()
        .getPageListFromCache(chapter)
        .onErrorResumeNext { fetchPageList(chapter) }