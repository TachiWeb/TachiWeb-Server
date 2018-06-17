package xyz.nulldev.ts.api.v2.java.model.chapters

interface ChapterModel : ChapterLikeModel {
    override val id: Long

    override var readingStatus: ReadingStatus?
}