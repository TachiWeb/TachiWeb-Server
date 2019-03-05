package xyz.nulldev.ts.api.java.impl.catalogue

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.LocalSource
import eu.kanade.tachiyomi.source.SourceInjector
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.source.online.LoginSource
import eu.kanade.tachiyomi.util.syncChaptersWithSource
import xyz.nulldev.ts.api.java.model.catalogue.Catalogue
import xyz.nulldev.ts.api.java.model.catalogue.CataloguePage
import xyz.nulldev.ts.api.java.util.*
import xyz.nulldev.ts.ext.fetchPageListFromCacheThenNet
import xyz.nulldev.ts.ext.kInstanceLazy
import java.util.*

class CatalogueImpl : Catalogue {
    private val sourceManager: SourceManager by kInstanceLazy()
    private val sourceInjector by lazy {
        SourceInjector(sourceManager)
    }
    private val downloadManager: DownloadManager by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()
    private val prefs: PreferencesHelper by kInstanceLazy()

    override val sources: List<CatalogueSource>
        get() = sourceManager.getCatalogueSources()
    override val enabledSources: List<CatalogueSource>
        get() {
            val languages = prefs.enabledLanguages().getOrDefault()
            val hiddenCatalogues = prefs.hiddenCatalogues().getOrDefault()

            return sources
                    .filter { it.lang in languages }
                    .filterNot { it.id.toString() in hiddenCatalogues }
                    .sortedBy { "(${it.lang}) ${it.name}" } +
                    sourceManager.get(LocalSource.ID) as LocalSource
        }
    override val onlineSources: List<HttpSource>
        get() = sources.filterIsInstance<HttpSource>()
    override val loginSources: List<LoginSource>
        get() = sources.filterIsInstance<LoginSource>()

    override fun getCatalogueContent(page: Int, source: CatalogueSource, query: String, filters: FilterList?): CataloguePage {
        //Get catalogue from source
        //TODO Possibly compare filters to see if filters have changed?
        val observable = if (query.isNotBlank() || filters != null) {
            source.fetchSearchManga(page, query, filters ?: source.getFilterList())
        } else {
            source.fetchPopularManga(page)
        }
        //Actually get manga from catalogue
        val pageObj = observable.toBlocking().first()

        val manga = pageObj.mangas.map { networkToLocalManga(it, source.id) }
        return CataloguePage(manga, page, if(pageObj.hasNextPage) page + 1 else null)
    }

    private fun networkToLocalManga(sManga: SManga, sourceId: Long): Manga {
        var localManga = db.getManga(sManga.url, sourceId).executeAsBlocking()
        if (localManga == null) {
            val newManga = Manga.create(sManga.url, sManga.title, sourceId)
            newManga.copyFrom(sManga)
            val result = db.insertManga(newManga).executeAsBlocking()
            newManga.id = result.insertedId()
            localManga = newManga
        }
        return localManga
    }

    override fun getSource(id: Long) = sourceManager.get(id)

    override fun registerSource(source: CatalogueSource) {
        sourceInjector.registerSource(source)
    }

    override fun updateMangaInfo(manga: Manga): Manga {
        manga.copyFrom(manga.sourceObj.ensureLoaded().fetchMangaDetails(manga).toBlocking().first())
        db.insertManga(manga).executeAsBlocking()
        return manga
    }

    override fun updateMangaChapters(manga: Manga): Pair<List<Chapter>, List<Chapter>> {
        val sourceObj = manga.sourceObj.ensureLoaded()
        val result = syncChaptersWithSource(db,
                sourceObj.fetchChapterList(manga).toBlocking().first(),
                manga,
                sourceObj)

        //If we find new chapters, update the "last update" field in the manga object
        if(result.first.isNotEmpty() || result.second.isNotEmpty()) {
            manga.last_update = Date().time
            db.updateLastUpdated(manga).executeAsBlocking()
        }

        return result
    }

    override fun getPageList(chapter: Chapter): List<Page> {
        val manga = chapter.manga.ensureInDatabase()
        val sourceObj = manga.sourceObj.ensureLoaded()
        return if (chapter.isDownloaded) {
            // Fetch the page list from disk.
            downloadManager.buildPageList(sourceObj, manga, chapter)
        } else {
            (sourceObj as? HttpSource)?.fetchPageListFromCacheThenNet(chapter)
                    ?: sourceObj.fetchPageList(chapter)
        }.toBlocking().first()
    }
}