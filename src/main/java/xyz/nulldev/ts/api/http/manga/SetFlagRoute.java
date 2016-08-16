package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;
import xyz.nulldev.ts.util.LeniantParser;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 29/07/16
 */
public class SetFlagRoute extends TachiWebRoute {
    public SetFlagRoute(Library library) {
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
        MangaFlag flag;
        try {
            flag = MangaFlag.valueOf(L.def(request.params(":flag"), "").toUpperCase());
        } catch (IllegalArgumentException e) {
            return error("Invalid/no flag specified!");
        }
        MangaFlag.FlagState flagState = flag.findFlagState(L.def(request.params(":state"), "").toUpperCase());
        if(flagState == null) {
            return error("Invalid/no flag state specified!");
        }
        flag.set(manga, flagState);
        return success();
    }
}
