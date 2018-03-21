package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import com.github.salomonbrys.kodein.instance
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import xyz.nulldev.ts.config.ConfigManager

class SiteTemplate(private val mainPagePath: String) : KodeinGlobalAware {
    private val conf = instance<ConfigManager>().module<SyncConfigModule>()

    fun build(title: String, showLogout: Boolean = true, exec: DIV.() -> Unit): String {
        return createHTML(false).html {
            head {
                title(title)

                meta(name = "viewport", content = "width=device-width, initial-scale=1")

                // Semantic UI
                link(rel = "stylesheet",
                        type = "text/css",
                        href = "https://cdn.jsdelivr.net/npm/semantic-ui@2.3.0/dist/semantic.min.css")
            }
            body {
                div("ui compact top fixed inverted menu") {
                    div("header item") { +conf.name }

                    div("right menu") {
                        a(href = "https://github.com/TachiWeb/TachiServer", target = "_blank", classes = "item") {
                            i("github icon")
                            +"Source code"
                        }

                        if(showLogout)
                            div("item") {
                                button(classes = "ui button logout-button") {
                                    +"Logout"
                                }
                            }
                    }
                }

                div("ui container") {
                    style = "padding-top: 6em"

                    exec(this)
                }

                // JQuery
                script(src = "https://cdn.jsdelivr.net/npm/jquery@3.3.1/dist/jquery.min.js") {}
                // Cookie lib
                script(src = "https://cdn.jsdelivr.net/npm/js-cookie@2/src/js.cookie.min.js") {}
                // Semantic UI
                script(src = "https://cdn.jsdelivr.net/npm/semantic-ui@2.3.0/dist/semantic.min.js") {}
                // API
                script {
                    unsafe {
                        //language=js
                        raw("""
jQuery.fn.api.settings.api = {
  'auth' : '/sapi/auth',
  'clear data' : '/sapi/clear-data',
  'close account' : '/sapi/close-account',
  'change password' : '/sapi/change-password'
};
jQuery.fn.api.settings.successTest = function(response) {
  if(response && response.success) {
    return response.success;
  }
  return false;
};
jQuery.fn.api.settings.hideError = false;

function logout() {
    Cookies.set('token', null);
    Cookies.set('username', null);

    window.location.href = '$mainPagePath';
}

window.addEventListener('load', function() {
    $(".logout-button").click(logout);
});
                        """.trimIndent())
                    }
                }
            }
        }
    }

    companion object {
        val USERNAME_COOKIE = "username"
        val TOKEN_COOKIE = "token"
    }
}