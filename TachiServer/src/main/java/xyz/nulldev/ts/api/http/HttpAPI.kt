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

import mu.KotlinLogging
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
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
import xyz.nulldev.ts.api.http.library.*
import xyz.nulldev.ts.api.http.manga.*
import xyz.nulldev.ts.api.http.settings.ListLoginSourceRoute
import xyz.nulldev.ts.api.http.settings.PreferencesRoute
import xyz.nulldev.ts.api.http.settings.SetPreferenceRoute
import xyz.nulldev.ts.api.http.settings.SourceLoginRoute
import xyz.nulldev.ts.api.http.sync.SyncRoute
import xyz.nulldev.ts.api.http.task.TaskStatusRoute
import xyz.nulldev.ts.api.v2.http.HttpApplication
import xyz.nulldev.ts.api.v2.http.categories.CategoriesController
import xyz.nulldev.ts.api.v2.http.chapters.ChaptersController
import xyz.nulldev.ts.api.v2.http.extensions.ExtensionsController
import xyz.nulldev.ts.api.v2.http.jvcompat.JavalinShim
import xyz.nulldev.ts.api.v2.http.library.LibraryController
import xyz.nulldev.ts.api.v2.http.mangas.MangasController
import xyz.nulldev.ts.api.v3.WebAPI
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

    private val logger = KotlinLogging.logger { }

    suspend fun start() {
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

        // V2 APIs (TODO Move to Javalin once all routes are rewritten)
        //TODO Change path params to use constants
        HttpApplication() // Initialize Javalin API for now but do not start it
        getAPIRoute("/v2/library/flags", JavalinShim(LibraryController::getLibraryFlags))
        postAPIRoute("/v2/library/flags", JavalinShim(LibraryController::setLibraryFlags))

        getAPIRoute("/v2/chapters/reading_status", JavalinShim(ChaptersController::getReadingStatus))
        postAPIRoute("/v2/chapters/reading_status", JavalinShim(ChaptersController::setReadingStatus))
        getAPIRoute("/v2/chapters/:chapters/reading_status", JavalinShim(ChaptersController::getReadingStatus))
        postAPIRoute("/v2/chapters/:chapters/reading_status", JavalinShim(ChaptersController::setReadingStatus))

        getAPIRoute("/v2/mangas/viewer", JavalinShim(MangasController::getViewer))
        postAPIRoute("/v2/mangas/viewer", JavalinShim(MangasController::setViewer))
        getAPIRoute("/v2/mangas/:mangas/viewer", JavalinShim(MangasController::getViewer))
        postAPIRoute("/v2/mangas/:mangas/viewer", JavalinShim(MangasController::setViewer))

        getAPIRoute("/v2/categories", JavalinShim(CategoriesController::getCategory))
        postAPIRoute("/v2/categories", JavalinShim(CategoriesController::addCategory))
        deleteAPIRoute("/v2/categories", JavalinShim(CategoriesController::deleteCategory))
        getAPIRoute("/v2/categories/name", JavalinShim(CategoriesController::getName))
        postAPIRoute("/v2/categories/name", JavalinShim(CategoriesController::setName))
        getAPIRoute("/v2/categories/order", JavalinShim(CategoriesController::getOrder))
        postAPIRoute("/v2/categories/order", JavalinShim(CategoriesController::setOrder))
        getAPIRoute("/v2/categories/flags", JavalinShim(CategoriesController::getFlags))
        postAPIRoute("/v2/categories/flags", JavalinShim(CategoriesController::setFlags))
        getAPIRoute("/v2/categories/manga", JavalinShim(CategoriesController::getManga))
        postAPIRoute("/v2/categories/manga", JavalinShim(CategoriesController::setManga))
        getAPIRoute("/v2/categories/:categories", JavalinShim(CategoriesController::getCategory))
        deleteAPIRoute("/v2/categories/:categories", JavalinShim(CategoriesController::deleteCategory))
        getAPIRoute("/v2/categories/:categories/name", JavalinShim(CategoriesController::getName))
        postAPIRoute("/v2/categories/:categories/name", JavalinShim(CategoriesController::setName))
        getAPIRoute("/v2/categories/:categories/order", JavalinShim(CategoriesController::getOrder))
        postAPIRoute("/v2/categories/:categories/order", JavalinShim(CategoriesController::setOrder))
        getAPIRoute("/v2/categories/:categories/flags", JavalinShim(CategoriesController::getFlags))
        postAPIRoute("/v2/categories/:categories/flags", JavalinShim(CategoriesController::setFlags))
        getAPIRoute("/v2/categories/:categories/manga", JavalinShim(CategoriesController::getManga))
        postAPIRoute("/v2/categories/:categories/manga", JavalinShim(CategoriesController::setManga))

        postAPIRoute("/v2/extensions", JavalinShim(ExtensionsController::installExternal))
        getAPIRoute("/v2/extensions", JavalinShim(ExtensionsController::getExtension))
        deleteAPIRoute("/v2/extensions", JavalinShim(ExtensionsController::delete))
        getAPIRoute("/v2/extensions/name", JavalinShim(ExtensionsController::getName))
        getAPIRoute("/v2/extensions/status", JavalinShim(ExtensionsController::getStatus))
        getAPIRoute("/v2/extensions/version_name", JavalinShim(ExtensionsController::getVersionName))
        getAPIRoute("/v2/extensions/version_code", JavalinShim(ExtensionsController::getVersionCode))
        getAPIRoute("/v2/extensions/signature_hash", JavalinShim(ExtensionsController::getSignatureHash))
        getAPIRoute("/v2/extensions/lang", JavalinShim(ExtensionsController::getLang))
        getAPIRoute("/v2/extensions/sources", JavalinShim(ExtensionsController::getSources))
        getAPIRoute("/v2/extensions/has_update", JavalinShim(ExtensionsController::getHasUpdate))
        getAPIRoute("/v2/extensions/icon", JavalinShim(ExtensionsController::getIcon))
        postAPIRoute("/v2/extensions/install", JavalinShim(ExtensionsController::install))
        postAPIRoute("/v2/extensions/reload-local", JavalinShim(ExtensionsController::reloadLocal))
        postAPIRoute("/v2/extensions/reload-available", JavalinShim(ExtensionsController::reloadAvailable))
        postAPIRoute("/v2/extensions/trust", JavalinShim(ExtensionsController::trust))
        getAPIRoute("/v2/extensions/:extensions", JavalinShim(ExtensionsController::getExtension))
        deleteAPIRoute("/v2/extensions/:extensions", JavalinShim(ExtensionsController::delete))
        getAPIRoute("/v2/extensions/:extensions/name", JavalinShim(ExtensionsController::getName))
        getAPIRoute("/v2/extensions/:extensions/status", JavalinShim(ExtensionsController::getStatus))
        getAPIRoute("/v2/extensions/:extensions/version_name", JavalinShim(ExtensionsController::getVersionName))
        getAPIRoute("/v2/extensions/:extensions/version_code", JavalinShim(ExtensionsController::getVersionCode))
        getAPIRoute("/v2/extensions/:extensions/signature_hash", JavalinShim(ExtensionsController::getSignatureHash))
        getAPIRoute("/v2/extensions/:extensions/lang", JavalinShim(ExtensionsController::getLang))
        getAPIRoute("/v2/extensions/:extensions/sources", JavalinShim(ExtensionsController::getSources))
        getAPIRoute("/v2/extensions/:extensions/has_update", JavalinShim(ExtensionsController::getHasUpdate))
        getAPIRoute("/v2/extensions/:extensions/icon", JavalinShim(ExtensionsController::getIcon))
        postAPIRoute("/v2/extensions/:extensions/install", JavalinShim(ExtensionsController::install))

        //Sync route
        val syncRoute = SyncRoute()
        getAPIRoute("/sync", syncRoute)
        postAPIRoute("/sync", syncRoute)

        // Start v3 web API and proxy to it
        val v3Api = WebAPI().start()
        val proxyHttpClient = OkHttpClient.Builder()
                .cache(null)
                .followRedirects(true) // Our web client can't handle proxied redirects
                .followSslRedirects(false) // v3 shouldn't be doing this
                .build()
        val v3Route = Route { incoming, outgoing ->
            val targetUrl = "http://localhost:${v3Api.port}${incoming.contextPath() ?: ""}${incoming.servletPath()
                    ?: ""}${incoming.pathInfo() ?: ""}?${incoming.queryString() ?: ""}"

            logger.info {
                val originalUrl = "${incoming.requestMethod()} ${incoming.contextPath() ?: ""}${incoming.servletPath()
                        ?: ""}${incoming.pathInfo() ?: ""}?${incoming.queryString() ?: ""}"
                "Proxying v3 API request: $originalUrl --> $targetUrl"
            }

            val request = Request.Builder()
                    .url(targetUrl)
                    .apply {
                        if (incoming.requestMethod() in listOf("POST", "PUT", "DELETE", "PATCH"))
                            method(incoming.requestMethod(), RequestBody.create(null, incoming.bodyAsBytes()))
                    }
                    .headers(Headers.of(incoming.headers().associateWith { incoming.headers(it) }))
                    .build()

            val result = proxyHttpClient.newCall(request).execute()

            outgoing.status(result.code())
            result.headers().toMultimap().forEach { name, values ->
                values.forEach { value ->
                    outgoing.header(name, value)
                }
            }
            result.body().byteStream().use { resultBody ->
                resultBody.copyTo(outgoing.raw().outputStream)
            }

            ""
        }

        fun proxyPath(path: String) {
            Spark.get(buildAPIPath(path), v3Route)
            Spark.post(buildAPIPath(path), v3Route)
            Spark.put(buildAPIPath(path), v3Route)
            Spark.patch(buildAPIPath(path), v3Route)
            Spark.delete(buildAPIPath(path), v3Route)
            Spark.head(buildAPIPath(path), v3Route)
            Spark.trace(buildAPIPath(path), v3Route)
            Spark.connect(buildAPIPath(path), v3Route)
            Spark.options(buildAPIPath(path), v3Route)
        }
        proxyPath("/v3/*")
        proxyPath("/v3")
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

    private fun deleteAPIRoute(path: String, route: Route) {
        if(!checkApi(path)) return
        val builtPath = buildAPIPath(path)
        Spark.delete(builtPath, route)
        Spark.delete(builtPath + "/", route)
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