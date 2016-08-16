package xyz.nulldev.ts.api.http.library;

import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/07/16
 */
public class CreateBackupRoute extends TachiWebRoute {
    public CreateBackupRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        if("true".equalsIgnoreCase(request.queryParams("force-download"))) {
            response.header("Content-Type", "application/octet-stream");
            response.header("Content-Disposition", "attachment; filename=\"backup.json\"");
        }
        return DIReplacement.get().injectBackupManager().backupToString(true);
    }
}
