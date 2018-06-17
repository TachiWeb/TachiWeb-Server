package xyz.nulldev.ts.api.v2.java.model.chapters

interface ChaptersController {
    fun getChapters(vararg chapterIds: Long): ChapterCollection
}