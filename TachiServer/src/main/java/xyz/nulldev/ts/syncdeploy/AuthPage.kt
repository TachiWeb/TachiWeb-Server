package xyz.nulldev.ts.syncdeploy

import spark.Request
import spark.Response
import spark.Route

class AuthPage(private val am: AccountManager): Route {
    override fun handle(request: Request, response: Response): Any {
        response.disableCache()
        val account = request.params(":account")
        val password = request.queryParams("password")

        val success = am.authAccount(account, password)

        return if(success) {
            val token = am.lockAcc(account, Account::token)

            //language=json
            """{"success": true, "token": "$token"}"""
        } else {
            //language=json
            """{"success": false, "error": "Incorrect password!"}"""
        }
    }
}