package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import kotlinx.html.*
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.config.ConfigManager

class MainPage(private val am: AccountManager,
               private val mainPagePath: String) : Route {
    private val syncConfig = Kodein.global.instance<ConfigManager>().module<SyncConfigModule>()

    //language=html
    override fun handle(request: Request, response: Response): Any {
        val username = request.cookie("username")
        val token = request.cookie("token")

        // Redirect logged in users
        if(username != null && token != null && am.authToken(username, token)) {
            response.redirect("/account")
            return ""
        }

        return SiteTemplate(mainPagePath).build(syncConfig.name, showLogout = false) {
            script(src = "https://www.google.com/recaptcha/api.js?onload=recaptchaOnload&render=explicit") {
                async = true
                defer = true
            }

            //language=js
            script {
                unsafe {
                    raw("""
function recaptchaOnload() {
    renderCaptcha('login_captcha', 'finishLogin');
    renderCaptcha('register_captcha', 'finishRegister');
}

function renderCaptcha(elementId, callback) {
    grecaptcha.render(elementId, {
        sitekey: '${syncConfig.recaptchaSiteKey}',
        callback: callback,
        size: 'invisible'
    });
}

window.onload = function() {
    let loginForm = $(".login-form");
    let registerForm = $(".register-form");

    window.finishLogin = function(token) {
        grecaptcha.reset(0);
        loginForm.api({
            action: 'auth',
            method : 'POST',
            data: {
                register: false,
                username: $('[name=l-username]').val(),
                password: $('[name=l-password]').val(),
                "g-recaptcha-response": token
            },
            on: 'now',
            onFailure: function(e) {
                if(e.error != null)
                    showError(loginForm, e.error);
                else
                    showError(loginForm, "Failed to connect to server!");
            },
            onSuccess: function(e) {
                authClient($('[name="l-username"]').val(), e.data.token);
            }
        });
    };

    window.finishRegister = function(token) {
        grecaptcha.reset(1);
        registerForm.api({
            action: 'auth',
            method : 'POST',
            data: {
                register: true,
                username: $('[name=r-username]').val(),
                password: $('[name=r-password]').val(),
                "g-recaptcha-response": token
            },
            on: 'now',
            onFailure: function(e) {
                if(e.error != null)
                    showError(registerForm, e.error);
                else
                    showError(registerForm, "Failed to connect to server!");
            },
            onSuccess: function(e) {
                authClient($('[name="r-username"]').val(), e.data.token);
            }
        });
    };

    function authClient(name, token) {
        $('.auth-dimmer').dimmer({
            closable: false
        }).dimmer('show');

        Cookies.set('${SiteTemplate.USERNAME_COOKIE}', name);
        Cookies.set('${SiteTemplate.TOKEN_COOKIE}', token);

        window.location.href = "/account";
    }

    function showError(form, message) {
        form.removeClass("success");
        form.addClass("error");
        form.find(".error.message").text(message);
    }

    loginForm.form({
        fields: {
            username: {
                identifier: "l-username",
                rules: [
                    {
                        type: 'empty',
                        prompt: 'Please enter a username'
                    }
                ]
            },
            password: {
                identifier: 'l-password',
                rules: [
                    {
                        type: 'empty',
                        prompt: 'Please enter a password'
                    }
                ]
            }
        },
        onSuccess: function() {
            grecaptcha.execute(0);
            return false;
        }
    });
    registerForm.form({
        fields: {
            username: {
                identifier: "r-username",
                rules: [
                    {
                        type: 'empty',
                        prompt: 'Please enter a username'
                    },
                    {
                        type: 'maxLength[100]',
                        prompt: 'Please use a shorter username'
                    }
                ]
            },
            password: {
                identifier: 'r-password',
                rules: [
                    {
                        type: 'empty',
                        prompt: 'Please enter a password'
                    }
                ]
            },
            confirmPassword: {
                identifier: 'r-confirm-password',
                rules: [
                    {
                        type: 'empty',
                        prompt: 'Please re-enter your password'
                    },
                    {
                        type: 'match[r-password]',
                        prompt: 'Your confirmed password is different from your password! Please re-type your passwords.'
                    }
                ]
            }
        },
        onSuccess: function() {
            grecaptcha.execute(1);
            return false;
        }
    });
};
                     """)
                }
            }

            div(classes = "ui two column divided stackable grid") {
                div(classes = "row") {
                    div ("column") {
                        h1("ui header") {
                            +"Login"
                        }

                        div(classes = "ui form login-form") {
                            div("field") {
                                label { +"Username" }
                                input(InputType.text, name = "l-username")
                            }
                            div("field") {
                                label { +"Password" }
                                input(InputType.password, name = "l-password")
                            }
                            div("ui error message")
                            button(classes = "ui primary submit button") {
                                +"Login"
                            }
                        }
                    }

                    div ("column") {
                        h1("ui header") {
                            +"Create account"
                        }

                        div(classes = "ui form register-form") {
                            div("field") {
                                label { +"Username" }
                                input(InputType.text, name = "r-username")
                            }
                            div("field") {
                                label { +"Password" }
                                input(InputType.password, name = "r-password")
                            }
                            div("field") {
                                label { +"Confirm password" }
                                input(InputType.password, name = "r-confirm-password")
                            }
                            div("ui error message")
                            button(classes = "ui primary submit button") {
                                +"Create account"
                            }
                        }
                    }
                }
            }

            div { id = "login_captcha" }
            div { id = "register_captcha" }

            div("ui page dimmer auth-dimmer") {
                div("ui text huge loader") {
                    +"Loading..."
                }
            }
        }
    }
}
