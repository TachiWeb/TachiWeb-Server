package xyz.nulldev.ts.library;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.SourceManager;
import eu.kanade.tachiyomi.util.ChapterSourceSyncKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 16/08/16
 */
public class LibraryUpdater {

    private static Logger logger = LoggerFactory.getLogger(LibraryUpdater.class);

    private SourceManager sourceManager;

    public LibraryUpdater(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    public void updateLibrary(Library library, boolean updateAll) {
        for(Manga manga : new ArrayList<>(updateAll ? library.getMangas() : library.getFavoriteMangas())) {
            updateManga(library, manga);
        }
    }

    public void updateManga(Library library, Manga manga) {
        Source source = sourceManager.get(manga.getSource());
        if(source == null) {
            logger.info("Manga #{} is missing it's source!", manga.getId());
            return;
        }
        //Update manga info
        try {
            Long originalId = manga.getId();
            String originalTitle = manga.getTitle();
            manga = source.fetchMangaDetails(manga).toBlocking().first();
            if (manga == null) {
                throw new NullPointerException();
            }
            manga.setId(originalId);
            //TODO WHY THE HECK IS THE TITLE NOT SET AFTER THE MANGA IS UPDATED!
            try {
                manga.getTitle();
            } catch (Exception ignored) {
                manga.setTitle(originalTitle);
            }
            //Update the manga in the library
            library.insertManga(manga);
        } catch (Exception e) {
            logger.error("Error updating manga!", e);
            return;
        }
        //Update manga chapters
        try {
            List<Chapter> chapters = source.fetchChapterList(manga).toBlocking().first();
            if(chapters == null) {
                throw new NullPointerException();
            }
            //Sync the library chapters with the source chapters
            ChapterSourceSyncKt.syncChaptersWithSource(library, chapters, manga, source);
        } catch (Exception e) {
            logger.error("Error updating chapters!", e);
        }
    }
}
