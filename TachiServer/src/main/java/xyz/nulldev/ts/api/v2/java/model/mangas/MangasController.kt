package xyz.nulldev.ts.api.v2.java.model.mangas

interface MangasController {
    fun get(vararg mangaIds: Long): MangaCollection

    fun getAll(): MangaCollection
}