var spinner;
var sourcesSelect;
var mangaGrid;
var scrollBox;
var searchBox;

var loginDialog;
var loginDialogTitle;
var loginDialogUsername;
var loginDialogPassword;
var loginDialogClose;
var loginDialogLogin;
var dialogSpinner;

let pageContent;
let toggleFiltersBtn;
let toggleFiltersBtnText;

var currentRequest = null;
var currentSources = [];
var currentManga = {};

var typingTimer;
var doneTypingInterval = 250;

var scrollEndPadding = 5000;

var searchState;
function resetSearchState() {
    searchState = {
        query: null,
        hasNextPage: null,
        page: 1
    };
}
resetSearchState();

function onLoad() {
    spinner = $("#catalogue_spinner");
    sourcesSelect = $("#sources_select");
    mangaGrid = $("#manga_grid");
    scrollBox = $(".manga-wrapper");
    searchBox = $("#search_box");

    loginDialog = $("#login_dialog");
    loginDialogTitle = $("#login_dialog_title");
    loginDialogUsername = $("#login_dialog_username");
    loginDialogPassword = $("#login_dialog_password");
    loginDialogClose = $("#login_dialog_close");
    loginDialogLogin = $("#login_dialog_login");
    dialogSpinner = $("#login_loading_spinner");

    pageContent = $(".page-content");
    toggleFiltersBtn = $("#hide_filter_menu_btn");
    toggleFiltersBtnText = $("#hide_filter_menu_btn_txt");

    setupFilterToggle();
    setupLoginDialog();
    setupSourcesSelect();
    setupScrollBox();
    setupSearchBox();
    setupFavoriteListener();
    refreshSources();
}

function setupFilterToggle() {
    const FILTERS_OPEN_CLASS = "page-content-filters-open";

    function updateFiltersBtnText() {
        if(pageContent.hasClass(FILTERS_OPEN_CLASS))
            toggleFiltersBtnText.html("&#x25C0;&nbsp; HIDE FILTERS &nbsp;&#x25C0;");
        else
            toggleFiltersBtnText.html("&#x25B6;&nbsp; SHOW FILTERS &nbsp;&#x25B6;");
    }

    toggleFiltersBtn.click(function() {
        pageContent.toggleClass(FILTERS_OPEN_CLASS);
        updateFiltersBtnText();
    });

    updateFiltersBtnText();
}

function setupLoginDialog() {
    if (!rawElement(loginDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(loginDialog));
    }
    loginDialogClose.click(function () {
        rawElement(loginDialog).close();
        selectLoggedInSource();
    });
    loginDialogLogin.click(function () {
        loginDialogClose.prop("disabled", true);
        loginDialogLogin.prop("disabled", true);
        loginDialogUsername.prop("disabled", true);
        loginDialogPassword.prop("disabled", true);
        dialogSpinner.css("display", "block");
        TWApi.Commands.SourceLogin.execute(refreshSources,
            function (error) {
                sourceLoginError(error);
                selectLoggedInSource();
            }, {
                sourceId: rawElement(sourcesSelect).value,
                username: loginDialogUsername.val(),
                password: loginDialogPassword.val()
            }, function () {
                rawElement(loginDialog).close();
            });
    })
}

function setupSearchBox() {
    /** http://stackoverflow.com/questions/4220126/run-javascript-function-when-user-finishes-typing-instead-of-on-key-up **/
    searchBox.on("keyup", function () {
        clearTimeout(typingTimer);
        typingTimer = setTimeout(performSearch, doneTypingInterval);
    });
    searchBox.on("keydown", function () {
        clearTimeout(typingTimer);
    });
}

function performSearch() {
    searchState.page = 1;
    searchState.hasNextPage = null;
    var searchText = rawElement(searchBox).value;
    if (searchText.trim() !== "") {
        searchState.query = searchText;
    } else {
        //Set to null if no query
        searchState.query = null;
    }
    scrollBox.scrollTop(0);
    clearMangas();
    refreshCatalogue();
}

function setupScrollBox() {
    scrollBox.on('scroll', function () {
        if (scrollBox.scrollTop() + scrollBox.innerHeight() >= rawElement(scrollBox).scrollHeight - scrollEndPadding) {
            if (searchState.hasNextPage && !isRefreshing()) {
                searchState.page++;
                refreshCatalogue();
            }
        }
    });
}

function selectLoggedInSource() {
    for (var i = 0; i < currentSources.length; i++) {
        var source = currentSources[i];
        if (isLoggedIn(source)) {
            selectSource(source);
            refreshCatalogue();
            return;
        }
    }
}

function selectSource(source) {
    var rawSourcesSelect = rawElement(sourcesSelect);
    for (var i = 0; i < rawSourcesSelect.options.length; i++) {
        if (rawSourcesSelect.options[i].value == source.id) {
            rawElement(rawSourcesSelect).selectedIndex = i;
            return;
        }
    }
}

function isLoggedIn(source) {
    if (!valid(source["logged_in"])) {
        return true;
    }
    return source["logged_in"];
}

function setupSourcesSelect() {
    sourcesSelect.change(function () {
        var selectedSource = getCurrentSource();
        if (selectedSource && !isLoggedIn(selectedSource)) {
            showLoginBox(selectedSource);
        } else {
            searchState.page = 1;
            searchState.hasNextPage = null;
            scrollBox.scrollTop(0);
            clearMangas();
            refreshCatalogue();
        }
    });
}

function clearMangas() {
    clearElement(mangaGrid);
    currentManga = {};
}

function updateCatalogueFavoriteStatus(mangaId, newFave) {
    var mangaElement = currentManga[mangaId];
    mangaElement.style.opacity = newFave ? 0.5 : 1;
}

function setupFavoriteListener() {
    BrowserCommand.Favorite.on(function (data) {
        updateCatalogueFavoriteStatus(data.mangaId, data.favorite);
    });
}

function showLoginBox(source) {
    loginDialogTitle.text(source.name + " Login");
    loginDialogUsername.val("");
    loginDialogPassword.val("");
    loginDialogClose.prop('disabled', false);
    loginDialogLogin.prop('disabled', false);
    loginDialogUsername.prop("disabled", false);
    loginDialogPassword.prop("disabled", false);
    loginDialog.data("source", source);
    dialogSpinner.css("display", "");
    rawElement(loginDialog).showModal();
}

function getCurrentSource() {
    var currentSourceID = parseInt(rawElement(sourcesSelect).value);
    for (var i = 0; i < currentSources.length; i++) {
        var currentSource = currentSources[i];
        if (currentSource.id === currentSourceID) {
            return currentSource;
        }
    }
    return null;
}

function showSpinner() {
    spinner.css("opacity", 1);
}

function hideSpinner() {
    spinner.css("opacity", 0);
}

function refreshSources() {
    showSpinner();
    TWApi.Commands.Sources.execute(function (res) {
            currentSources = res.content;
            updateSourcesUI();
            selectLoggedInSource();
        }, sourcesRefreshError,
        null,
        hideSpinner);
}

function isRefreshing() {
    return currentRequest && !currentRequest.completed;
}

function refreshCatalogue(oldRequest) {
    var request;
    if (oldRequest) {
        request = oldRequest;
    } else {
        request = new Request();
    }
    if (currentRequest === request) {
        currentRequest.cancel();
    }
    request.completed = false;
    currentRequest = request;
    showSpinner();
    TWApi.Commands.Catalogue.execute(function (res) {
        if (!request.canceled) {
            //Clear catalogue if page 1
            if (searchState.page === 1) {
                clearMangas();
            }
            //Set new search state
            searchState.hasNextPage = res["has_next"];
            //Add on new manga
            updateCatalogueUI(res.content);
        }
    }, function () {
        if (!request.canceled) {
            catalogueRefreshError(request);
        }
    }, {
        sourceId: rawElement(sourcesSelect).value,
        page: searchState.page,
        query: searchState.query
    }, function () {
        if (!request.canceled) {
            hideSpinner();
            request.completed = true;
        }
    });
    return request;
}

function updateSourcesUI() {
    clearElement(sourcesSelect);
    $.each(currentSources, function (index, value) {
        sourcesSelect.append($('<option/>', {
            value: value.id,
            text: value.name
        }));
    });
}

function updateCatalogueUI(manga) {
    $.each(manga, function (index, value) {
        currentManga[value.id] = appendManga(value, mangaGrid, true);
        updateCatalogueFavoriteStatus(value.id, value.favorite);
    });
}

/**
 * Basic cancelable request
 **/
function Request() {
    var that = this;
    this.canceled = false;
    this.cancel = function () {
        that.canceled = true;
    };
    this.completed = false;
}

function sourcesRefreshError() {
    snackbar.showSnackbar({
        message: "Error getting sources!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            refreshSources();
        }
    });
}

function catalogueRefreshError(request) {
    snackbar.showSnackbar({
        message: "Error getting catalogue!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            refreshCatalogue(request);
        }
    });
}

function sourceLoginError(message) {
    snackbar.showSnackbar({
        message: "Error logging in (" + message + ")!",
        timeout: 2000
    });
}