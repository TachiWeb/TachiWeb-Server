package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.online.OnlineSource;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.LeniantParser;

import static xyz.nulldev.ts.util.StringUtils.notNullOrEmpty;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class MangaRoute extends TachiWebRoute {
    public static final String KEY_TITLE = "title";
    public static final String KEY_CHAPTER_COUNT = "chapters";
    public static final String KEY_SOURCE_NAME = "source";
    public static final String KEY_BROWSER_URL = "url";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GENRES = "genres";
    public static final String KEY_STATUS = "status";
    public static final String KEY_FAVORITE = "favorite";

    public MangaRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        if (mangaId == null) {
            return "MangaID must be specified!";
        }
        Manga manga = getLibrary().getManga(mangaId);
        if (manga == null) {
            return "The specified manga does not exist!";
        }
        JSONObject object = new JSONObject();
        object.put(KEY_TITLE, manga.getTitle());
        object.put(KEY_CHAPTER_COUNT, getLibrary().getChapters(manga).size());
        Source source = DIReplacement.get().injectSourceManager().get(manga.getSource());
        String url = "";
        if(source != null) {
            object.put(KEY_SOURCE_NAME, source.getName());
            if(OnlineSource.class.isAssignableFrom(source.getClass())) {
                OnlineSource onlineSource = (OnlineSource) source;
                url = onlineSource.getBaseUrl() + manga.getUrl();
            }
        }
        object.put(KEY_BROWSER_URL, url);
        if(notNullOrEmpty(manga.getArtist())) {
            object.put(KEY_ARTIST, manga.getArtist());
        }
        if(notNullOrEmpty(manga.getAuthor())) {
            object.put(KEY_AUTHOR, manga.getAuthor());
        }
        if(notNullOrEmpty(manga.getDescription())) {
            object.put(KEY_DESCRIPTION, manga.getDescription());
        }
        if(notNullOrEmpty(manga.getGenre())) {
            object.put(KEY_GENRES, manga.getGenre());
        }
        object.put(KEY_STATUS, statusToString(manga.getStatus()));
        object.put(KEY_FAVORITE, manga.getFavorite());
        return object;
    }
    private static String statusToString(int i) {
        switch (i) {
            case Manga.ONGOING:
                return "Ongoing";
            case Manga.COMPLETED:
                return "Completed";
            case Manga.LICENSED:
                return "Licensed";
            case Manga.UNKNOWN:
            default:
                return "Unknown";
        }
    }
}
