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

import android.content.Context;
import eu.kanade.tachiyomi.data.backup.BackupManager;
import spark.Spark;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.api.http.auth.CheckSessionRoute;
import xyz.nulldev.ts.api.http.auth.ClearSessionsRoute;
import xyz.nulldev.ts.api.http.sync.SyncRoute;
import xyz.nulldev.ts.api.http.task.TaskStatusRoute;
import xyz.nulldev.ts.api.task.TaskManager;
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
        //Sync route
        Spark.post(API_ROOT + "/sync", syncRoute);
        Spark.post(API_ROOT + "/sync/", syncRoute);
        //Task status route
        Spark.get(API_ROOT + "/task/:taskId", taskStatusRoute);
        Spark.get(API_ROOT + "/task/:taskId/", taskStatusRoute);
        //Check session
        Spark.get(API_ROOT + "/auth", checkSessionRoute);
        Spark.get(API_ROOT + "/auth/", checkSessionRoute);
        //Clear sessions
        Spark.get(API_ROOT + "/clear_sessions", clearSessionsRoute);
        Spark.get(API_ROOT + "/clear_sessions/", clearSessionsRoute);
    }
}
