package xyz.nulldev.ts.api.v3.models.chapters

data class WChapter(
        val bookmarked: Boolean,
        val chapterNumber: Float?,
        val dateFetch: Long,
        val dateUpload: Long?,
        val id: Long,
        val mangaId: Long,
        val name: String,
        val readingStatus: WChapterReadingStatus,
        val scanlator: String?,
        val sourceOrder: Long?,
        val url: String?
)