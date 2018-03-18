package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.google.gson.JsonParser
import okhttp3.FormBody
import okhttp3.OkHttpClient
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.config.ConfigManager

class AccountPage(val am: AccountManager): Route {
    private val syncConfig = Kodein.global.instance<ConfigManager>().module<SyncConfigModule>()
    val client = OkHttpClient.Builder().build()
    val jsonParser = JsonParser()

    override fun handle(request: Request, response: Response): Any {
        response.disableCache()
        val ip = request.headers("X-Real-IP") ?: request.ip()
        val username = request.queryParams("username")
        val password = request.queryParams("password")
        val captcha = request.queryParams("g-recaptcha-response")

        if(username.isBlank())
            return "Username cannot be empty!"

        if(username.any { it !in am.VALID_USERNAME_CHARS })
            return "Username can only contain alphabetical characters, numerical characters, '@', '-' and '_'!"

        if(username.length > am.MAX_USERNAME_LENGTH)
            return "Username cannot be longer than: ${am.MAX_USERNAME_LENGTH} characters!"

        if(captcha == null || captcha.isBlank())
            return "Please complete the RECAPTCHA challenge!"

        val resp = client.newCall(okhttp3.Request.Builder()
                .url("https://www.google.com/recaptcha/api/siteverify")
                .post(FormBody.Builder()
                        .add("secret", syncConfig.recaptchaSecret)
                        .add("response", captcha)
                        .add("remoteip", ip)
                        .build()).build()).execute()

        val captchaStatus = jsonParser.parse(resp.body()!!.string()).asJsonObject["success"].asBoolean

        if(!captchaStatus)
            return "RECAPTCHA challenge failed!"

        lateinit var token: String

        //Auth account
        am.lockAcc(username) {
            if(it.configured) {
                //Auth as account exists
                if(!am.authAccount(username, password)) {
                    return "The supplied password does not match the username! If you are trying to sign-up for a new account, this username is already taken, please choose a different username!"
                }
            } else {
                //Create new account
                am.confAccount(username, password)
            }

            token = it.token.toString()
        }

        //language=html
        fun action(name: String, url: String) = """
        <form action='$url' method='POST' target="_blank">
            <input type='hidden' name='account' value='$username'/>
            <input type='hidden' name='token' value='$token'/>
            <input type='submit' value='$name'/>
        </form>
            """

        //language=html
        return """
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Account Panel</title>
    </head>
    <body>
        <p>Welcome back $username!</p>
        <br>
        <p><b>Sync Credentials:</b></p>
        <p><b>URL:</b> <span style='font-family: monospace;'>${syncConfig.baseUrl}/s/$username/</span></p>
        <p><b>Password:</b> <i>Use your account password</i></p>
        <br>
        <p>Delete all sync data:</p>
        ${action("Clear sync data", "/account/clear-data")}
        <p>Change your password:</p>
        ${action("Change password", "/account/change-password")}
    </body>
</html>
            """
    }
}