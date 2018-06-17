package xyz.nulldev.ts.api.v2.http.chapters

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.Response
import xyz.nulldev.ts.api.v2.http.jvcompat.*
import xyz.nulldev.ts.api.v2.java.model.chapters.ChapterCollection
import xyz.nulldev.ts.api.v2.java.model.chapters.ReadingStatus

object ChaptersController : BaseController() {
    //TODO Swap to Javalin attribute passing
    fun prepareChapterAttributes(ctx: Context) {
        val chaptersParam = ctx.param(CHAPTERS_PARAM)!!

        ctx.attribute(CHAPTERS_ATTR,
                api.chapters.getChapters(*chaptersParam.split(",").map {
                    it.trim().toLong()
                }.toLongArray()))
    }

    private val CHAPTERS_PARAM = ":chapters"
    private val CHAPTERS_ATTR = "chapters"

    fun getReadingStatus(ctx: Context) {
        prepareChapterAttributes(ctx)

        ctx.json(ctx.attribute<ChapterCollection>(CHAPTERS_ATTR).readingStatus)
    }

    fun setReadingStatus(ctx: Context) {
        prepareChapterAttributes(ctx)

        val status = ctx.bodyAsClass<Array<ReadingStatus>>().toList()
        ctx.attribute<ChapterCollection>(CHAPTERS_ATTR).readingStatus = status
        ctx.json(Response.Success())
    }
}