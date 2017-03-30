var authDialog;
var loadingDialog;

var authDialogPassword;
var authSubmit;

var forwardUrl = QueryString.fu;

function onLoad() {
    authDialog = $("#auth_dialog");
    loadingDialog = $("#busy_dialog");

    authDialogPassword = $("#auth_dialog_password");
    authSubmit = $("#auth_submit");
    setupDialogs();
    setupLogin();
    //Show auth dialog
    rawElement(authDialog).showModal();
}

function setupDialogs() {
    //Dialog polyfills
    if (!rawElement(loadingDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(loadingDialog));
    }
    if (!rawElement(authDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(authDialog));
    }
}

function setupLogin() {
    authSubmit.click(function () {
        rawElement(authDialog).close();
        rawElement(loadingDialog).showModal();
        TWApi.Commands.Auth.execute(function () {
            if (!valid(forwardUrl)) {
                forwardUrl = "/";
            }
            //Redirect to forward URL
            window.location = forwardUrl;
        }, function () {
            authError();
            rawElement(loadingDialog).close();
            rawElement(authDialog).showModal();
        }, {password: authDialogPassword.val()})
    });
    //Enter button triggers login
    authDialogPassword.keyup(function (event) {
        if (event.keyCode == 13) {
            authSubmit.click();
        }
    });
}

function authError() {
    snackbar.showSnackbar({
        message: "Authentication error!",
        timeout: 2000
    });
}