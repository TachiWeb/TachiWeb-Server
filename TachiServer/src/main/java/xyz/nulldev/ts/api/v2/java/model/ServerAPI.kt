package xyz.nulldev.ts.api.v2.java.model

import xyz.nulldev.ts.api.v2.java.model.chapters.ChaptersController
import xyz.nulldev.ts.api.v2.java.model.library.LibraryController

interface ServerAPI {
    val library: LibraryController
    val chapters: ChaptersController
}