package xyz.nulldev.ts.api.http.library;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Manga;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.MangaUtils;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 13/07/16
 */
public class LibraryRoute extends TachiWebRoute {
    public static final String KEY_TITLE = "title";
    public static final String KEY_ID = "id";
    public static final String KEY_UNREAD = "unread";
    public static final String KEY_CATEGORIES = "categories";

    public LibraryRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        JSONArray array = new JSONArray();
        for(Manga manga : getLibrary().getFavoriteMangas()) {
            JSONObject mangaJson = new JSONObject();
            mangaJson.put(KEY_ID, manga.getId());
            mangaJson.put(KEY_TITLE, manga.getTitle());
            mangaJson.put(KEY_UNREAD, MangaUtils.getUnreadCount(manga));
            JSONArray categoriesJson = new JSONArray();
            for(Category category : getLibrary().getCategoriesForManga(manga)) {
                categoriesJson.put(category.getName());
            }
            mangaJson.put(KEY_CATEGORIES, categoriesJson);
            array.put(mangaJson);
        }
        return array.toString();
    }
}
