package xyz.nulldev.ts.api.v2.java.impl.chapters

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.java.model.chapters.ChaptersController

class ChaptersControllerImpl : ChaptersController, BaseController() {
    override fun getChapters(vararg chapterIds: Long)
        = ChapterCollectionImpl(chapterIds.toList()) // TODO Check these chapters exist
}
