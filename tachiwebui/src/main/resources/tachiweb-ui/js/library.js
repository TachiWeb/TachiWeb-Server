var librarySpinner;
var libraryWrapper;
var currentManga = [];
var unreadCheckbox;
var downloadedCheckbox;
var filters;
let editCategoriesBtn;

let editingCategories = false;
let currentCategories;

let addCategoryDialog;
let addCategoryDialogName;
let addCategoryDialogAddBtn;
let addCategoryDialogCloseBtn;
let renameCategoryDialog;
let renameCategoryDialogName;
let renameCategoryDialogAddBtn;
let renameCategoryDialogCloseBtn;

let currentRenamingCategory = null;

const DEFAULT_CATEGORY_ID = "_default";

function resetFilters() {
    filters = {
        onlyUnread: false,
        onlyDownloaded: false,
        text: ""
    };
}
function mapFiltersToUI() {
    mdlCheckboxCheck(unreadCheckbox, filters.onlyUnread);
    mdlCheckboxCheck(downloadedCheckbox, filters.onlyDownloaded);
}
resetFilters();

function onLoad() {
    librarySpinner = $(".loading_spinner");
    libraryWrapper = $("#library_wrapper");

    addCategoryDialog = $("#add_category_dialog");
    addCategoryDialogName = $("#add_category_dialog_name");
    addCategoryDialogAddBtn = $("#add_category_dialog_add_btn");
    addCategoryDialogCloseBtn = $("#add_category_dialog_close_btn");

    renameCategoryDialog = $("#rename_category_dialog");
    renameCategoryDialogName = $("#rename_category_dialog_name");
    renameCategoryDialogAddBtn = $("#rename_category_dialog_add_btn");
    renameCategoryDialogCloseBtn = $("#rename_category_dialog_close_btn");

    editCategoriesBtn = $("#edit_categories_btn");

    setupFilters();
    setupUpdateButton();
    setupFavoriteListener();
    setupEditCategoriesBtn();
    setupAddCategoryDialog();
    setupRenameCategoryDialog();
    updateLibrary();
}

function setupAddCategoryDialog() {
    if (!rawElement(addCategoryDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(addCategoryDialog));
    }
    addCategoryDialogCloseBtn.click(function () {
        rawElement(addCategoryDialog).close();
    });
    addCategoryDialogAddBtn.click(function() {
        rawElement(addCategoryDialog).close();

        let name = addCategoryDialogName.val();

        for(let category of currentCategories) {
            if(category.name === name) {
                snackbarError("A category with this name already exists!");
                return;
            }
        }
        serverAddCategory(name);
    });
}
function setupRenameCategoryDialog() {
    if (!rawElement(renameCategoryDialog).showModal) {
        dialogPolyfill.registerDialog(rawElement(renameCategoryDialog));
    }
    renameCategoryDialogCloseBtn.click(function () {
        rawElement(renameCategoryDialog).close();
    });
    renameCategoryDialogAddBtn.click(function() {
        rawElement(renameCategoryDialog).close();

        let name = renameCategoryDialogName.val();

        for(let category of currentCategories) {
            if(category.name === name) {
                if(category.id !== currentRenamingCategory.id)
                    snackbarError("A category with this name already exists!");
                return;
            }
        }
        serverRenameCategory(currentRenamingCategory.id, name);
    });
}

function setupFavoriteListener() {
    BrowserCommand.Favorite.on(updateLibrary);
}

function updateLibrary() {
    showSpinner();
    TWApi.Commands.Library.execute(function(res) {
        currentManga = res.content;
        applyAndUpdate(currentManga);
    }, libraryUpdateError, null, hideSpinner);
}
function setupUpdateButton() {
    $("#refresh_btn").click(function () {
        updateServerLibrary();
    });
}
function setupEditCategoriesBtn() {
    //TODO Make this button also exit category edit mode
    editCategoriesBtn.click(function() {
        beginEditingCategories();
    });
}
function updateServerLibrary() {
    showSpinner();
    var currentOnComplete = function () {
        hideSpinner();
        updateLibrary();
    };
    for (var i = 0; i < currentManga.length; i++) {
        var manga = currentManga[i];
        currentOnComplete = function (manga, lastOnComplete) {
            return function () {
                updateManga(manga, lastOnComplete);
            };
        }(manga, currentOnComplete);
    }
    currentOnComplete();
}
function updateManga(manga, onComplete) {
    console.log("Updating: " + manga.title + " (" + manga.id + ")");
    TWApi.Commands.Update.execute(null, function() {
        mangaUpdateError(manga.title);
    }, {
        mangaId: manga.id,
        updateType: "CHAPTERS"
    }, onComplete);
}

function serverReorderCategories(newOrder) {
    let categories = [];
    for(category of newOrder)
        if(category.id !== DEFAULT_CATEGORY_ID)
            categories.push(category.id);
    TWApi.Commands.EditCategories.execute(function(res) {
        for(let key of Object.keys(res.content)) {
            for(let obj of newOrder) {
                if(obj.id === parseInt(key))
                    obj.order = res.content[key];
            }
        }
        currentCategories = newOrder;
        sortCategories(currentCategories);
        showEditCategoriesUI(currentCategories.slice(0));
    }, function() {
        serverReorderCategoryError(newOrder);
    }, {
        operation: "REORDER",
        categories: categories
    });
}

function showSpinner() {
    librarySpinner.css("opacity", 1);
}

function hideSpinner() {
    librarySpinner.css("opacity", 0);
}

function updateLibraryUI(mangas) {
    //Construct categories
    var categories = {};
    for (var i = 0; i < mangas.length; i++) {
        var manga = mangas[i];
        var mCategories = manga.categories.slice(0);
        if (mCategories.length <= 0) {
            mCategories.push("Default");
        }
        for (var a = 0; a < mCategories.length; a++) {
            var categoryName = mCategories[a];
            let categoryId = mCategories[a];
            var category = categories[categoryId];
            if (!category) {
                category = {
                    content: [],
                    id: categoryId,
                    name: categoryName
                };
                categories[categoryId] = category;
            }
            category.content.push(manga);
        }
    }
    //Remove old entries
    clearElement(libraryWrapper[0]);
    var categoryKeys = Object.keys(categories);
    if (categoryKeys.length <= 1) {
        //Append directly if we don't have tabs
        appendMangas(mangas, libraryWrapper[0]);
    } else {
        for (i = 0; i < categoryKeys.length; i++) {
            let categoryId = categoryKeys[i];
            let category = categories[categoryId];
            let categorySplitter = createCategorySplitter(category);
            libraryWrapper[0].appendChild(categorySplitter);
            //Actually append mangas
            appendMangas(category.content, libraryWrapper[0]);
        }
    }
    //Make sure MDL gets content changes
    componentHandler.upgradeElement(libraryWrapper[0]);
}

function createCategorySplitter(category) {
    let categorySplitter = document.createElement("div");
    categorySplitter.className = "list_header";
    categorySplitter.textContent = category.name;
    categorySplitter.dataset.categoryId = category.id;
    return categorySplitter;
}

function beginEditingCategories() {
    TWApi.Commands.GetCategories.execute(function(res) {
        editingCategories = true;
        sortCategories(res.content);
        currentCategories = res.content;
        showEditCategoriesUI(res.content.slice(0));
    }, function() {
        beginEditingCategoriesError();
    });
}

function serverAddCategory(name) {
    TWApi.Commands.EditCategories.execute(function(res) {
        let category = {
            id: res.content.id,
            name: name,
            order: res.content.order,
        };
        currentCategories.push(category);
        sortCategories(currentCategories);
        showEditCategoriesUI(currentCategories.slice(0));
    }, function() {
        serverAddCategoryError(name)
    }, {
        operation: "CREATE",
        name: name
    });
}

function serverRenameCategory(id, newName) {
    TWApi.Commands.EditCategories.execute(function() {
        for(let category of currentCategories) {
            if(category.id === id) {
                category.name = newName
            }
        }
        showEditCategoriesUI(currentCategories.slice(0));
    }, function() {
        serverRenameCategoryError(id, newName);
    }, {
        operation: "RENAME",
        id: id,
        name: newName
    });
}

function serverDeleteCategory(id) {
    TWApi.Commands.EditCategories.execute(function() {
        currentCategories = currentCategories.filter(function(c){return c.id !== id;});
        sortCategories(currentCategories);
        showEditCategoriesUI(currentCategories.slice(0));
    }, function() {
        serverDeleteCategory(id);
    }, {
        operation: "DELETE",
        id: id
    });
}

function sortCategories(categories) {
    //Sort categories by order
    categories.sort(function(a, b) {
        return a.order - b.order;
    });
}

function showEditCategoriesUI(categories) {
    //Add default category
    categories.unshift({
        name: "Default",
        id: DEFAULT_CATEGORY_ID
    });

    let upgradeQueue = [];

    //Clear old entries
    clearElement(libraryWrapper[0]);
    for (let category of categories) {
        let categorySplitter = createCategorySplitter(category);

        //Add category controls
        if(category.id !== DEFAULT_CATEGORY_ID) {
            let categoryIndex = currentCategories.indexOf(category);

            function ctrlBtn(icon, tooltip, disabled) {
                let div = document.createElement("span");
                let button = document.createElement("button");
                button.className = "mdl-button mdl-js-button mdl-button--icon list-header-btn";
                button.id = "category_ctrl_btn_" + icon + "_" + categoryIndex;
                button.disabled = disabled;
                let iconElement = document.createElement("i");
                iconElement.className = "material-icons";
                iconElement.textContent = icon;
                button.appendChild(iconElement);
                let toolTipElement = document.createElement("div");
                toolTipElement.className = "mdl-tooltip";
                toolTipElement.setAttribute("data-mdl-for", button.id);
                toolTipElement.textContent = tooltip;
                div.appendChild(button);
                div.appendChild(toolTipElement);
                upgradeQueue.push(button);
                upgradeQueue.push(toolTipElement);
                return div;
            }

            let controls = document.createElement("span");
            controls.className = "list-header-controls";

            let moveUpBtn = ctrlBtn("keyboard_arrow_up", "Move up", categoryIndex <= 0);
            $(moveUpBtn).click(function () {
                let newOrder = currentCategories.slice(0);
                arraymove(newOrder, categoryIndex, categoryIndex - 1);
                serverReorderCategories(newOrder);
            });
            controls.appendChild(moveUpBtn);

            let moveDownBtn = ctrlBtn("keyboard_arrow_down", "Move down", categoryIndex >= currentCategories.length - 1);
            $(moveDownBtn).click(function () {
                let newOrder = currentCategories.slice(0);
                arraymove(newOrder, categoryIndex, categoryIndex + 1);
                serverReorderCategories(newOrder);
            });
            controls.appendChild(moveDownBtn);

            let renameBtn = ctrlBtn("mode_edit", "Rename");
            $(renameBtn).click(function() {
                rawElement(renameCategoryDialogName.parent()).MaterialTextfield.change(category.name);
                currentRenamingCategory = category;
                rawElement(renameCategoryDialog).showModal();
            });
            controls.appendChild(renameBtn);

            let deleteBtn = ctrlBtn("delete", "Delete");
            $(deleteBtn).click(function() {
                serverDeleteCategory(parseInt(categorySplitter.dataset.categoryId));
            });
            controls.appendChild(deleteBtn);
            categorySplitter.appendChild(controls);
        }

        libraryWrapper[0].appendChild(categorySplitter);
        //Actually append mangas
        let mangas = [];
        for(let manga of currentManga) {
            if(manga.categories.length <= 0 && category.id === DEFAULT_CATEGORY_ID) {
                mangas.push(manga);
            } else {
                for(let mangaCategory of manga.categories) {
                    if(mangaCategory.id === category.id) {
                        mangas.push(manga);
                    }
                }
            }
        }
        appendMangas(mangas, libraryWrapper[0]);
    }

    //Append add category button
    let addCategorySplitter = createCategorySplitter({name: "+ Add category", id: "_addCategory"});
    addCategorySplitter.classList.add("add-category-list-header");
    $(addCategorySplitter).click(function() {
        rawElement(addCategoryDialogName.parent()).MaterialTextfield.change("");
        rawElement(addCategoryDialog).showModal();
    });
    libraryWrapper[0].appendChild(addCategorySplitter);

    //Make sure MDL gets content changes
    componentHandler.upgradeElement(libraryWrapper[0]);
    for(element of upgradeQueue)
        componentHandler.upgradeElement(element);
}

function appendMangas(mangas, rootElement) {
    var libraryGrid = createGrid();
    rootElement.appendChild(libraryGrid);
    for (var i = 0; i < mangas.length; i++) {
        appendManga(mangas[i], libraryGrid, false, true);
    }
    componentHandler.upgradeElement(libraryGrid);
}

function createGrid() {
    var grid = document.createElement("div");
    grid.className = "mdl-grid";
    return grid;
}

function libraryUpdateError() {
    snackbar.showSnackbar({
        message: "Error getting library!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            updateLibrary();
        }
    });
}

function beginEditingCategoriesError() {
    snackbar.showSnackbar({
        message: "Error getting categories to edit!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            beginEditingCategories();
        }
    });
}

function serverAddCategoryError(name) {
    snackbar.showSnackbar({
        message: "Error adding category!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            serverAddCategory(name);
        }
    });
}

function serverReorderCategoryError(newOrder) {
    snackbar.showSnackbar({
        message: "Error moving category!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            serverReorderCategories(newOrder);
        }
    });
}

function serverRenameCategoryError(id, newName) {
    snackbar.showSnackbar({
        message: "Error renaming category!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            serverRenameCategory(id, newName);
        }
    });
}

function serverDeleteCategoryError(id) {
    snackbar.showSnackbar({
        message: "Error deleting category!",
        timeout: 2000,
        actionText: "Retry",
        actionHandler: function () {
            serverDeleteCategory(id);
        }
    });
}

function mangaUpdateError(manga) {
    snackbar.showSnackbar({
        message: "Error updating manga: '" + manga + "'!",
        timeout: 500
    });
}

function snackbarError(text) {
    snackbar.showSnackbar({
        message: text,
        timeout: 1000
    });
}

function applyAndUpdate(mangas) {
    var clonedMangas = mangas.slice(0);
    applyFilters(clonedMangas);
    updateLibraryUI(clonedMangas);
}

function applyFilters(mangas) {
    for (var i = mangas.length - 1; i >= 0; i--) {
        var manga = mangas[i];
        var remove = false;
        if (filters.onlyUnread && manga.unread <= 0) {
            remove = true;
        }
        if (!remove && filters.onlyDownloaded && !manga.downloaded) {
            remove = true;
        }
        if (!remove && filters.text.trim !== "" && manga.title.toLowerCase().indexOf(filters.text.toLowerCase()) <= -1) {
            remove = true;
        }
        if (remove) {
            mangas.splice(i, 1);
        }
    }
}

function setupFilters() {
    unreadCheckbox = $("#unread-chkbx");
    unreadCheckbox.change(function () {
        filters.onlyUnread = this.checked;
        applyAndUpdate(currentManga);
    });
    downloadedCheckbox = $("#download-chkbx");
    downloadedCheckbox.change(function () {
        filters.onlyDownloaded = this.checked;
        applyAndUpdate(currentManga);
    });
    $("#clear_filters_btn").click(function () {
        resetFilters();
        mapFiltersToUI();
        applyAndUpdate(currentManga);
    });
    $("#manga_search").on('input', function () {
        filters.text = $(this).val();
        applyAndUpdate(currentManga);
    });
}
