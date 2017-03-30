var backButton;
var busyDialog;
var restoreDialog;
var restoreResDialog;

var restoreResTitle;
var restoreResBody;

var uploadButton;
var restoreButton;
var backupButton;
var restoreSubmitButton;
var restoreCloseButton;
var restoreResCloseButton;

var uploadFileName;

var selectedFile;

var backLink = QueryString.b;

function onLoad() {
    backButton = $(".back-button");

    busyDialog = $("#busy_dialog");
    restoreDialog = $("#restore_dialog");
    restoreResDialog = $("#restore_res_dialog");

    restoreResTitle = $("#restore_res_title");
    restoreResBody = $("#restore_res_body");

    uploadButton = $("#uploadBtn");
    restoreButton = $("#restore_button");
    backupButton = $("#backup_button");
    restoreSubmitButton = $("#restore_submit");
    restoreCloseButton = $("#restore_close_button");
    restoreResCloseButton = $("#restore_res_close_button");

    uploadFileName = $("#upload_file_name");

    setupDialogs();
    setupButtons();
    setupBackButton();
}
function setupBackButton() {
    backButton.click(function () {
        if (backLink) {
            window.location.href = backLink;
        } else {
            window.history.back();
        }
    });
}
function setupDialogs() {
    //Dialog polyfills
    if (!rawElement(busyDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(busyDialog));
    }
    if (!rawElement(restoreDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(restoreDialog));
    }
    if (!rawElement(restoreResDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(restoreResDialog));
    }
}
function setupButtons() {
    restoreButton.click(function () {
        rawElement(restoreDialog).showModal();
    });
    backupButton.click(function () {
        window.location.href = TWApi.Endpoints.Backup + "?force-download=true";
    });
    rawElement(uploadButton).onchange = function () {
        selectedFile = rawElement(uploadButton).files[0];
        rawElement(uploadFileName).value = selectedFile.name;
    };
    restoreSubmitButton.click(function () {
        if (!selectedFile) {
            return;
        }
        rawElement(restoreDialog).close();
        rawElement(busyDialog).showModal();
        TWApi.Commands.RestoreFile.execute(
            showRestoreSuccessDialog,
            showRestoreErrorDialog, null, function () {
                rawElement(busyDialog).close();
            }, null, null, function (builtUrl) {
                var xhr = new XMLHttpRequest();
                xhr.open("POST", builtUrl, true);
                return xhr;
            }, function (xhr) {
                var formData = new FormData();
                formData.append("uploaded_file", selectedFile);
                xhr.send(formData);
            });
    });
    restoreCloseButton.click(function () {
        rawElement(restoreDialog).close();
    });
    restoreResCloseButton.click(function () {
        rawElement(restoreResDialog).close();
    });
}
function showRestoreErrorDialog() {
    restoreResTitle.text("Restore failed!");
    restoreResBody.text("The restore failed, please try again later!");
    rawElement(restoreResDialog).showModal();
}
function showRestoreSuccessDialog() {
    restoreResTitle.text("Restore complete!");
    restoreResBody.text("The restore completed successfully!");
    rawElement(restoreResDialog).showModal();
}