/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.api.http;

import spark.Route;
import spark.Spark;
import xyz.nulldev.ts.api.http.auth.CheckSessionRoute;
import xyz.nulldev.ts.api.http.auth.ClearSessionsRoute;
import xyz.nulldev.ts.api.http.sync.SyncRoute;
import xyz.nulldev.ts.api.http.task.TaskStatusRoute;
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

    private ImageRoute imageRoute = new ImageRoute();
    private CoverRoute coverRoute = new CoverRoute();
    private LibraryRoute libraryRoute = new LibraryRoute();
    private MangaRoute mangaRoute = new MangaRoute();
    private ChaptersRoute chapterRoute = new ChaptersRoute();
    private PageCountRoute pageCountRoute = new PageCountRoute();
    private CreateBackupRoute createBackupRoute = new CreateBackupRoute();
    private RestoreFromFileRoute restoreFromFileRoute = new RestoreFromFileRoute();
    private FaveRoute faveRoute = new FaveRoute();
    private ReadingStatusRoute readingStatusRoute = new ReadingStatusRoute();
    private UpdateRoute updateRoute = new UpdateRoute();
    private ListSourcesRoute listSourcesRoute = new ListSourcesRoute();
    private CatalogueRoute catalogueRoute = new CatalogueRoute();
    private ListLoginSourceRoute listLoginSourceRoute = new ListLoginSourceRoute();
    private SourceLoginRoute sourceLoginRoute = new SourceLoginRoute();
    private DownloadChapterRoute downloadChapterRoute = new DownloadChapterRoute();
    private DownloadsOperationRoute downloadsOperationRoute = new DownloadsOperationRoute();
    private GetDownloadStatusRoute getDownloadStatusRoute = new GetDownloadStatusRoute();
    private SetFlagRoute setFlagRoute = new SetFlagRoute();
    private PreferencesRoute preferencesRoute = new PreferencesRoute();
    private SetPreferenceRoute setPreferenceRoute = new SetPreferenceRoute();
    private SyncRoute syncRoute = new SyncRoute();
    private TaskStatusRoute taskStatusRoute = new TaskStatusRoute();
    private CheckSessionRoute checkSessionRoute = new CheckSessionRoute();
    private ClearSessionsRoute clearSessionsRoute = new ClearSessionsRoute();

    public void start() {
        //Get an image from a chapter
        getAPIRoute("/img/:mangaId/:chapterId/:page", imageRoute);
        //Get the cover of a manga
        getAPIRoute("/cover/:mangaId", coverRoute);
        //Get the library
        getAPIRoute("/library", libraryRoute);
        //Get details about a manga
        getAPIRoute("/manga_info/:mangaId", mangaRoute);
        //Get the chapters of a manga
        getAPIRoute("/chapters/:mangaId", chapterRoute);
        //Get the page count of a chapter
        getAPIRoute("/page_count/:mangaId/:chapterId", pageCountRoute);
        //Backup the library
        getAPIRoute("/backup", createBackupRoute);
        //Restore the library
        postAPIRoute("/restore_file", restoreFromFileRoute);
        //Favorite/unfavorite a manga
        getAPIRoute("/fave/:mangaId", faveRoute);
        //Set the reading status of a chapter
        getAPIRoute("/reading_status/:mangaId/:chapterId", readingStatusRoute);
        //Update a manga/chapter
        getAPIRoute("/update/:mangaId/:updateType", updateRoute);
        //Source list
        getAPIRoute("/sources", listSourcesRoute);
        //Catalogue
        getAPIRoute("/catalogue/:sourceId/:page", catalogueRoute);
        //Login source list
        getAPIRoute("/list_login_sources", listLoginSourceRoute);
        //Login route
        getAPIRoute("/source_login/:sourceId", sourceLoginRoute);
        //Download
        getAPIRoute("/download/:mangaId/:chapterId", downloadChapterRoute);
        //Downloads operation
        getAPIRoute("/downloads_op/:operation", downloadsOperationRoute);
        //Get downloads
        getAPIRoute("/get_downloads", getDownloadStatusRoute);
        //Set flags
        getAPIRoute("/set_flag/:mangaId/:flag/:state", setFlagRoute);
        //Preferences route
        getAPIRoute("/prefs", preferencesRoute);
        //Set preferences route
        getAPIRoute("/set_pref/:key/:type", setPreferenceRoute);
        //Sync route
        postAPIRoute("/sync", syncRoute);
        //Task status route
        getAPIRoute("/task/:taskId", taskStatusRoute);
        //Check session
        getAPIRoute("/auth", checkSessionRoute);
        //Clear sessions
        getAPIRoute("/clear_sessions", clearSessionsRoute);
    }

    private String buildAPIPath(String path) {
        return API_ROOT + path;
    }

    private void getAPIRoute(String path, Route route) {
        String builtPath = buildAPIPath(path);
        Spark.get(builtPath, route);
        Spark.get(builtPath + "/", route);
    }

    private void postAPIRoute(String path, Route route) {
        String builtPath = buildAPIPath(path);
        Spark.post(builtPath, route);
        Spark.post(builtPath + "/", route);
    }
}
