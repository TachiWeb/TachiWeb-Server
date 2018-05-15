package xyz.nulldev.ts.syncdeploy

import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.api.http.sync.SyncRoute
import xyz.nulldev.ts.ext.disableCache
import xyz.nulldev.ts.sandbox.Sandbox

class SyncPage(val am: AccountManager): Route {
    override fun handle(request: Request, response: Response): Any? {
        response.disableCache()
        val account = request.params(":account")
        val token = request.headers("TW-Session")

        val success = am.authToken(account, token)
        if(!success)
            //language=json
            return """{"success": false, "error": "Not authenticated!"}"""

        // Hand off request to sandboxed, real sync route
        return am.lockAcc(account) {
            // Construct sandbox for this account
            Sandbox(it.configFolder).use { sandbox ->
                // Hand off request
                SyncRoute(sandbox.kodein).handle(request, response)
            }
        }
    }
}