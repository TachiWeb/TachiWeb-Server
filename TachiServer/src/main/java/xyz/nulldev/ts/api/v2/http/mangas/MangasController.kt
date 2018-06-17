package xyz.nulldev.ts.api.v2.http.mangas

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.Response
import xyz.nulldev.ts.api.v2.http.jvcompat.*
import xyz.nulldev.ts.api.v2.java.model.mangas.MangaCollection

object MangasController : BaseController() {
    //TODO Swap to Javalin attribute passing
    fun prepareMangaAttributes(ctx: Context) {
        val mangasParam = ctx.param(MANGAS_PARAM)

        ctx.attribute(MANGAS_ATTR, if(mangasParam != null)
            api.mangas.get(*mangasParam.split(",").map {
                it.trim().toLong()
            }.toLongArray())
        else
            api.mangas.getAll()
        )
    }

    private val MANGAS_PARAM = ":mangas"
    private val MANGAS_ATTR = "mangas"

    fun getViewer(ctx: Context) {
        prepareMangaAttributes(ctx)

        val mangas = ctx.attribute<MangaCollection>(MANGAS_ATTR)

        ctx.json(Response.Success(mangas.viewer.mapIndexed { index, viewer ->
            MangaViewer(mangas.id[index], viewer)
        }))
    }

    fun setViewer(ctx: Context) {
        prepareMangaAttributes(ctx)

        val mangas = ctx.attribute<MangaCollection>(MANGAS_ATTR)

        val viewer = ctx.bodyAsClass<Array<MangaViewer>>().toList()
        mangas.viewer = mangas.id.map { manga ->
            viewer.find { it.id == manga }?.viewer
        }
        ctx.json(Response.Success())
    }
}
