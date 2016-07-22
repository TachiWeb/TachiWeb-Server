package xyz.nulldev.ts.api.http.catalogue;

import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.model.MangasPage;
import eu.kanade.tachiyomi.data.source.online.OnlineSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.LeniantParser;
import xyz.nulldev.ts.util.StringUtils;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 19/07/16
 */
public class CatalogueRoute extends TachiWebRoute {
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ID = "id";

    private static Logger logger = LoggerFactory.getLogger(CatalogueRoute.class);

    public CatalogueRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        try {
            Integer sourceId = LeniantParser.parseInteger(request.params(":sourceId"));
            Integer page = LeniantParser.parseInteger(request.params(":page"));
            String lastUrl = request.queryParams("lurl");
            String query = request.queryParams("query");
            if (sourceId == null) {
                return error("SourceID must be specified!");
            } else if (page == null) {
                return error("Page must be specified!");
            } else if (page > 1 && lastUrl == null) {
                return error("Is not first page but lastURL not specified!");
            }
            OnlineSource onlineSource;
            try {
                onlineSource = (OnlineSource) DIReplacement.get().injectSourceManager().get(sourceId);
            } catch (ClassCastException e) {
                return error("The specified source is not an OnlineSource!");
            }
            if (onlineSource == null) {
                return error("The specified source does not exist!");
            }
            MangasPage pageObj = new MangasPage(page);
            if(lastUrl != null) {
                pageObj.setUrl(lastUrl);
            } else if(page != 1) {
                return error("Page is not '1' but no last URL provided!");
            }
            Observable<MangasPage> observable;
            if (StringUtils.notNullOrEmpty(query)) {
                observable = onlineSource.fetchSearchManga(pageObj, query);
            } else {
                observable = onlineSource.fetchPopularManga(pageObj);
            }
            List<Manga> result =
                    observable
                            .flatMap(mangasPage -> Observable.from(mangasPage.getMangas()))
                            .map(this::networkToLocalManga)
                            .toList()
                            .toBlocking()
                            .first();
            JSONObject toReturn = success(true);
            JSONArray content = new JSONArray();
            for (Manga manga : result) {
                JSONObject mangaJson = new JSONObject();
                mangaJson.put(KEY_ID, manga.getId());
                mangaJson.put(KEY_TITLE, manga.getTitle());
                content.put(mangaJson);
            }
            toReturn.put(KEY_CONTENT, content);
            return toReturn;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Manga networkToLocalManga(Manga networkManga) {
        Manga localManga = getLibrary().getManga(networkManga.getUrl(), networkManga.getSource());
        if (localManga == null) {
            networkManga.setId(getLibrary().insertManga(networkManga));
            localManga = networkManga;
        }
        return localManga;
    }
}
