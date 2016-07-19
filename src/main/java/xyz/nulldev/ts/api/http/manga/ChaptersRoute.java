package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.LeniantParser;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class ChaptersRoute extends TachiWebRoute {
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_READ = "read";
    public static final String KEY_LAST_READ = "last_page_read";
    public static final String KEY_CHAPTER_NUMBER = "chapter_number";

    public ChaptersRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        if (mangaId == null) {
            return error("MangaID must be specified!");
        }
        Manga manga = getLibrary().getManga(mangaId);
        if (manga == null) {
            return error("The specified manga does not exist!");
        }
        List<Chapter> chapters = getLibrary().getChapters(manga);
        //TODO Return in JSON
        JSONArray array = new JSONArray();
        for(Chapter chapter : chapters) {
            JSONObject object = new JSONObject();
            object.put(KEY_ID, chapter.getId());
            object.put(KEY_NAME, chapter.getName());
            object.put(KEY_DATE, chapter.getDate_upload());
            object.put(KEY_READ, chapter.getRead());
            object.put(KEY_LAST_READ, chapter.getLast_page_read());
            object.put(KEY_CHAPTER_NUMBER, chapter.getChapter_number());
            array.put(object);
        }
        return array;
    }
}
