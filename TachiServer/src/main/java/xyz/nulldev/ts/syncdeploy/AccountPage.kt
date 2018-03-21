package xyz.nulldev.ts.syncdeploy

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import kotlinx.html.*
import spark.Request
import spark.Response
import spark.Route
import xyz.nulldev.ts.config.ConfigManager

class AccountPage(val am: AccountManager,
                  val mainPagePath: String): Route {
    private val syncConfig = Kodein.global.instance<ConfigManager>().module<SyncConfigModule>()

    override fun handle(request: Request, response: Response): Any {
        val username = request.cookie("username")
        val token = request.cookie("token")

        // Redirect users who are not logged in
        if(username == null || token == null || !am.authToken(username, token)) {
            response.redirect(mainPagePath)
            return ""
        }

        return SiteTemplate(mainPagePath).build(syncConfig.name + " - Dashboard") {
            div("ui center aligned container") {
                h1("ui dividing header") { +"Dashboard" }

                h2("ui header") {
                    +"Credentials"
                }

                table("ui definition table collapsing") {
                    style = "margin: auto"

                    tbody {
                        tr {
                            td { +"Server URL" }
                            td {
                                div("ui action input server-url") {
                                    style = "width:350px"

                                    input(InputType.text) {
                                        value = "${syncConfig.baseUrl}/s/$username/"
                                        readonly = true
                                    }
                                    button(classes ="ui teal right labeled icon button") {
                                        i("copy icon")
                                        +"Copy"
                                    }
                                }
                            }
                        }
                        tr {
                            td { +"Password" }
                            td { i { +"Use your account password" } }
                        }
                    }
                }

                h2("ui header") { +"Account" }
                button(classes ="ui button labeled icon change-password-button") {
                    i("lock icon")
                    +"Change password"
                }
                button(classes ="ui animated fade red button labeled icon delete-account-button") {
                    i("times icon")
                    div("visible content") { +"Close account" }
                    div("hidden content") { +"WIPE ALL DATA" }
                }

                h2("ui header") { +"Sync data" }
                a(href = "/account/data.zip", target = "_blank") {
                    button(classes = "ui button labeled icon download-button") {
                        i("download icon")
                        +"Download data"
                    }
                }
                button(classes ="ui red button labeled icon clear-data-button") {
                    i("trash icon")
                    +"Clear data"
                }
            }

        div("ui modal delete-account-modal") {
            div("header") { +"Delete account?" }
            div("content") {
                +"Closing your account will remove it completely from our servers. "
                b { +"All data in your account will be permanently deleted. " }
                +"You will no longer be able to sync with your account."
            }
            div("actions") {
                button(classes ="ui deny button") {
                    +"Cancel"
                }
                button(classes ="ui approve red button right icon labeled delete-account-modal-button") {
                    +"Close account"
                    i("times icon")
                }
            }
        }

        div("ui tiny modal clear-data-modal") {
            div("header") { +"Clear all sync data?" }
            div("content") {
                +"All your sync data will be permanently deleted from our servers."
            }
            div("actions") {
                button(classes ="ui deny button") {
                    +"Cancel"
                }
                button(classes ="ui approve red button right icon labeled clear-data-modal-button") {
                    +"Clear sync data"
                    i("trash icon")
                }
            }
        }

        div("ui tiny modal change-password-modal") {
            div("header") { +"Change password" }
            div("content") {
                form(classes = "ui form") {
                    id="change-password-form"

                    div("field") {
                        label { +"New password" }
                        input(InputType.password, name = "cp-password")
                    }
                    div("field") {
                        label { +"Confirm new password" }
                        input(InputType.password, name = "cp-confirm-password")
                    }
                    div("ui error message")
                }
            }
            div("actions") {
                button(classes ="ui deny button") {
                    +"Cancel"
                }
                button(classes ="ui submit green button right icon labeled change-password-form-submit-button") {
                    form = "change-password-form"

                    +"Change password"
                    i("check icon")
                }
            }
        }

        div("ui page dimmer loading-dimmer") {
            div("ui text huge loader") {
                +"Loading..."
            }
        }

        div("ui tiny modal complete-modal") {
            div("header")
            div("content")
            div("actions") {
                button(classes ="ui approve button") {
                    +"Close"
                }
            }
        }

        script {
            unsafe {
                //language=js
                raw("""
window.onload = function() {
    let sUrlBtn = $(".server-url");
    sUrlBtn.find("button").click(function() {
        sUrlBtn.find("input").select();
        document.execCommand("copy");
    });

    $(".delete-account-button").click(function() {
        $(".delete-account-modal").modal('show');
    });

    $(".clear-data-button").click(function() {
        $(".clear-data-modal").modal('show');
    });

    $(".delete-account-modal-button").click(function() {
        apiCall({
            text: "Closing account...",
            okHeader: "Account closed",
            okText: "Your account has been deleted.",
            failHeader: "Failed to close account",
            failText: "An unknown error occurred and we could not close your account!",
            okModalHide: logout
        }, {
            action: 'close account',
            method: 'GET'
        });
    });

    $(".clear-data-modal-button").click(function() {
        apiCall({
            text: "Clearing sync data...",
            okHeader: "Sync data cleared",
            okText: "Successfully cleared all sync data!",
            failHeader: "Failed to clear sync data",
            failText: "An unknown error occurred and the sync data could not be cleared!"
        }, {
            action: 'clear data',
            method: 'GET'
        });
    });

    function changePw() {
        apiCall({
            text: "Changing password...",
            okHeader: "Password changed",
            okText: "Successfully changed your password!",
            failHeader: "Failed to change password",
            failText: "An unknown error occurred and your password could not be changed!"
        }, {
            action: 'change password',
            method: 'POST',
            data: {
                password: $("[name=cp-password]").val()
            }
        });
    }

    let loadingDimmer = $(".loading-dimmer").dimmer({ closable: false });
    let loadingDimmerText = loadingDimmer.find(".text");
    let completeModal = $('.complete-modal');
    let completeModalHideListener = null;
    completeModal.modal({ onHidden: function() {
        if(completeModalHideListener != null)
            completeModalHideListener();
    }
    });

    function showLoadingDimmer(text) {
        loadingDimmer.dimmer('show');
        loadingDimmerText.text(text);
    }

    function hideLoadingDimmer() {
        loadingDimmer.dimmer('hide');
    }

    function apiCall(args, obj) {
        showLoadingDimmer(args.text);

        obj.on = 'now';
        obj.onComplete = function() {
            hideLoadingDimmer();
        };
        obj.onSuccess = function() {
            completeModal.find('.header').text(args.okHeader);
            completeModal.find('.content').text(args.okText);
            completeModal.modal('show');

            completeModalHideListener = args.okModalHide;

            if(args.onOk != null)
                args.onOk();
        };
        obj.onFailure = function() {
            completeModal.find('.header').text(args.failHeader);
            completeModal.find('.content').text(args.failText);
            completeModal.modal('show');

            completeModalHideListener = args.failModalHide;

            if(args.onFail != null)
                args.onFail();
        };

        $('<div></div>').api(obj);
    }

    let changePasswordForm = $('#change-password-form');
    let changePasswordModal = $(".change-password-modal");
    changePasswordForm.form({
        fields: {
            password: {
                identifier: 'cp-password',
                rules: [
                    {
                        type: 'empty',
                        prompt: 'Please enter a new password'
                    }
                ]
            },
            confirmPassword: {
                identifier: 'cp-confirm-password',
                rules: [
                    {
                        type: 'empty',
                        prompt: 'Please re-enter your new password'
                    },
                    {
                        type: 'match[cp-password]',
                        prompt: 'Your confirmed new password is different from your new password! Please re-type your passwords.'
                    }
                ]
            }
        },
        onSuccess: function() {
            changePw();
            changePasswordModal.modal('hide');
            return false;
        }
    });

    $(".change-password-button").click(function() {
        changePasswordModal.modal('show');
        changePasswordForm.form('clear');
        changePasswordForm.removeClass('error');
    });

    $('.change-password-form-submit-button').click(function(e) {
        e.preventDefault();
        changePasswordForm.form('validate form');
    });
};
                        """)
            }
        }
    }
}
//        language=html
/*        return """
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
            """*/
//    }
}