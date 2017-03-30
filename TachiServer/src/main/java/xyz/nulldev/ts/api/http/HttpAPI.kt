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

package xyz.nulldev.ts.api.http

import spark.Route
import spark.Spark
import xyz.nulldev.ts.api.http.auth.CheckSessionRoute
import xyz.nulldev.ts.api.http.auth.ClearSessionsRoute
import xyz.nulldev.ts.api.http.auth.TestAuthenticatedRoute
import xyz.nulldev.ts.api.http.catalogue.CatalogueRoute
import xyz.nulldev.ts.api.http.catalogue.ListSourcesRoute
import xyz.nulldev.ts.api.http.download.DownloadChapterRoute
import xyz.nulldev.ts.api.http.download.DownloadsOperationRoute
import xyz.nulldev.ts.api.http.download.GetDownloadStatusRoute
import xyz.nulldev.ts.api.http.image.CoverRoute
import xyz.nulldev.ts.api.http.image.ImageRoute
import xyz.nulldev.ts.api.http.library.CreateBackupRoute
import xyz.nulldev.ts.api.http.library.LibraryRoute
import xyz.nulldev.ts.api.http.library.RestoreFromFileRoute
import xyz.nulldev.ts.api.http.manga.*
import xyz.nulldev.ts.api.http.settings.ListLoginSourceRoute
import xyz.nulldev.ts.api.http.settings.PreferencesRoute
import xyz.nulldev.ts.api.http.settings.SetPreferenceRoute
import xyz.nulldev.ts.api.http.settings.SourceLoginRoute
import xyz.nulldev.ts.api.http.task.TaskStatusRoute

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class HttpAPI {
    private val imageRoute = ImageRoute()
    private val coverRoute = CoverRoute()
    private val libraryRoute = LibraryRoute()
    private val mangaRoute = MangaRoute()
    private val chapterRoute = ChaptersRoute()
    private val pageCountRoute = PageCountRoute()
    private val createBackupRoute = CreateBackupRoute()
    private val restoreFromFileRoute = RestoreFromFileRoute()
    private val faveRoute = FaveRoute()
    private val readingStatusRoute = ReadingStatusRoute()
    private val updateRoute = UpdateRoute()
    private val listSourcesRoute = ListSourcesRoute()
    private val catalogueRoute = CatalogueRoute()
    private val listLoginSourceRoute = ListLoginSourceRoute()
    private val sourceLoginRoute = SourceLoginRoute()
    private val downloadChapterRoute = DownloadChapterRoute()
    private val downloadsOperationRoute = DownloadsOperationRoute()
    private val getDownloadStatusRoute = GetDownloadStatusRoute()
    private val setFlagRoute = SetFlagRoute()
    private val preferencesRoute = PreferencesRoute()
    private val setPreferenceRoute = SetPreferenceRoute()
    private val taskStatusRoute = TaskStatusRoute()
    private val checkSessionRoute = CheckSessionRoute()
    private val clearSessionsRoute = ClearSessionsRoute()
    private val testAuthRoute = TestAuthenticatedRoute()

    fun start() {
        //Get an image from a chapter
        getAPIRoute("/img/:mangaId/:chapterId/:page", imageRoute)
        //Get the cover of a manga
        getAPIRoute("/cover/:mangaId", coverRoute)
        //Get the library
        getAPIRoute("/library", libraryRoute)
        //Get details about a manga
        getAPIRoute("/manga_info/:mangaId", mangaRoute)
        //Get the chapters of a manga
        getAPIRoute("/chapters/:mangaId", chapterRoute)
        //Get the page count of a chapter
        getAPIRoute("/page_count/:mangaId/:chapterId", pageCountRoute)
        //Backup the library
        getAPIRoute("/backup", createBackupRoute)
        //Restore the library
        postAPIRoute("/restore_file", restoreFromFileRoute)
        //Favorite/unfavorite a manga
        getAPIRoute("/fave/:mangaId", faveRoute)
        //Set the reading status of a chapter
        getAPIRoute("/reading_status/:mangaId/:chapterId", readingStatusRoute)
        //Update a manga/chapter
        getAPIRoute("/update/:mangaId/:updateType", updateRoute)
        //Source list
        getAPIRoute("/sources", listSourcesRoute)
        //Catalogue
        getAPIRoute("/catalogue/:sourceId/:page", catalogueRoute)
        //Login source list
        getAPIRoute("/list_login_sources", listLoginSourceRoute)
        //Login route
        getAPIRoute("/source_login/:sourceId", sourceLoginRoute)
        //Download
        getAPIRoute("/download/:mangaId/:chapterId", downloadChapterRoute)
        //Downloads operation
        getAPIRoute("/downloads_op/:operation", downloadsOperationRoute)
        //Get downloads
        getAPIRoute("/get_downloads", getDownloadStatusRoute)
        //Set flags
        getAPIRoute("/set_flag/:mangaId/:flag/:state", setFlagRoute)
        //Preferences route
        getAPIRoute("/prefs", preferencesRoute)
        //Set preferences route
        getAPIRoute("/set_pref/:key/:type", setPreferenceRoute)
        //Sync route (TODO)
//        postAPIRoute("/sync", syncRoute)
        //Task status route
        getAPIRoute("/task/:taskId", taskStatusRoute)
        //Check session
        getAPIRoute("/auth", checkSessionRoute)
        //Clear sessions
        getAPIRoute("/clear_sessions", clearSessionsRoute)
        //Diff sync route (TODO)
//        postAPIRoute("/diff_sync", diffSyncRoute)
        //Test auth route
        getAPIRoute("/test_auth", testAuthRoute)
    }

    private fun buildAPIPath(path: String): String {
        return API_ROOT + path
    }

    private fun getAPIRoute(path: String, route: Route) {
        val builtPath = buildAPIPath(path)
        Spark.get(builtPath, route)
        Spark.get(builtPath + "/", route)
    }

    private fun postAPIRoute(path: String, route: Route) {
        val builtPath = buildAPIPath(path)
        Spark.post(builtPath, route)
        Spark.post(builtPath + "/", route)
    }

    companion object {
        val API_ROOT = "/api"
    }
}