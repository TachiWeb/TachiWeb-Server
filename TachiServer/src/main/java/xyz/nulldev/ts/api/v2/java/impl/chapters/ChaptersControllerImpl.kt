package xyz.nulldev.ts.api.v2.java.impl.chapters

import xyz.nulldev.ts.api.v2.java.model.chapters.ChaptersController

class ChaptersControllerImpl : ChaptersController {
    override fun getChapters(vararg chapterIds: Long)
        = ChapterCollectionImpl(chapterIds.toList())
}
