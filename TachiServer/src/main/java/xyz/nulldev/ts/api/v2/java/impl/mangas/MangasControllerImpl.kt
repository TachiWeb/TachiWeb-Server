package xyz.nulldev.ts.api.v2.java.impl.mangas

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.java.model.mangas.MangasController

class MangasControllerImpl : MangasController, BaseController() {
    override fun getMangas(vararg mangaIds: Long)
            = MangaCollectionImpl(mangaIds.toList()) // TODO Check these chapters exist
}