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

package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.ChapterUtils;
import xyz.nulldev.ts.util.LeniantParser;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class PageCountRoute extends TachiWebRoute {

    private static final String KEY_PAGE_COUNT = "page_count";

    private static Logger logger = LoggerFactory.getLogger(PageCountRoute.class);

    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        Long chapterId = LeniantParser.parseLong(request.params(":chapterId"));
        if (mangaId == null) {
            return error("MangaID must be specified!");
        } else if (chapterId == null) {
            return error("ChapterID must be specified!");
        }
        Manga manga = getLibrary().getManga(mangaId);
        if (manga == null) {
            return error("The specified manga does not exist!");
        }
        Source source;
        try {
            source = DIReplacement.get().injectSourceManager().get(manga.getSource());
            if (source == null) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            return error("This manga's source is not loaded!");
        }
        Chapter chapter = getLibrary().getChapter(chapterId);
        if (chapter == null) {
            return error("The specified chapter does not exist!");
        }
        List<Page> pages = ChapterUtils.getPageList(downloadManager, source, manga, chapter);
        if (pages == null) {
            return error("Failed to fetch page list!");
        }
        return success().put(KEY_PAGE_COUNT, pages.size());
    }
}
