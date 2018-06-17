package xyz.nulldev.ts.api.v2.java.model.chapters

interface ChapterCollection : ChapterLikeModel, List<ChapterModel> {
    override val id: List<Long>

    override var readingStatus: List<ReadingStatus?>
}