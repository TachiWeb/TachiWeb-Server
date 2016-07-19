package xyz.nulldev.ts.api.http;

import spark.Spark;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.api.http.catalogue.CatalogueRoute;
import xyz.nulldev.ts.api.http.catalogue.ListSourcesRoute;
import xyz.nulldev.ts.api.http.debug.DebugLibrary;
import xyz.nulldev.ts.api.http.image.CoverRoute;
import xyz.nulldev.ts.api.http.image.ImageRoute;
import xyz.nulldev.ts.api.http.library.CreateBackupRoute;
import xyz.nulldev.ts.api.http.library.LibraryRoute;
import xyz.nulldev.ts.api.http.library.RestoreFromFileRoute;
import xyz.nulldev.ts.api.http.manga.*;

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
    private MangaRoute mangaRoute = new MangaRoute(DIReplacement.get().getLibrary());
    private ChaptersRoute chapterRoute = new ChaptersRoute(DIReplacement.get().getLibrary());
    private PageCountRoute pageCountRoute = new PageCountRoute(DIReplacement.get().getLibrary());
    private CreateBackupRoute createBackupRoute = new CreateBackupRoute(DIReplacement.get().getLibrary());
    private RestoreFromFileRoute restoreFromFileRoute = new RestoreFromFileRoute(DIReplacement.get().getLibrary());
    private FaveRoute faveRoute = new FaveRoute(DIReplacement.get().getLibrary());
    private ReadingStatusRoute readingStatusRoute = new ReadingStatusRoute(DIReplacement.get().getLibrary());
    private UpdateRoute updateRoute = new UpdateRoute(DIReplacement.get().getLibrary());
    private ListSourcesRoute listSourcesRoute = new ListSourcesRoute(DIReplacement.get().getLibrary());
    private CatalogueRoute catalogueRoute = new CatalogueRoute(DIReplacement.get().getLibrary());

    public void start() {
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
        Spark.staticFiles.location("/static");
        //Get an image from a chapter
        Spark.get(API_ROOT + "/img/:mangaId/:chapterId/:page", imageRoute);
        Spark.get(API_ROOT + "/img/:mangaId/:chapterId/:page/", imageRoute);
        //Get the cover of a manga
        Spark.get(API_ROOT + "/cover/:mangaId", coverRoute);
        Spark.get(API_ROOT + "/cover/:mangaId/", coverRoute);
        //Get the library
        Spark.get(API_ROOT + "/library", libraryRoute);
        Spark.get(API_ROOT + "/library/", libraryRoute);
        //Get details about a manga
        Spark.get(API_ROOT + "/manga_info/:mangaId", mangaRoute);
        Spark.get(API_ROOT + "/manga_info/:mangaId/", mangaRoute);
        //Get the chapters of a manga
        Spark.get(API_ROOT + "/chapters/:mangaId", chapterRoute);
        Spark.get(API_ROOT + "/chapters/:mangaId/", chapterRoute);
        //Get the page count of a chapter
        Spark.get(API_ROOT + "/page_count/:mangaId/:chapterId", pageCountRoute);
        Spark.get(API_ROOT + "/page_count/:mangaId/:chapterId/", pageCountRoute);
        //Backup the library
        Spark.get(API_ROOT + "/backup", createBackupRoute);
        Spark.get(API_ROOT + "/backup/", createBackupRoute);
        //Restore the library
        Spark.post(API_ROOT + "/restore_file", restoreFromFileRoute);
        Spark.post(API_ROOT + "/restore_file/", restoreFromFileRoute);
        //Favorite/unfavorite a manga
        Spark.get(API_ROOT + "/fave/:mangaId", faveRoute);
        Spark.get(API_ROOT + "/fave/:mangaId/", faveRoute);
        //Set the reading status of a chapter
        Spark.get(API_ROOT + "/reading_status/:mangaId/:chapterId", readingStatusRoute);
        Spark.get(API_ROOT + "/reading_status/:mangaId/:chapterId/", readingStatusRoute);
        //Update a manga/chapter
        Spark.get(API_ROOT + "/update/:mangaId/:updateType", updateRoute);
        Spark.get(API_ROOT + "/update/:mangaId/:updateType/", updateRoute);
        //Source list
        Spark.get(API_ROOT + "/sources", listSourcesRoute);
        Spark.get(API_ROOT + "/sources/", listSourcesRoute);
        //Catalogue
        Spark.get(API_ROOT + "/catalogue/:sourceId/:page", catalogueRoute);
        Spark.get(API_ROOT + "/catalogue/:sourceId/:page/", catalogueRoute);
        Spark.get("/", new DebugLibrary(DIReplacement.get().getLibrary()));
    }
}
