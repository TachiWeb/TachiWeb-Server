package xyz.nulldev.ts.api.v2.java.model

import xyz.nulldev.ts.api.v2.java.model.chapters.ChaptersController
import xyz.nulldev.ts.api.v2.java.model.library.LibraryController
import xyz.nulldev.ts.api.v2.java.model.mangas.MangasController

interface ServerAPI {
    val library: LibraryController
    val chapters: ChaptersController
    val mangas: MangasController
}