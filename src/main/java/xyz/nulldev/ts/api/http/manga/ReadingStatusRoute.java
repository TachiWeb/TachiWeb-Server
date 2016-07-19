package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.LeniantParser;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/07/16
 */
public class ReadingStatusRoute extends TachiWebRoute {

    public ReadingStatusRoute(Library library) {
        super(library);
    }

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
        Chapter chapter = getLibrary().getChapter(chapterId);
        if (chapter == null) {
            return error("The specified chapter does not exist!");
        }
        String lastPage = request.queryParams("lp");
        String read = request.queryParams("read");
        //Huge mess down here required to keep changes atomic
        int page = -1;
        boolean readB = false;
        if(lastPage != null) {
            try {
                page = Integer.parseInt(lastPage);
            } catch (NumberFormatException e) {
                return error("Last page is not a number!");
            }
        }
        if(read != null) {
            if(read.equalsIgnoreCase("true")) {
                readB = true;
            } else if(read.equalsIgnoreCase("false")) {
                readB = false;
            } else {
                return error("Read is not a boolean!");
            }
        }
        if(lastPage != null) {
            chapter.setLast_page_read(page);
        }
        if(read != null) {
            chapter.setRead(readB);
        }
        return success();
    }
}
