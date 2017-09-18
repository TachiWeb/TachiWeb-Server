var pauseBtn;
var clearBtn;
var pauseTooltip;
var downloadList;
var pauseIcon;
var spinner;

var paused;
var currentDownloads;

const DOWNLOAD_UPDATE_FREQ = 250;

function onLoad() {
    pauseBtn = $("#toggle_state_btn");
    clearBtn = $("#clear_btn");
    pauseTooltip = $("#toggle_state_btn_tooltip");
    downloadList = $("#download_list");
    pauseIcon = $("#toggle_state_icon");
    spinner = $(".loading_spinner");

    setupButtons();
    hideSpinner();
    startUpdatingDownloads();
}
function setupButtons() {
    pauseBtn.click(function () {
        downloadsOperation(paused ? "RESUME" : "PAUSE", paused ? "resuming" : "pausing");
    });
    clearBtn.click(function () {
        downloadsOperation("CLEAR", "clearing");
    });
}
function startUpdatingDownloads() {
    var realUpdateDownloads = function () {
        updateDownloads(function () {
            setTimeout(realUpdateDownloads, DOWNLOAD_UPDATE_FREQ);
        });
    };
    realUpdateDownloads();
}
function updateDownloads(onComplete) {
    TWApi.Commands.GetDownloads.execute(function (res) {
        paused = res.paused;
        currentDownloads = res.downloads;
        updateDownloadsUI();
        updateButtons();
    }, downloadsUpdateError, null, onComplete);
}
function downloadsOperation(operation, operationName) {
    TWApi.Commands.DownloadsOperation.execute(function () {
        updateDownloadsUI();
        updateButtons();
    }, function () {
        downloadsOperationError(operationName);
    }, {
        operation: operation
    });
}
function updateDownloadsUI() {
    var oldScrollPos = downloadList.scrollTop();
    clearElement(downloadList);
    for (var i = 0; i < currentDownloads.length; i++) {
        var download = currentDownloads[i];
        var downloadElement = document.createElement("div");
        downloadElement.className = "download";
        var downloadTitle = document.createElement("div");
        downloadTitle.className = "download_row download_title";
        downloadTitle.textContent = download.manga_title;
        downloadElement.appendChild(downloadTitle);
        var downloadChapter = document.createElement("div");
        downloadChapter.className = "download_row download_chapter";
        downloadChapter.textContent = download.chapter_name;
        downloadElement.appendChild(downloadChapter);
        if (download.total_images) {
            var downloadedImages = download.downloaded_images;
            if (!downloadedImages) {
                downloadedImages = 0;
            }
            var downloadStatus = document.createElement("div");
            downloadStatus.className = "download_row download_status";
            downloadStatus.textContent = downloadedImages + "/" + download.total_images;
            downloadElement.appendChild(downloadStatus);
        }
        var downloadProgressWrapper = document.createElement("div");
        downloadProgressWrapper.className = "download_row download_progress";
        downloadProgressWrapper.appendChild(createProgressBar(download.progress));
        downloadElement.appendChild(downloadProgressWrapper);
        rawElement(downloadList).appendChild(downloadElement);
    }
    downloadList.scrollTop(oldScrollPos);
}
/**
 * Create a progress bar
 * @param percent Percentage of progress bar filled (from 0 - 1)
 */
function createProgressBar(percent) {
    var wrapper = document.createElement("div");
    wrapper.className = "progress";
    var bar = document.createElement("div");
    bar.className = "determinate";
    bar.style.width = (percent * 100) + "%";
    wrapper.appendChild(bar);
    return wrapper;
}
function updateButtons() {
    var newPauseIcon;
    var newPauseTooltip;

    if (paused) {
        newPauseIcon = "play_arrow";
        newPauseTooltip = "Resume Downloads";
    } else {
        newPauseIcon = "pause";
        newPauseTooltip = "Pause Downloads";
    }

    pauseIcon.html(newPauseIcon);
    pauseTooltip.text(newPauseTooltip);
    if (!currentDownloads || currentDownloads.length <= 0) {
        pauseBtn.hide(fadeSpeed);
        clearBtn.hide(fadeSpeed);
    } else {
        pauseBtn.show(fadeSpeed);
        clearBtn.show(fadeSpeed);
    }
}
function downloadsUpdateError() {
    snackbar.showSnackbar({
        message: "Error updating downloads!",
        timeout: 1000
    });
}
function downloadsOperationError(operation) {
    snackbar.showSnackbar({
        message: "Error " + operation + " downloads!",
        timeout: 1000
    });
}
/**
 * Show the loading spinner
 */
function showSpinner() {
    spinner.css("opacity", 1);
}

/**
 * Hide the loading spinner
 */
function hideSpinner() {
    spinner.css("opacity", 0);
}