package xyz.nulldev.ts.api.http.library;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Manga;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.ts.Library;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 13/07/16
 */
public class LibraryRoute implements Route {
    public static final String KEY_TITLE = "title";
    public static final String KEY_ID = "id";
    public static final String KEY_UNREAD = "unread";
    public static final String KEY_CATEGORIES = "categories";

    private Library library;

    public LibraryRoute(Library library) {
        this.library = library;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.header("Access-Control-Allow-Origin", "*");
        JSONArray array = new JSONArray();
        for(Manga manga : library.getMangas()) {
            JSONObject mangaJson = new JSONObject();
            mangaJson.put(KEY_ID, manga.getId());
            mangaJson.put(KEY_TITLE, manga.getTitle());
            mangaJson.put(KEY_UNREAD, manga.getUnread());
            JSONArray categoriesJson = new JSONArray();
            for(Category category : library.getCategoriesForManga(manga)) {
                categoriesJson.put(category.getName());
            }
            mangaJson.put(KEY_CATEGORIES, categoriesJson);
            array.put(mangaJson);
        }
        return array.toString();
    }
}
