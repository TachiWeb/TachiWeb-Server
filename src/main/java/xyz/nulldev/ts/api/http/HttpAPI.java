package xyz.nulldev.ts.api.http;

import spark.Spark;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.api.http.debug.DebugLibrary;
import xyz.nulldev.ts.api.http.image.CoverRoute;
import xyz.nulldev.ts.api.http.image.ImageRoute;
import xyz.nulldev.ts.api.http.library.LibraryRoute;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 12/07/16
 */
public class HttpAPI {
    public static final String API_ROOT = "/api";

    private ImageRoute imageRoute = new ImageRoute(DIReplacement.get().getLibrary());
    private CoverRoute coverRoute = new CoverRoute(DIReplacement.get().getLibrary());
    private LibraryRoute libraryRoute = new LibraryRoute(DIReplacement.get().getLibrary());

    public void start() {
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
        Spark.staticFiles.location("/static");
        Spark.get(API_ROOT + "/img/:mangaId/:chapterId/:page/:lastReqId", imageRoute);
        Spark.get(API_ROOT + "/img/:mangaId/:chapterId/:page/:lastReqId/", imageRoute);
        Spark.get(API_ROOT + "/cover/:mangaId", coverRoute);
        Spark.get(API_ROOT + "/cover/:mangaId/", coverRoute);
        Spark.get(API_ROOT + "/library", libraryRoute);
        Spark.get(API_ROOT + "/library/", libraryRoute);
        Spark.get("/", new DebugLibrary(DIReplacement.get().getLibrary()));
    }
}
