package xyz.nulldev.ts.api.v2.http.mangas

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.jvcompat.Context
import xyz.nulldev.ts.api.v2.http.jvcompat.attribute
import xyz.nulldev.ts.api.v2.http.jvcompat.param
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

        getApiField(ctx,
                MANGAS_ATTR,
                MangaCollection::id,
                MangaCollection::viewer,
                MangaViewer::class)
    }

    fun setViewer(ctx: Context) {
        prepareMangaAttributes(ctx)

        setApiField(ctx,
                MANGAS_ATTR,
                MangaCollection::id,
                MangaCollection::viewer,
                MangaViewer::id,
                MangaViewer::viewer)
    }
}
