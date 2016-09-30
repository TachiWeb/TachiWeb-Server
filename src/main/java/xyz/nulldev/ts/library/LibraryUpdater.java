/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.library;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.SourceManager;
import eu.kanade.tachiyomi.util.ChapterSourceSyncKt;
import kotlin.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uy.kohesive.injekt.InjektKt;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 16/08/16
 */
public class LibraryUpdater {

    private static Logger logger = LoggerFactory.getLogger(LibraryUpdater.class);

    private SourceManager sourceManager = InjektKt.getInjekt().getInstance(SourceManager.class);

    public void updateLibrary(Library library, boolean updateAll) {
        for (Manga manga :
                new ArrayList<>(updateAll ? library.getMangas() : library.getFavoriteMangas())) {
            silentUpdateMangaInfo(library, manga);
            silentUpdateChapters(library, manga);
        }
    }

    public void silentUpdateMangaInfo(Library library, Manga manga) {
        Source source = sourceManager.get(manga.getSource());
        if (source == null) {
            logger.info("Manga #{} is missing it's source!", manga.getId());
            return;
        }
        try {
            updateMangaInfo(library, manga, source);
        } catch (Exception e) {
            logger.error("Error updating manga!", e);
        }
    }

    public void updateMangaInfo(Library library, Manga manga, Source source) {
        //Update manga info
        manga.copyFrom(source.fetchMangaDetails(manga).toBlocking().first());
        //Update the manga in the library
        library.insertManga(manga);
    }

    public Pair<Integer, Integer> silentUpdateChapters(Library library, Manga manga) {
        Source source = sourceManager.get(manga.getSource());
        if (source == null) {
            logger.info("Manga #{} is missing it's source!", manga.getId());
            return new Pair<>(0, 0);
        }
        try {
            return updateChapters(library, manga, source);
        } catch (Exception e) {
            logger.error("Error updating chapters!", e);
            return new Pair<>(0, 0);
        }
    }

    public Pair<Integer, Integer> updateChapters(Library library, Manga manga, Source source) {
        //Update manga chapters
        List<Chapter> chapters = source.fetchChapterList(manga).toBlocking().first();
        if (chapters == null) {
            throw new NullPointerException();
        }
        //Sync the library chapters with the source chapters
        return ChapterSourceSyncKt.syncChaptersWithSource(library, chapters, manga, source);
    }
}
