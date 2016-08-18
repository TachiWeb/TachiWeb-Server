package xyz.nulldev.ts.api.http;

import android.content.Context;
import spark.Spark;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.catalogue.CatalogueRoute;
import xyz.nulldev.ts.api.http.catalogue.ListSourcesRoute;
import xyz.nulldev.ts.api.http.download.DownloadChapterRoute;
import xyz.nulldev.ts.api.http.download.DownloadsOperationRoute;
import xyz.nulldev.ts.api.http.download.GetDownloadStatusRoute;
import xyz.nulldev.ts.api.http.image.CoverRoute;
import xyz.nulldev.ts.api.http.image.ImageRoute;
import xyz.nulldev.ts.api.http.library.CreateBackupRoute;
import xyz.nulldev.ts.api.http.library.LibraryRoute;
import xyz.nulldev.ts.api.http.library.RestoreFromFileRoute;
import xyz.nulldev.ts.api.http.manga.*;
import xyz.nulldev.ts.api.http.settings.*;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 12/07/16
 */
public class HttpAPI {
    public static final String API_ROOT = "/api";

    private Context context = DIReplacement.get().getContext();
    private Library library = DIReplacement.get().getLibrary();
    private ImageRoute imageRoute = new ImageRoute(library);
    private CoverRoute coverRoute = new CoverRoute(library);
    private LibraryRoute libraryRoute = new LibraryRoute(library);
    private MangaRoute mangaRoute = new MangaRoute(library);
    private ChaptersRoute chapterRoute = new ChaptersRoute(library);
    private PageCountRoute pageCountRoute = new PageCountRoute(library);
    private CreateBackupRoute createBackupRoute = new CreateBackupRoute(library);
    private RestoreFromFileRoute restoreFromFileRoute = new RestoreFromFileRoute(library);
    private FaveRoute faveRoute = new FaveRoute(library);
    private ReadingStatusRoute readingStatusRoute = new ReadingStatusRoute(library);
    private UpdateRoute updateRoute = new UpdateRoute(library);
    private ListSourcesRoute listSourcesRoute = new ListSourcesRoute(library);
    private CatalogueRoute catalogueRoute = new CatalogueRoute(library);
    private ListLoginSourceRoute listLoginSourceRoute = new ListLoginSourceRoute(library);
    private SourceLoginRoute sourceLoginRoute = new SourceLoginRoute(library);
    private DownloadChapterRoute downloadChapterRoute = new DownloadChapterRoute(library);
    private DownloadsOperationRoute downloadsOperationRoute = new DownloadsOperationRoute(library);
    private GetDownloadStatusRoute getDownloadStatusRoute = new GetDownloadStatusRoute(library);
    private SetFlagRoute setFlagRoute = new SetFlagRoute(library);
    private PreferencesRoute preferencesRoute = new PreferencesRoute(context, library);
    private SetPreferenceRoute setPreferenceRoute = new SetPreferenceRoute(library, context);

    public void start() {
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
        //Login source list
        Spark.get(API_ROOT + "/list_login_sources", listLoginSourceRoute);
        Spark.get(API_ROOT + "/list_login_sources/", listLoginSourceRoute);
        //Login route
        Spark.get(API_ROOT + "/source_login/:sourceId", sourceLoginRoute);
        Spark.get(API_ROOT + "/source_login/:sourceId/", sourceLoginRoute);
        //Download
        Spark.get(API_ROOT + "/download/:mangaId/:chapterId", downloadChapterRoute);
        Spark.get(API_ROOT + "/download/:mangaId/:chapterId/", downloadChapterRoute);
        //Downloads operation
        Spark.get(API_ROOT + "/downloads_op/:operation", downloadsOperationRoute);
        Spark.get(API_ROOT + "/downloads_op/:operation/", downloadsOperationRoute);
        //Get downloads
        Spark.get(API_ROOT + "/get_downloads", getDownloadStatusRoute);
        Spark.get(API_ROOT + "/get_downloads/", getDownloadStatusRoute);
        //Set flags
        Spark.get(API_ROOT + "/set_flag/:mangaId/:flag/:state", setFlagRoute);
        Spark.get(API_ROOT + "/set_flag/:mangaId/:flag/:state/", setFlagRoute);
        //Preferences route
        Spark.get(API_ROOT + "/prefs", preferencesRoute);
        Spark.get(API_ROOT + "/prefs/", preferencesRoute);
        //Set preferences route
        Spark.get(API_ROOT + "/set_pref/:key/:type", setPreferenceRoute);
        Spark.get(API_ROOT + "/set_pref/:key/:type/", setPreferenceRoute);
    }
}
