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
import xyz.nulldev.ts.api.http.catalogue.GetFiltersRoute
import xyz.nulldev.ts.api.http.catalogue.ListSourcesRoute
import xyz.nulldev.ts.api.http.download.DownloadChapterRoute
import xyz.nulldev.ts.api.http.download.DownloadsOperationRoute
import xyz.nulldev.ts.api.http.download.GetDownloadStatusRoute
import xyz.nulldev.ts.api.http.image.CoverRoute
import xyz.nulldev.ts.api.http.image.ImageRoute
import xyz.nulldev.ts.api.http.jvcompat.JavalinShim
import xyz.nulldev.ts.api.http.library.*
import xyz.nulldev.ts.api.http.manga.*
import xyz.nulldev.ts.api.http.settings.ListLoginSourceRoute
import xyz.nulldev.ts.api.http.settings.PreferencesRoute
import xyz.nulldev.ts.api.http.settings.SetPreferenceRoute
import xyz.nulldev.ts.api.http.settings.SourceLoginRoute
import xyz.nulldev.ts.api.http.sync.SyncRoute
import xyz.nulldev.ts.api.http.task.TaskStatusRoute
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import xyz.nulldev.ts.ext.kInstance

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class HttpAPI {
    private val serverConfig by lazy { kInstance<ConfigManager>().module<ServerConfig>() }

    fun start() {
        //Get an image from a chapter
        val imageRoute = ImageRoute()
        getAPIRoute("/img/:mangaId/:chapterId/:page", imageRoute)
        //Get the cover of a manga
        val coverRoute = CoverRoute()
        getAPIRoute("/cover/:mangaId", coverRoute)
        //Get the library
        val libraryRoute = LibraryRoute()
        getAPIRoute("/library", libraryRoute)
        //Get details about a manga
        val mangaRoute = MangaRoute()
        getAPIRoute("/manga_info/:mangaId", mangaRoute)
        //Get the chapters of a manga
        val chapterRoute = ChaptersRoute()
        getAPIRoute("/chapters/:mangaId", chapterRoute)
        //Get the page count of a chapter
        val pageCountRoute = PageCountRoute()
        getAPIRoute("/page_count/:mangaId/:chapterId", pageCountRoute)
        //Backup the library
        val createBackupRoute = CreateBackupRoute()
        getAPIRoute("/backup", createBackupRoute)
        //Restore the library
        val restoreFromFileRoute = RestoreFromFileRoute()
        postAPIRoute("/restore_file", restoreFromFileRoute)
        //Favorite/unfavorite a manga
        val faveRoute = FaveRoute()
        getAPIRoute("/fave/:mangaId", faveRoute)
        //Set the reading status of a chapter
        val readingStatusRoute = ReadingStatusRoute()
        getAPIRoute("/reading_status/:mangaId/:chapterId", readingStatusRoute)
        //Update a manga/chapter
        val updateRoute = UpdateRoute()
        getAPIRoute("/update/:mangaId/:updateType", updateRoute)
        //Source list
        val listSourcesRoute = ListSourcesRoute()
        getAPIRoute("/sources", listSourcesRoute)
        //Catalogue
        val catalogueRoute = CatalogueRoute()
        postAPIRoute("/catalogue", catalogueRoute)
        //Login source list
        val listLoginSourceRoute = ListLoginSourceRoute()
        getAPIRoute("/list_login_sources", listLoginSourceRoute)
        //Login route
        val sourceLoginRoute = SourceLoginRoute()
        getAPIRoute("/source_login/:sourceId", sourceLoginRoute)
        //Download
        val downloadChapterRoute = DownloadChapterRoute()
        getAPIRoute("/download/:mangaId/:chapterId", downloadChapterRoute)
        //Downloads operation
        val downloadsOperationRoute = DownloadsOperationRoute()
        getAPIRoute("/downloads_op/:operation", downloadsOperationRoute)
        //Get downloads
        val getDownloadStatusRoute = GetDownloadStatusRoute()
        getAPIRoute("/get_downloads", getDownloadStatusRoute)
        //Set flags
        val setFlagRoute = SetFlagRoute()
        getAPIRoute("/set_flag/:mangaId/:flag/:state", setFlagRoute)
        //Preferences route
        val preferencesRoute = PreferencesRoute()
        getAPIRoute("/prefs", preferencesRoute)
        //Set preferences route
        val setPreferenceRoute = SetPreferenceRoute()
        getAPIRoute("/set_pref/:key/:type", setPreferenceRoute)
        //Task status route
        val taskStatusRoute = TaskStatusRoute()
        getAPIRoute("/task/:taskId", taskStatusRoute)
        //Check session
        val checkSessionRoute = CheckSessionRoute()
        getAPIRoute("/auth", checkSessionRoute)
        //Clear sessions
        val clearSessionsRoute = ClearSessionsRoute()
        getAPIRoute("/clear_sessions", clearSessionsRoute)
        //Test auth route
        val testAuthRoute = TestAuthenticatedRoute()
        getAPIRoute("/test_auth", testAuthRoute)
        //Get categories
        val getCategoriesRoute = GetCategoriesRoute()
        getAPIRoute("/get_categories", getCategoriesRoute)
        //Edit categories
        val editCategoriesRoute = EditCategoriesRoute()
        getAPIRoute("/edit_categories/:operation", editCategoriesRoute)
        //Get filters
        val getFiltersRoute = GetFiltersRoute()
        getAPIRoute("/get_filters/:sourceId", getFiltersRoute)

        // Javalin compatible routes
        getAPIRoute("/library/flags", JavalinShim(LibraryController::getLibraryFlags))
        postAPIRoute("/library/flags", JavalinShim(LibraryController::setLibraryFlags))

        //Sync route
        val syncRoute = SyncRoute()
        getAPIRoute("/sync", syncRoute)
        postAPIRoute("/sync", syncRoute)
    }

    private fun buildAPIPath(path: String): String {
        return API_ROOT + path
    }

    private fun getAPIRoute(path: String, route: Route) {
        if(!checkApi(path)) return
        val builtPath = buildAPIPath(path)
        Spark.get(builtPath, route)
        Spark.get(builtPath + "/", route)
    }

    private fun postAPIRoute(path: String, route: Route) {
        if(!checkApi(path)) return
        val builtPath = buildAPIPath(path)
        Spark.post(builtPath, route)
        Spark.post(builtPath + "/", route)
    }

    fun checkApi(path: String): Boolean {
        val endpoint = path.split("/").filterNot(String::isBlank).first().trim().toLowerCase()

        if(endpoint in serverConfig.disabledApiEndpoints) return false

        if(serverConfig.enabledApiEndpoints.isNotEmpty()) {
            if(endpoint !in serverConfig.enabledApiEndpoints) return false
        }

        return true
    }

    companion object {
        val API_ROOT = "/api"
    }
}