package xyz.nulldev.ts.api.v2.http.mangas

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.Response
import xyz.nulldev.ts.api.v2.http.jvcompat.*
import xyz.nulldev.ts.api.v2.java.model.mangas.MangaCollection
import xyz.nulldev.ts.api.v2.java.model.mangas.MangaViewer

object MangasController : BaseController() {
    //TODO Swap to Javalin attribute passing
    fun prepareMangaAttributes(ctx: Context) {
        val mangasParam = ctx.param(MANGAS_PARAM)!!

        ctx.attribute(MANGAS_ATTR,
                api.mangas.getMangas(*mangasParam.split(",").map {
                    it.trim().toLong()
                }.toLongArray()))
    }

    private val MANGAS_PARAM = ":mangas"
    private val MANGAS_ATTR = "mangas"

    fun getViewer(ctx: Context) {
        prepareMangaAttributes(ctx)

        ctx.json(Response.Success(ctx.attribute<MangaCollection>(MANGAS_ATTR).viewer))
    }

    fun setViewer(ctx: Context) {
        prepareMangaAttributes(ctx)

        val viewer = ctx.bodyAsClass<Array<MangaViewer?>>().toList()
        ctx.attribute<MangaCollection>(MANGAS_ATTR).viewer = viewer
        ctx.json(Response.Success())
    }
}
