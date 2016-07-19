package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import org.json.JSONObject;
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
public class FaveRoute extends TachiWebRoute {

    public FaveRoute(Library library) {
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
        String fave = request.queryParams("fave");
        if(fave == null) {
            return error("Parameter 'fave' was not specified!");
        }
        if(fave.equalsIgnoreCase("true")) {
            manga.setFavorite(true);
        } else if(fave.equalsIgnoreCase("false")) {
            manga.setFavorite(false);
        } else {
            return error("Parameter 'fave' is not a valid boolean!");
        }
        return success();
    }
}
