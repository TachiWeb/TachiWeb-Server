package xyz.nulldev.ts.api.v2.java.model.mangas

interface MangaCollection : MangaLikeModel, List<MangaModel> {
    override val id: List<Long>

    override var viewer: List<MangaViewer?>
}
