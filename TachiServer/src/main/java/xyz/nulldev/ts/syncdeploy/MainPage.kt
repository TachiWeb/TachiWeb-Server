package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.config.ConfigManager

class MainPage : Route {
    private val syncConfig = Kodein.global.instance<ConfigManager>().module<SyncConfigModule>()

    //language=html
    override fun handle(request: Request?, response: Response?) = """
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <title>Tachiyomi Sync Server</title>
</head>
<body>
<script>
    function onSubmit(token) {
        document.getElementById("login").submit();
    }
 </script>
<p>Welcome to the Tachiyomi sync server! If you have an account, you can log in below. <b>If you do not have an account and would like to create one, fill in your username and password below and click the button.</b></p>
<form id="login" action="/account" method="POST">
    Username:<br>
    <input type="text" name="username">
    <br>
    Password:<br>
    <input type="password" name="password">
    <br><br>
    <button
            class="g-recaptcha"
            data-sitekey="${syncConfig.recaptchaSiteKey}"
            data-callback="onSubmit">
        Login/Create account
    </button>
</form>
</body>
</html>
        """
}