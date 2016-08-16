package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.util.ChapterSourceSyncKt;
import kotlin.Pair;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
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

    private static final String KEY_ADDED = "added";
    private static final String KEY_REMOVED = "removed";

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
                getLibrary().insertManga(manga);
            } catch (Exception e) {
                return error("Error updating manga!");
            }
        } else if(updateType == UpdateType.CHAPTERS) {
            //Update manga chapters
            try {
                List<Chapter> chapters = source.fetchChapterList(manga).toBlocking().first();
                if(chapters == null) {
                    throw new NullPointerException();
                }
                //Sync the library chapters with the source chapters
                Pair<Integer, Integer> results = ChapterSourceSyncKt.syncChaptersWithSource(getLibrary(), chapters, manga, source);
                //Return the results in JSON
                JSONObject toReturn = success();
                toReturn.put(KEY_ADDED, results.getFirst());
                toReturn.put(KEY_REMOVED, results.getSecond());
                return toReturn.toString();
            } catch (Exception e) {
                return error("Error updating chapters!");
            }
        } else {
            return error("Null/unimplemented update type!");
        }
        return success();
    }

    /**
     * The type of update to perform
     */
    public enum UpdateType {
        INFO,
        CHAPTERS
    }
}
