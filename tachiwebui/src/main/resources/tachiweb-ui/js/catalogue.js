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
let filterBox;
let filtersSearchBtn;
let filtersResetBtn;

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

let filtersCache = {};
let currentFilters = null;

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
    filterBox = $("#filters_box");

    filtersSearchBtn = $("#filters_search_btn");
    filtersResetBtn = $("#filters_reset_btn");

    setupFilterToggle();
    setupFilterMenuButtons();
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

    //Workaround writing-mode bug
    requestAnimationFrame(tryReflowFiltersBtnText);

    updateFiltersBtnText();
}

function tryReflowFiltersBtnText() {
    if(toggleFiltersBtnText.height() <= 0) {
        toggleFiltersBtnText.css("display", "none");
        //Wait until browser has reflowed the page
        requestAnimationFrame(function() {
            toggleFiltersBtnText.css("display", "initial");
            requestAnimationFrame(tryReflowFiltersBtnText);
        });
    } else {
        requestAnimationFrame(tryReflowFiltersBtnText);
    }
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
    let searchText = rawElement(searchBox).value;
    if (searchText.trim() !== "") {
        searchState.query = searchText;
    } else {
        //Set to null if no query
        searchState.query = null;
    }
    searchState.filters = currentFilters;
    scrollBox.scrollTop(0);
    clearMangas();
    refreshCatalogue();
}

function setupScrollBox() {
    scrollBox.on('scroll', function () {
        tryLoadNextPage();
    });
}

function tryLoadNextPage() {
    if (scrollBox.scrollTop() + scrollBox.innerHeight() >= rawElement(scrollBox).scrollHeight - scrollEndPadding) {
        if (searchState.hasNextPage && !isRefreshing()) {
            searchState.page++;
            refreshCatalogue();
        }
    }
}

function selectLoggedInSource() {
    for (let i = 0; i < currentSources.length; i++) {
        let source = currentSources[i];
        if (isLoggedIn(source)) {
            selectSource(source);
            if(currentRequest !== null)
                currentRequest.cancel();
            refreshCatalogue();
            return;
        }
    }
}

function selectSource(source) {
    let rawSourcesSelect = rawElement(sourcesSelect);
    for (let i = 0; i < rawSourcesSelect.options.length; i++) {
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
        let selectedSource = getCurrentSource();
        if (selectedSource && !isLoggedIn(selectedSource)) {
            showLoginBox(selectedSource);
        } else {
            searchState.page = 1;
            searchState.hasNextPage = null;
            scrollBox.scrollTop(0);
            clearMangas();
            updateFilters(selectedSource.id);
            if(currentRequest !== null)
                currentRequest.cancel();
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
    let currentSourceID = parseInt(sourcesSelect.val());
    for (let currentSource of currentSources) {
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
        {
            enabled: true //Only get enabled sources
        },
        hideSpinner);
}

function isRefreshing() {
    return currentRequest && !currentRequest.completed;
}

function refreshCatalogue(oldRequest) {
    let request;
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
            //Try loading next page (give UI one ms to render)
            setTimeout(function() {
                tryLoadNextPage();
            }, 1);
        }
    }, function () {
        if (!request.canceled) {
            catalogueRefreshError(request);
        }
    }, null, function () {
        if (!request.canceled) {
            hideSpinner();
            request.completed = true;
        }
    }, null, null, null, function(xhr) {
        xhr.send(JSON.stringify({
            sourceId: rawElement(sourcesSelect).value,
            page: searchState.page,
            query: searchState.query,
            filters: searchState.filters
        }));
    });
    return request;
}

function updateSourcesUI() {
    clearElement(sourcesSelect);
    $.each(currentSources, function (index, value) {
        let displayText = value.name;
        if(value.lang.name.length > 0)
            displayText = value.lang.name.toUpperCase() + " - " + displayText;
        sourcesSelect.append($('<option/>', {
            value: value.id,
            text: displayText
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
 * Filters
 */
function getFiltersForSource(sourceId, success, done) {
    //Try cache
    let fromCache = filtersCache[sourceId];
    if(valid(fromCache)) {
        let clone = $.extend(true, [], fromCache);
        success(clone);
        done();
        return;
    }
    //Try network
    TWApi.Commands.GetFilters.execute(function(resp) {
        //Clone filter object before caching it
        filtersCache[sourceId] = $.extend(true, [], resp.content);
        success(resp.content);
    }, function() {
        //TODO Handle error
    }, {
        id: sourceId
    }, done);
}

function updateFilters(sourceId) {
    getFiltersForSource(sourceId, function(filters) {
        currentFilters = filters;
        updateFilterHtml(filters);
    }, function() {
        //TODO Update progress spinner
    });
}

let filterRenderers = {
    /* TODO Implementation of HEADER And SEPARATOR may not be correct.
       Waiting for validation on how they should actually look.
     */
    "HEADER": function(f) {
        let created = $('<aside class="mdc-typography--title mdc-typography--adjust-margin"></aside>');
        created.text(f.name);
        return created;
    },
    "SEPARATOR": function(f) {
        let created = $(`
            <div>
                <div class="filters-separator filters-fullwidth"></div>
                <aside class="mdc-typography--body2 mdc-typography--adjust-margin"></aside>
            </div>
        `);
        created.find("aside").text(f.name);
        return created;
    },
    "SELECT": function(f) {
        let created = $(`
            <div class="mdc-select filters-select" role="listbox" tabindex="0">
                <span class="mdc-select__selected-text"></span>
                <div class="mdc-simple-menu mdc-select__menu">
                    <ul class="mdc-list mdc-simple-menu__items">
                    </ul>
                </div>
            </div>
        `);

        let items = created.find(".mdc-simple-menu__items");
        for(let i = 0; i < f.values.length; i++) {
            let value = $(`
                <li class="mdc-list-item" role="option" tabindex="0"></li>
            `);
            value.text(f.values[i]);
            if(f.state === i) {
                value.prop('aria-selected', true);
            }
            items.append(value);
        }

        //Set label
        created.find(".mdc-select__selected-text").text(f.values[f.state]);

        let select = new MDCSelect(rawElement(created));
        select.listen('MDCSelect:change', function() {
            f.state = select.selectedIndex;
        });

        let wrapper = $(`<div class="filters-select-wrapper"></div>`);
        let label = $(`<h4 class="mdc-typography--subheading1 mdc-typography--adjust-margin filters-select-label"></h4>`);
        label.text(f.name + ":");
        wrapper.append(label);
        wrapper.append(created);

        return wrapper;
    },
    "TEXT": function(f) {
        let created = $(`
            <label class="mdc-textfield filters-textfield">
                <input type="text" class="mdc-textfield__input">
                <span class="mdc-textfield__label"></span>
            </label>
        `);

        //Set label
        created.find(".mdc-textfield__label").text(f.name);

        //Attach JS
        MDCTextfield.attachTo(rawElement(created));

        //Set value
        let textField = created.find("input");

        textField.val(f.state);

        textField.on('input', function() {
            f.state = textField.val();
        });

        return created;
    },
    "CHECKBOX": function(f) {
        let form = $(`
            <div class="filters-checkbox-wrapper mdc-form-field">
              <div class="mdc-checkbox">
                <input type="checkbox"
                       class="filter-checkbox mdc-checkbox__native-control"/>
                <div class="mdc-checkbox__background">
                  <svg class="mdc-checkbox__checkmark"
                       viewBox="0 0 24 24">
                    <path class="mdc-checkbox__checkmark__path"
                          fill="none"
                          stroke="white"
                          d="M1.73,12.91 8.1,19.28 22.79,4.59"/>
                  </svg>
                  <div class="mdc-checkbox__mixedmark"></div>
                </div>
              </div>

              <label class="filter-checkbox-label"></label>
            </div>
        `);

        let id = makeElementId();
        let label = form.find(".filter-checkbox-label");
        let checkbox = form.find(".filter-checkbox");

        label.text(f.name);
        label.prop('for', id);
        checkbox.attr('id', id);

        checkbox.prop('checked', f.state);
        checkbox.change(function() {
            f.state = this.checked;
        });

        let mdcCheckbox = new MDCCheckbox(rawElement(checkbox.parent()));
        let mdcForm = new MDCFormField(rawElement(form));
        mdcForm.input = mdcCheckbox;

        return form;
    },
    "TRISTATE": function(f) {
        let form = $(`
            <div class="filters-checkbox-wrapper mdc-form-field">
              <div class="mdc-checkbox">
                <input type="checkbox"
                       class="filter-checkbox mdc-checkbox__native-control"/>
                <div class="mdc-checkbox__background">
                  <svg class="mdc-checkbox__checkmark"
                       viewBox="0 0 24 24">
                    <path class="mdc-checkbox__checkmark__path"
                          fill="none"
                          stroke="white"
                          d="M1.73,12.91 8.1,19.28 22.79,4.59"/>
                  </svg>
                  <div class="mdc-checkbox__mixedmark"></div>
                </div>
              </div>

              <label class="filter-checkbox-label"></label>
            </div>
        `);

        let id = makeElementId();
        let label = form.find(".filter-checkbox-label");
        let checkbox = form.find(".filter-checkbox");

        label.text(f.name);
        label.prop('for', id);
        checkbox.attr('id', id);
        checkbox.data('indeterminate', false);

        if(f.state === TRISTATE_STATES.STATE_IGNORE) {
            checkbox.prop('indeterminate', true);
            checkbox.data('indeterminate', true);
        } else if(f.state === TRISTATE_STATES.STATE_INCLUDE)
            checkbox.prop('checked', true);
        else if(f.state === TRISTATE_STATES.STATE_EXCLUDE)
            checkbox.prop('checked', false);

        checkbox.click(function() {
            let realChecked = !checkbox.prop("checked");

            if(checkbox.data('indeterminate')) {
                checkbox.prop('checked', true);
                checkbox.prop('indeterminate', false);
                checkbox.data('indeterminate', false);
                f.state = TRISTATE_STATES.STATE_INCLUDE;
            } else {
                checkbox.prop('checked', false);
                if(realChecked) {
                    f.state = TRISTATE_STATES.STATE_EXCLUDE;
                } else {
                    checkbox.prop('indeterminate', true);
                    checkbox.data('indeterminate', true);
                    f.state = TRISTATE_STATES.STATE_IGNORE;
                }
            }
        });

        //Init checkbox JS
        let mdcCheckbox = new MDCCheckbox(rawElement(checkbox.parent()));
        let mdcForm = new MDCFormField(rawElement(form));
        mdcForm.input = mdcCheckbox;

        return form;
    },
    "GROUP": function(f) {
        let content = [];
        for(let state of f.state) {
            let rendered = filterRenderers[state._type](state);
            content.push(rendered);
        }
        return createExpandableFilter(f.name, content);
    },
    "SORT": function(f) {
        let contentWrapper = $('<ul class="mdc-list filters-fullwidth" style="margin-top: 0; padding-top: 0"></ul>');
        for(let i = 0; i < f.values.length; i++) {
            let rendered = $(`
                <li class="mdc-list-item">
                    <i class="filters-sort-icon material-icons mdc-list-item__start-detail" aria-hidden="true">
                    </i>
                    <span class="filters-sort-text"></span>
                </li>
            `);
            MDCRipple.attachTo(rawElement(rendered));
            rendered.find('.filters-sort-text').text(f.values[i]);

            rendered.click(function() {
                if(f.state.index === i) {
                    f.state.ascending = !f.state.ascending;
                } else {
                    f.state.ascending = false;
                    f.state.index = i;
                }
                updateState(f.state);
            });

            contentWrapper.append(rendered);
        }

        //Update sort HTML state
        function updateState(newState) {
            let children = contentWrapper.children();
            for(let i = 0; i < children.length; i++) {
                let entry = $(children.get(i));
                let icon = entry.find('.filters-sort-icon');

                if(newState !== undefined
                    && newState !== null
                    && i === newState.index) {
                    if(newState.ascending) {
                        icon.text("keyboard_arrow_up");
                    } else {
                        icon.text("keyboard_arrow_down");
                    }
                } else {
                    icon.text("");
                }
            }
        }

        updateState(f.state);

        return createExpandableFilter(f.name, [contentWrapper]);
    }
};

function createExpandableFilter(name, content) {
    let group = $('<div class="filters-group"><div>');
    let header = $(`
        <div class="filters-group-header mdc-elevation--z4 filters-fullwidth">
            <aside class="filters-group-text mdc-typography--body2"></aside>
            <span class="filters-group-icon material-icons">arrow_drop_down</span>
        </div>
    `);
    header.find('aside').text(name);
    let contentElement = $('<div class="filters-group-content"></div>');
    for(let entry of content) {
        contentElement.append(entry);
    }
    group.append(header);
    group.append(contentElement);
    let hidden = false;
    let icon = header.find('.filters-group-icon');
    header.click(function() {
        if(hidden) {
            contentElement.slideDown(250);
            icon.removeClass("filters-group-icon-closed");
        } else {
            contentElement.slideUp(250);
            icon.addClass("filters-group-icon-closed");
        }
        hidden = !hidden;
    });
    return group;
}

const TRISTATE_STATES = {
    "STATE_IGNORE": 0,
    "STATE_INCLUDE": 1,
    "STATE_EXCLUDE": 2
};

function updateFilterHtml(newFilters) {
    filterBox.empty();
    for(filter of newFilters) {
        let rendered = filterRenderers[filter._type](filter);
        filterBox.append(rendered);
    }
}

function setupFilterMenuButtons() {
    filtersSearchBtn.click(function() {
        performSearch();
    });
    filtersResetBtn.click(function() {
        updateFilters(getCurrentSource().id);
    });
}

//Make an unused ID
let usedElementIds = [];
function makeElementId() {
    let text = "";
    let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for(let i = 0; i < 5 || usedElementIds.includes(text) ; i++)
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    usedElementIds.push(text);

    return text;
}

/**
 * Basic cancelable request
 **/
function Request() {
    let that = this;
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