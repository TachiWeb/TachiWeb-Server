package xyz.nulldev.ts.api.http.settings;

import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.online.LoginSource;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.LeniantParser;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 23/07/16
 */
public class SourceLoginRoute extends TachiWebRoute {
    public SourceLoginRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        Integer sourceID = LeniantParser.parseInteger(request.params(":sourceId"));
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        if(sourceID == null) {
            return error("No source ID/invalid source ID specified!");
        } else if(username == null) {
            return error("Username not specified!");
        } else if(password == null) {
            return error("Password not specified!");
        }
        LoginSource source;
        try {
            Source tmpSource = DIReplacement.get().injectSourceManager().get(sourceID);
            if (tmpSource == null || !LoginSource.class.isAssignableFrom(tmpSource.getClass())) {
                throw new IllegalArgumentException();
            }
            source = (LoginSource) tmpSource;
        } catch (Exception e) {
            return error("The specified source is not loaded/invalid!");
        }
        Boolean successObj = source.login(username, password).toBlocking().first();
        boolean success;
        if(successObj == null) {
            success = false;
        } else {
            success = successObj;
        }
        //Store login credentials on success
        if(success) {
            DIReplacement.get().injectPreferencesHelper().setSourceCredentials(source, username, password);
        }
        return success ? success() : error("Username/password incorrect!");
    }
}
