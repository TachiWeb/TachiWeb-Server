package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import spark.Route
import spark.Spark
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.syncdeploy.api.endpoints.AuthAPI
import xyz.nulldev.ts.syncdeploy.api.endpoints.ChangePasswordAPI
import xyz.nulldev.ts.syncdeploy.api.endpoints.ClearDataAPI
import xyz.nulldev.ts.syncdeploy.api.endpoints.CloseAccountAPI

class TSSyncDeploy: KodeinGlobalAware {
    private val config = instance<ConfigManager>().module<SyncConfigModule>()
    private val accountManager = AccountManager()

    fun bindSyncRoutes() {
        val mainPagePath = if(config.syncOnlyMode)
            ""
        else "/acc-login"

        val mainPageRelUrl = "/" + mainPagePath.removePrefix("/")

        getRoute(mainPagePath, MainPage(accountManager, mainPageRelUrl))
        getRoute("/account", AccountPage(accountManager, mainPageRelUrl))
        getRoute("/account/data.zip", DownloadDataPage(accountManager))

        postRoute("/sapi/auth", AuthAPI(accountManager))
        getRoute("/sapi/clear-data", ClearDataAPI(accountManager))
        getRoute("/sapi/close-account", CloseAccountAPI(accountManager))
        postRoute("/sapi/change-password", ChangePasswordAPI(accountManager))

        getRoute("/s/:account/test_auth", TestAuthPage(accountManager))
        getRoute("/s/:account/auth", AuthPage(accountManager))
        postRoute("/s/:account/sync", SyncPage(accountManager))
    }

    private fun getRoute(path: String, route: Route) {
        Spark.get(path, route)
        Spark.get(path + "/", route)
    }

    private fun postRoute(path: String, route: Route) {
        Spark.post(path, route)
        Spark.post(path + "/", route)
    }
}