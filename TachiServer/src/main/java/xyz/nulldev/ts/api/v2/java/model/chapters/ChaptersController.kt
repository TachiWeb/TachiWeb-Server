package xyz.nulldev.ts.api.v2.java.model.chapters

interface ChaptersController {
    fun get(vararg chapterIds: Long): ChapterCollection

    fun getAll(): ChapterCollection
}