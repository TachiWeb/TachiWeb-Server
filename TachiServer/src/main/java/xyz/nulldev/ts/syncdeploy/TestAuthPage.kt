package xyz.nulldev.ts.syncdeploy

import spark.Request
import spark.Response
import spark.Route

class TestAuthPage(private val am: AccountManager) : Route {
    override fun handle(request: Request, response: Response): Any {
        response.disableCache()
        val account = request.params(":account")
        val token = request.headers("TW-Session")

        val success = am.authToken(account, token)

        //language=json
        return """{"success": $success}"""
    }
}