/**
 Global drawer
 **/
var drawer;
var drawerTitle;
var drawerNav;

function setupDrawer() {
    drawerTitle.text("TachiWeb");
    insertDrawerLink("Library", "library.html");
    insertDrawerLink("Catalogue", "catalogue.html");
    insertDrawerLink("Downloads", "downloads.html");
    insertDrawerLink("Settings", "options.html");
    insertDrawerLink("Backup/Restore", "restore_backup.html");
}

function insertDrawerLink(text, link) {
    var drawerElementLink = document.createElement("a");
    drawerElementLink.className = "mdl-navigation__link";
    drawerElementLink.href = link;
    drawerElementLink.textContent = text;
    rawElement(drawerNav).appendChild(drawerElementLink);
}

$(document).ready(function () {
    drawer = $(".mdl-layout__drawer");
    if (elementExists(drawer)) {
        drawerTitle = drawer.find(".mdl-layout-title");
        drawerNav = drawer.find(".mdl-navigation");
        if (elementExists(drawerTitle) && elementExists(drawerNav)) {
            setupDrawer();
            return;
        }
    }
    console.warn("Could not find drawer, not setting it up!");
});