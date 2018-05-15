package xyz.nulldev.ts.syncdeploy.api.endpoints

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.google.gson.JsonParser
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.apache.http.client.utils.URLEncodedUtils
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.syncdeploy.AccountManager
import xyz.nulldev.ts.syncdeploy.SyncConfigModule
import xyz.nulldev.ts.syncdeploy.api.JsonError
import xyz.nulldev.ts.syncdeploy.api.JsonSuccess
import xyz.nulldev.ts.ext.disableCache
import java.nio.charset.Charset

class AuthAPI(val am: AccountManager): Route {
    private val syncConfig = Kodein.global.instance<ConfigManager>().module<SyncConfigModule>()
    val client = OkHttpClient.Builder().build()
    val jsonParser = JsonParser()

    override fun handle(request: Request, response: Response): Any {
        response.disableCache()

        val ip = if(syncConfig.ipHeader.isNotBlank())
            request.headers(syncConfig.ipHeader)
        else
            request.ip()

        // Process POST body
        val parsedBody = URLEncodedUtils.parse(request.body(), Charset.defaultCharset()).associate {
            it.name to it.value
        }
        val username = parsedBody["username"]!!
        val password = parsedBody["password"]!!
        val register = parsedBody["register"]!!.toBoolean()
        val captcha = parsedBody["g-recaptcha-response"]

        if (username.isBlank())
            return JsonError("Username cannot be empty!")

        if (username.any { it !in am.VALID_USERNAME_CHARS })
            return JsonError("Username can only contain alphabetical characters, numerical characters, '@', '-' and '_'!")

        if (username.length > am.MAX_USERNAME_LENGTH)
            return JsonError("Username cannot be longer than: ${am.MAX_USERNAME_LENGTH} characters!")

        if(password.isEmpty())
            return JsonError("Password cannot be empty!")

        if (captcha == null || captcha.isBlank())
            return JsonError("Please complete the RECAPTCHA challenge!")

        val resp = client.newCall(okhttp3.Request.Builder()
                .url("https://www.google.com/recaptcha/api/siteverify")
                .post(FormBody.Builder()
                        .add("secret", syncConfig.recaptchaSecret)
                        .add("response", captcha)
                        .add("remoteip", ip)
                        .build()).build()).execute()

        val captchaStatus = jsonParser.parse(resp.body()!!.string()).asJsonObject["success"].asBoolean

        if (!captchaStatus)
            return JsonError("RECAPTCHA challenge failed!")

        lateinit var token: String

        //Auth account
        am.lockAcc(username) {
            if (it.configured) {
                if(register)
                    return JsonError("An account already exists with this username!")

                //Auth as account exists
                if (!am.authAccount(username, password)) {
                    return JsonError("The supplied password does not match the username!")
                }
            } else {
                if(!register)
                    return JsonError("No account exists with this username!")

                //Create new account
                am.confAccount(username, password)
            }

            token = it.token.toString()
        }

        return JsonSuccess("token" to token)
    }
}
