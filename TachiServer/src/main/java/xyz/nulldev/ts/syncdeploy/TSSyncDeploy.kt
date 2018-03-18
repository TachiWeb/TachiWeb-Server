package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import spark.Route
import spark.Spark
import xyz.nulldev.ts.config.ConfigManager

class TSSyncDeploy: KodeinGlobalAware {
    private val config = instance<ConfigManager>().module<SyncConfigModule>()
    private val accountManager = AccountManager()

    fun bindSyncRoutes() {
        val mainPagePath = if(config.syncOnlyMode)
            ""
        else "/acc-login"
        getRoute(mainPagePath, MainPage())
        postRoute("/account", AccountPage(accountManager))
        getRoute("/s/:account/test_auth", TestAuthPage(accountManager))
        getRoute("/s/:account/auth", AuthPage(accountManager))
        postRoute("/s/:account/sync", SyncPage(accountManager))
        postRoute("/account/clear-data", ResetSyncDataPage(accountManager))
        postRoute("/account/change-password", ChangePasswordPage(accountManager))
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