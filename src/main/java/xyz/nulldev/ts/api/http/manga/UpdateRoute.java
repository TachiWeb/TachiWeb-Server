package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.util.ChapterSourceSyncKt;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;
import xyz.nulldev.ts.util.LeniantParser;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 18/07/16
 */
public class UpdateRoute extends TachiWebRoute {
    public UpdateRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        UpdateType updateType;
        try {
            updateType = UpdateType.valueOf(L.def(request.params(":updateType"), "").toUpperCase());
        } catch(IllegalArgumentException e) {
            return error("Invalid/no update type specified!");
        }
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        if (mangaId == null) {
            return error("MangaID must be specified!");
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
        if(updateType == UpdateType.INFO) {
            try {
                manga = source.fetchMangaDetails(manga).toBlocking().first();
                if (manga == null) {
                    throw new NullPointerException();
                }
                getLibrary().insertManga(manga);
            } catch (Exception e) {
                return error("Error updating manga!");
            }
        } else if(updateType == UpdateType.CHAPTERS) {
            try {
                List<Chapter> chapters = source.fetchChapterList(manga).toBlocking().first();
                if(chapters == null) {
                    throw new NullPointerException();
                }
                ChapterSourceSyncKt.syncChaptersWithSource(getLibrary(), chapters, manga, source);
            } catch (Exception e) {
                return error("Error updating chapters!");
            }
        } else {
            return error("Null/unimplemented update type!");
        }
        return success();
    }

    private enum UpdateType {
        INFO,
        CHAPTERS
    }
}
