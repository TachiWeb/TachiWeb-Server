package xyz.nulldev.ts.api.v2.http.chapters

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.Response
import xyz.nulldev.ts.api.v2.http.jvcompat.*
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

        val chapters = ctx.attribute<ChapterCollection>(CHAPTERS_ATTR)

        ctx.json(Response.Success(chapters.readingStatus.mapIndexed { index, readingStatus ->
            ChapterReadingStatus(chapters.id[index], readingStatus)
        }))
    }

    fun setReadingStatus(ctx: Context) {
        prepareChapterAttributes(ctx)

        val chapters = ctx.attribute<ChapterCollection>(CHAPTERS_ATTR)

        val status = ctx.bodyAsClass<Array<ChapterReadingStatus>>().toList()
        chapters.readingStatus = chapters.id.map { chapter ->
            status.find { it.id == chapter }?.readingStatus
        }
        ctx.json(Response.Success())
    }
}