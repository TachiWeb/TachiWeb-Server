package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.model.Page;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.LeniantParser;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class PageCountRoute extends TachiWebRoute {

    public PageCountRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        response.header("Access-Control-Allow-Origin", "*");
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        Long chapterId = LeniantParser.parseLong(request.params(":chapterId"));
        if (mangaId == null) {
            return "MangaID must be specified!";
        } else if (chapterId == null) {
            return "ChapterID must be specified!";
        }
        Manga manga = getLibrary().getManga(mangaId);
        if (manga == null) {
            return "The specified manga does not exist!";
        }
        Source source;
        try {
            source = DIReplacement.get().injectSourceManager().get(manga.getSource());
            if (source == null) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            return "This manga's source is not loaded!";
        }
        Chapter chapter = getLibrary().getChapter(chapterId);
        if (chapter == null) {
            return "The specified chapter does not exist!";
        }
        List<Page> pages = null;
        try {
            pages = source.fetchPageList(chapter).toBlocking().first();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO Logging
        }
        if (pages == null) {
            return "Failed to fetch page list!";
        }
        return String.valueOf(pages.size());
    }
}
