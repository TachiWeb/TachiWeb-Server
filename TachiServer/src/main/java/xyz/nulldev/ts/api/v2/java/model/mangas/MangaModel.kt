package xyz.nulldev.ts.api.v2.java.model.mangas

interface MangaModel : MangaLikeModel {
    override val id: Long

    override var viewer: MangaViewer?
}
