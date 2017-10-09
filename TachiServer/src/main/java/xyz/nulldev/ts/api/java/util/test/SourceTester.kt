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

        fun warn(message: String, e: Throwable? = null) {
            eventHandler(Event.Warning(message, e))
        }

        //Test catalogue (3 pages)
        debug("Testing catalogue...")
        debug("Testing page 1 of catalogue...")
        val catalogueContent = try {
            api.catalogue.getCatalogueContent(1, source)
        } catch(e: Exception) {
            fail("Could not get page 1 of catalogue!", e)
            return
        }
        val mergedCatalogues = catalogueContent.manga.toMutableList()
        if(catalogueContent.nextPage != null) {
            debug("Testing page 2 of catalogue...")
            val catalogueContent2 = try {
                api.catalogue.getCatalogueContent(2, source)
            } catch (e: Exception) {
                fail("Could not get page 2 of catalogue!", e)
                return
            }
            mergedCatalogues += catalogueContent2.manga
            if(catalogueContent2.nextPage != null) {
                debug("Testing page 3 of catalogue...")
                val catalogueContent3 = try {
                    api.catalogue.getCatalogueContent(3, source)
                } catch (e: Exception) {
                    fail("Could not get page 3 of catalogue!", e)
                    return
                }
                mergedCatalogues += catalogueContent3.manga
            }
        } else warn("Catalogue appears to only have one page, please confirm that this is intended!")

        //Verify that there are no duplicates in the catalogue
        if(mergedCatalogues.any { a ->
            mergedCatalogues.any { b ->
                //Not same object but same URL?
                a !== b && a.url == b.url
            }
        }) {
            fail("Duplicate manga detected in catalogue!")
            return
        }

        debug("Testing update manga info/chapters...")
        catalogueContent.manga.take(10).forEachIndexed { index, manga ->
            debug("Testing manga $index (${manga.url})...")
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
                warn("Could not update manga $index chapters...", e)
            }

            debug("Testing get cover of manga $index...")
            val cover = try {
                api.images.fetchCover(manga, NOOPOutputStream())
            } catch(e: Exception) {
                warn("Could not get cover of manga $index...", e)
            }

            debug("Testing get page list of chapter 1...")
            val chapter = api.database.getChapters(manga).executeAsBlocking().getOrNull(0)
            if(chapter != null) {
                val pageList = try {
                    api.catalogue.getPageList(chapter)
                } catch (e: Exception) {
                    warn("Could not get page list of chapter 1...", e)
                    null
                }

                if (pageList != null) {
                    if(pageList.isNotEmpty()) {
                        debug("Testing get image of page 1...")
                        val image = try {
                            api.images.fetchImage(chapter, pageList[0], NOOPOutputStream())
                        } catch (e: Exception) {
                            warn("Could not get image of page 1...", e)
                        }
                    } else warn("Page list empty!")
                }
            } else warn("Chapter 1 does not exist!")
        }
    }
}