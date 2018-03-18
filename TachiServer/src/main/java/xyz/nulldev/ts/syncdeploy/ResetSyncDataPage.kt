package xyz.nulldev.ts.syncdeploy

import spark.Request
import spark.Response
import spark.Route
import java.io.File

class ResetSyncDataPage(private val am: AccountManager) : Route {
    override fun handle(request: Request, response: Response): Any {
        val account = request.queryParams("account")
        val token = request.queryParams("token")

        val success = am.authToken(account, token)
        if(!success)
            return "Invalid auth token!"

        am.lockAcc(account) {
            File(it.folder, "tachiserver-data").deleteRecursively()
        }

        //language=html
        return "All sync data deleted. <b>You MUST disable and re-enable sync in Tachiyomi's settings screen on ALL devices. You WILL experience strange behavior if you do not do this!</b>"
    }
}