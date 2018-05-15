package xyz.nulldev.ts.syncdeploy.api.endpoints

import org.apache.http.client.utils.URLEncodedUtils
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.syncdeploy.AccountManager
import xyz.nulldev.ts.syncdeploy.api.JsonError
import xyz.nulldev.ts.syncdeploy.api.JsonSuccess
import xyz.nulldev.ts.ext.disableCache
import java.nio.charset.Charset

class ChangePasswordAPI(private val am: AccountManager) : Route {
    override fun handle(request: Request, response: Response): Any {
        response.disableCache()
        val username = request.cookie("username")
        val token = request.cookie("token")

        // Validate token
        if(username == null || token == null || !am.authToken(username, token))
            return JsonError("Invalid auth token!")

        val parsedBody = URLEncodedUtils.parse(request.body(), Charset.defaultCharset()).associate {
            it.name to it.value
        }

        val password = parsedBody["password"] ?: return JsonError("No password specified!")

        if(password.isEmpty())
            return JsonError("Password cannot be empty!")

        am.confAccountPw(username, password)

        //language=html
        return JsonSuccess()
    }
}
