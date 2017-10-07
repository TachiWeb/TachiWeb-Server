package eu.kanade.tachiyomi.source

import eu.kanade.tachiyomi.source.model.*
import org.junit.Before
import org.junit.Test

import xyz.nulldev.ts.TachiServer
import xyz.nulldev.ts.api.java.TachiyomiAPI
import xyz.nulldev.ts.ext.kInstance
import kotlin.test.assertTrue

class SourceInjectorTest {
    @Before
    fun setUp() {
        TachiServer().initInternals()
    }

    @Test
    fun registerSource() {
        val newId = (TachiyomiAPI.catalogue.sources.maxBy { it.id }?.id ?: 0) + 1
        val source = object : CatalogueSource {
            override val lang = "en"
            override val supportsLatest = false

            override fun fetchPopularManga(page: Int) = error("Not implemented")
            override fun fetchSearchManga(page: Int, query: String, filters: FilterList) = error("Not implemented")
            override fun fetchLatestUpdates(page: Int) = error("Not implemented")
            override fun getFilterList() = FilterList()

            override val id = newId
            override val name = "Test source"
            override fun fetchMangaDetails(manga: SManga) = error("Not implemented")

            override fun fetchChapterList(manga: SManga) = error("Not implemented")

            override fun fetchPageList(chapter: SChapter) = error("Not implemented")
        }
        SourceInjector(kInstance()).registerSource(source)
        assertTrue(TachiyomiAPI.catalogue.sources.contains(source), "Test source was not injected!")
    }
}