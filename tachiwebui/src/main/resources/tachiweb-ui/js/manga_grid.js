const MANGA_CARD_LABEL = "manga_card_label";
/** Shared Manga Grid lib that allows displaying Manga in a grid format **/
function appendManga(manga, element, openInNewTab, showUnreadBadge) {
    var card = document.createElement("div");
    card.className = "mdl-card mdl-shadow--4dp manga_card mdl-cell mdl-cell--2-col mdl-button mdl-js-button mdl-js-ripple-effect";
    var img = document.createElement("img");
    img.className = "manga_card_img";
    img.src = TWApi.Commands.Cover.buildUrl({mangaId: manga.id});
    img.alt = manga.title;
    card.appendChild(img);
    var label = document.createElement("div");
    label.className = MANGA_CARD_LABEL + " manga_card_label_gradient";
    if (valid(OptionsApi)) {
        //Grab label type from options
        OptionsApi.onReady(function () {
            var background = OptionsApi.pref_manga_card_label_background;
            if (background === "gradient") {
                label.className = MANGA_CARD_LABEL + " manga_card_label_gradient";
            } else if (background === "solid") {
                label.className = MANGA_CARD_LABEL + " manga_card_label_solid";
            }
        });
    }
    label.textContent = manga.title;
    card.appendChild(label);
    $(card).click(function () {
        var builtUrl = "manga_info.html?id=" + manga.id + "&b=";
        if (openInNewTab) {
            window.open(builtUrl + "CLOSE", '_blank');
        } else {
            var currentUrl = window.location.href;
            window.location.href = builtUrl + encodeURIComponent(currentUrl);
        }
    });
    if (showUnreadBadge && manga.unread > 0) {
        var badge = document.createElement("div");
        badge.className = "badge";
        badge.textContent = manga.unread;
        card.appendChild(badge);
    }
    rawElement(element).appendChild(card);
    componentHandler.upgradeElement(card);
    componentHandler.upgradeElement(rawElement(element));
    return card;
}