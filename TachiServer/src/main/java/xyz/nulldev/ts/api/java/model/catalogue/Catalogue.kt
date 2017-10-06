package xyz.nulldev.ts.api.java.model.catalogue

import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.source.online.LoginSource

interface Catalogue {
    /**
     * List of all catalogue sources
     */
    val sources: List<CatalogueSource>

    /**
     * List of enabled catalogue sources
     */
    val enabledSources: List<CatalogueSource>

    /**
     * List of all online sources
     */
    val onlineSources: List<HttpSource>

    /**
     * List of catalogue sources that require login
     */
    val loginSources: List<LoginSource>

    /**
     * Perform a search on a source or just get the most popular manga
     *
     * @param page The page (0-indexed)
     * @param source The source to fetch the catalogue of
     * @param query The search string (leave empty to fetch the most popular manga)
     * @param filters Filters to be applied on the search query (use null to disable filtering)
     */
    fun getCatalogueContent(page: Int,
                            source: CatalogueSource,
                            query: String = "",
                            filters: FilterList? = null): CataloguePage

    /**
     * Get a source by it's ID
     *
     * @param id The source's ID
     * @return The source or null if not found
     */
    fun getSource(id: Long): Source?

    /**
     * Update a manga's info
     *
     * @param manga The manga to update
     * @return A manga object with the new manga info (same as input manga object as old object is updated)
     */
    fun updateMangaInfo(manga: Manga): Manga

    /**
     * Update a manga's info
     *
     * @param manga The manga to update
     * @return A list of new chapters and list of old chapters
     */
    fun updateMangaChapters(manga: Manga): Pair<List<Chapter>, List<Chapter>>

    /**
     * Get the page list of a chapter
     *
     * @param chapter The chapter to get the page list of
     * @return The list of pages in the chapter
     */
    fun getPageList(chapter: Chapter): List<Page>
}