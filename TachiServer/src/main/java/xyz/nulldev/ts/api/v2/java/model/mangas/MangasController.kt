package xyz.nulldev.ts.api.v2.java.model.mangas

interface MangasController {
    fun getMangas(vararg mangaIds: Long): MangaCollection
}