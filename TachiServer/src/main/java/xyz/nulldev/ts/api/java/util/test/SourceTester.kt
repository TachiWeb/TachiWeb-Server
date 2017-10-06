package xyz.nulldev.ts.api.java.util.test

import eu.kanade.tachiyomi.source.CatalogueSource
import xyz.nulldev.ts.api.java.TachiyomiAPI

class SourceTester {
    val api = TachiyomiAPI

    fun test(source: CatalogueSource, eventHandler: (Event) -> Unit) {
        fun debug(message: String) {
            eventHandler(Event.Debug(message))
        }

        fun fail(message: String, e: Throwable? = null) {
            eventHandler(Event.Error(message, e))
        }

        //Test catalogue
        debug("Testing catalogue...")
        debug("Testing page 1 of catalogue...")
        val catalogueContent = try {
            api.catalogue.getCatalogueContent(1, source)
        } catch(e: Exception) {
            fail("Could not get page 1 of catalogue!", e)
            return
        }

        debug("Testing update manga info/chapters...")
        catalogueContent.manga.take(10).forEachIndexed { index, manga ->
            debug("Testing manga $index...")
            debug("Testing update manga $index info...")
            try {
                api.catalogue.updateMangaInfo(manga)
            } catch(e: Exception) {
                fail("Could not update manga $index info...", e)
                return
            }
            debug("Testing update manga $index chapters...")
            try {
                api.catalogue.updateMangaChapters(manga)
            } catch(e: Exception) {
                fail("Could not update manga $index chapters...", e)
                return
            }

            debug("Testing get cover of manga $index...")
            val cover = try {
                api.images.fetchCover(manga, NOOPOutputStream())
            } catch(e: Exception) {
                fail("Could not get cover of manga $index...", e)
                return
            }

            debug("Testing get page list of chapter 1...")
            val chapter = api.database.getChapters(manga).executeAsBlocking()[0]
            val pageList = try {
                api.catalogue.getPageList(chapter)
            } catch(e: Exception) {
                fail("Could not get page list of chapter 1...", e)
                return
            }


            debug("Testing get image of page 1...")
            val image = try {
                api.images.fetchImage(chapter, pageList[0], NOOPOutputStream())
            } catch(e: Exception) {
                fail("Could not get image of page 1...", e)
                return
            }
        }
    }
}