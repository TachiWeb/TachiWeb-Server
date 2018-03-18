package xyz.nulldev.ts.syncdeploy

import spark.Request
import spark.Response
import spark.Route

class ChangePasswordPage(private val am: AccountManager): Route {
    override fun handle(request: Request, response: Response): Any {
        val account = request.queryParams("account")
        val token = request.queryParams("token")
        val password = request.queryParams("password")

        val success = am.authToken(account, token)
        if(!success)
            return "Invalid auth token!"

        return if(password == null) {
            //language=html
            """
                <p>Please enter the new password:</p>
                <form action="" method="POST">
                    <input type="hidden" name="account" value='$account'/>
                    <input type="hidden" name="token" value='$token'/>
                    <input type="password" name="password" value=''/>
                    <input type='submit'/>
                </form>
                """
        } else {
            am.confAccountPw(account, password)
            "Password successfully changed!"
        }
    }
}