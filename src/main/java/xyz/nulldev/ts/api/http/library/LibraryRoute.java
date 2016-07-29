package xyz.nulldev.ts.api.http.library;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.SourceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;
import xyz.nulldev.ts.util.MangaUtils;

import java.io.File;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 13/07/16
 */
public class LibraryRoute extends TachiWebRoute {
    public static final String KEY_TITLE = "title";
    public static final String KEY_ID = "id";
    public static final String KEY_UNREAD = "unread";
    public static final String KEY_DOWNLOADED = "downloaded";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_CONTENT = "content";

    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();
    private SourceManager sourceManager = DIReplacement.get().injectSourceManager();

    public LibraryRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        JSONArray array = new JSONArray();
        for(Manga manga : getLibrary().getFavoriteMangas()) {
            Source source = sourceManager.get(manga.getSource());
            JSONObject mangaJson = new JSONObject();
            mangaJson.put(KEY_ID, manga.getId());
            mangaJson.put(KEY_TITLE, manga.getTitle());
            mangaJson.put(KEY_UNREAD, MangaUtils.getUnreadCount(manga));
            if(source != null) {
                mangaJson.put(KEY_DOWNLOADED, isMangaDownloaded(source, manga));
            }
            JSONArray categoriesJson = new JSONArray();
            for(Category category : getLibrary().getCategoriesForManga(manga)) {
                categoriesJson.put(category.getName());
            }
            mangaJson.put(KEY_CATEGORIES, categoriesJson);
            array.put(mangaJson);
        }
        return success().put(KEY_CONTENT, array);
    }

    private boolean isMangaDownloaded(Source source, Manga manga) {
        File mangaDir = downloadManager.getAbsoluteMangaDirectory(source, manga);

        if (mangaDir.exists()) {
            for (File file : L.def(mangaDir.listFiles(), new File[0])) {
                if (file.isDirectory() && L.def(file.listFiles(), new File[0]).length >= 1) {
                    return true;
                }
            }
        }
        return false;
    }
}
