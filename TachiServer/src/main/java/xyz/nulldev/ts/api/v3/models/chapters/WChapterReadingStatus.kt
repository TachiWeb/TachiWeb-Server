package xyz.nulldev.ts.api.v3.models.chapters

data class WChapterReadingStatus(
        val lastPageRead: Long,
        val lastRead: Long?,
        val read: Boolean
)