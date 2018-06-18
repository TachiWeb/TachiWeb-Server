package xyz.nulldev.ts.api.v2.http.chapters

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.jvcompat.Context
import xyz.nulldev.ts.api.v2.http.jvcompat.attribute
import xyz.nulldev.ts.api.v2.http.jvcompat.param
import xyz.nulldev.ts.api.v2.java.model.chapters.ChapterCollection

object ChaptersController : BaseController() {
    //TODO Swap to Javalin attribute passing
    fun prepareChapterAttributes(ctx: Context) {
        val chaptersParam = ctx.param(CHAPTERS_PARAM)

        ctx.attribute(CHAPTERS_ATTR, if(chaptersParam != null)
            api.chapters.get(*chaptersParam.split(",").map {
                it.trim().toLong()
            }.toLongArray())
        else
            api.chapters.getAll()
        )
    }

    private val CHAPTERS_PARAM = ":chapters"
    private val CHAPTERS_ATTR = "chapters"

    fun getReadingStatus(ctx: Context) {
        prepareChapterAttributes(ctx)

        getApiField(ctx,
                CHAPTERS_ATTR,
                ChapterCollection::id,
                ChapterCollection::readingStatus,
                ChapterReadingStatus::class)
    }

    fun setReadingStatus(ctx: Context) {
        prepareChapterAttributes(ctx)

        setApiField(ctx,
                CHAPTERS_ATTR,
                ChapterCollection::id,
                ChapterCollection::readingStatus,
                ChapterReadingStatus::id,
                ChapterReadingStatus::readingStatus)
    }
}