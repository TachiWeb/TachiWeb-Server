package xyz.nulldev.ts.api.http.library;

import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/07/16
 */
public class RestoreFromFileRoute extends TachiWebRoute {
    public RestoreFromFileRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
        try (InputStream is = request.raw().getPart("uploaded_file").getInputStream()) {
            DIReplacement.get().injectBackupManager().restoreFromStream(is, getLibrary());
        } catch (Exception e) {
            e.printStackTrace();
            return error("Restore failed!");
        }
        return success();
    }
}
