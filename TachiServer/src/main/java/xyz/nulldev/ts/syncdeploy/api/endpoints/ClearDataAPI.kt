package xyz.nulldev.ts.syncdeploy.api.endpoints

import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.syncdeploy.AccountManager
import xyz.nulldev.ts.syncdeploy.api.JsonError
import xyz.nulldev.ts.syncdeploy.api.JsonSuccess
import xyz.nulldev.ts.syncdeploy.disableCache

class ClearDataAPI(private val am: AccountManager) : Route {
    override fun handle(request: Request, response: Response): Any {
        response.disableCache()
        val username = request.cookie("username")
        val token = request.cookie("token")

        // Validate token
        if(username == null || token == null || !am.authToken(username, token)) {
            return JsonError("Invalid auth token!")
        }

        am.lockAcc(username) {
            it.syncDataFolder.deleteRecursively()
        }

        //language=html
        return JsonSuccess()
    }
}
