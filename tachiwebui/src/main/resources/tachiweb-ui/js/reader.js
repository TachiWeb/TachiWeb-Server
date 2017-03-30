var imageManager = {};
var buttonManager = {};
var hudManager = {};

const imageSlideTime = 350;
const loaded = "loaded";
const SCROLL_LIMIT = 5;
const SCROLL_STEP = 0.2;

var mangaId = QueryString.m;
var mangaName = QueryString.mn;
var chapterId = QueryString.c;
var currentPage = QueryString.p;
var maxPages = parseInt(QueryString.mp);
if (!valid(currentPage) || currentPage < 0 || currentPage >= maxPages) {
    currentPage = 0;
}
var hasNextChapter = QueryString.nc === "true";
var hasPrevChapter = QueryString.pc === "true";
var backLink = QueryString.b;
var rightToLeft = QueryString.rtl;
if (!valid(rightToLeft)) {
    rightToLeft = false;
}

function onLoad() {
    setupHudManager();
    setupReader();
    setupButtonManager();
    updateButtons();
    loadNextImage();
    requestAnimationFrame(animate);
    setupOptions();
}
function setupOptions() {
    OptionsApi.onReady(function () {
        $("body").css("background", OptionsApi.pref_reader_theme_key);
    });
}
function setupReader() {
    console.log("Setting up reader...");
    setupImageManager();
}
function setupImageManager() {
    console.log("Setting up image manager...");
    imageManager = {};
    imageManager.readerRow = $("#reader_img_row");
    imageManager.imgLeft = $("#reader_img_left");
    imageManager.imgCenter = $("#reader_img_center");
    imageManager.imgRight = $("#reader_img_right");
    imageManager.selectedImage = 0;
    imageManager.readerRowTransform = 0;
    imageManager.docWidth = $(document).width();
    imageManager.rotation = 0;
    imageManager.animateReaderRow = function (to, onComplete) {
        var that = this;
        if (OptionsApi.pref_reader_enable_transitions) {
            this.animation = new TWEEN.Tween({x: this.readerRowTransform})
                .to({x: to}, imageSlideTime)
                .onUpdate(function () {
                    that.setReaderRowLoc(this.x);
                })
                .easing(TWEEN.Easing.Quartic.Out)
                .onComplete(function () {
                    if (valid(onComplete)) {
                        onComplete();
                    }
                });
            this.animation.start();
        } else {
            that.setReaderRowLoc(to);
            if (valid(onComplete)) {
                onComplete();
            }
        }
    };
    //Setup reader row
    imageManager.setReaderRowLoc = function (loc) {
        var that = this;
        that.readerRow.css("transform", "translateX(-" + loc + "px)");
        that.readerRowTransform = loc;
    };
    imageManager.readerElements = [];
    imageManager.wrapperElements = [];
    imageManager.setupReaderElements = function () {
        var that = this;
        that.docWidth = $(document).width();
        for (var i = 0; i < imageManager.wrapperElements.length; i++) {
            that.wrapperElements[i].css("width", (imageManager.docWidth - 200) + "px");
        }
    };
    var tmpImageArray = [];
    for (var i = 0; i < maxPages; i++) {
        var readerElement = document.createElement("div");
        readerElement.className = "reader_img_wrapper";
        tmpImageArray.push(readerElement);
        imageManager.wrapperElements.push($(readerElement));
        var nestedReaderElement = document.createElement("div");
        nestedReaderElement.className = "reader_img";
        imageManager.readerElements.push($(nestedReaderElement));
        readerElement.appendChild(nestedReaderElement);
        var jqueryReaderElement = $(nestedReaderElement);
        jqueryReaderElement.data(loaded, false);
        jqueryReaderElement.data("zoom", false);
    }
    //Deal with right to left
    if (rightToLeft) {
        tmpImageArray.reverse();
    }
    for (i = 0; i < tmpImageArray.length; i++) {
        imageManager.readerRow[0].appendChild(tmpImageArray[i]);
    }
    imageManager.setupReaderElements();
    imageManager.goToImage = function (image, onComplete, animate) {
        var that = this;
        if (valid(that.animation)) {
            that.animation.stop();
            that.animation = null;
        }
        var loc;
        var i;
        if (rightToLeft) {
            loc = imageManager.docWidth / 2;
            for (i = maxPages - 1; i > image; i--) {
                loc += imageManager.docWidth;
            }
        } else {
            loc = imageManager.docWidth / 2;
            for (i = 0; i < image; i++) {
                loc += imageManager.docWidth;
            }
        }
        if (animate) {
            this.animateReaderRow(loc, onComplete);
        } else {
            this.setReaderRowLoc(loc);
            if (valid(onComplete)) {
                onComplete();
            }
        }
        if (currentPage !== image) {
            updateReadingStatus(parseInt(image));
        }
        currentPage = image;
        updateHudPageNumber();
    };
    imageManager.goToSelectedImage = function (onComplete, animate) {
        this.goToImage(currentPage, onComplete, animate);
    };
    imageManager.getSelectedImage = function () {
        var that = this;
        return that.readerElements[that.selectedImage];
    };
    $(window).resize(function () {
        imageManager.setupReaderElements();
        imageManager.goToSelectedImage(null, false);
    });
    imageManager.goToSelectedImage(null, true);
}
function updateReadingStatus(page) {
    var parameters = {
        mangaId: mangaId,
        chapterId: chapterId,
        lastReadPage: page
    };
    //Set read if we are at last page
    if (page === parseInt(maxPages) - 1) {
        parameters.read = true
    }
    TWApi.Commands.ReadingStatus.execute(null, function () {
        console.warn("Failed to set reading status!");
    }, parameters)
}
function hasPrevPage() {
    return currentPage > 0;
}
function hasNextPage() {
    return currentPage < maxPages - 1;
}
//TODO Seamless chapters
function updateButtons() {
    if (hasPrevPage()) {
        enableButton(buttonManager.previousBtn);
    } else if (!hasPrevChapter) {
        disableButton(buttonManager.previousBtn);
    } else {
        crossChapterButton(buttonManager.previousBtn);
    }
    if (hasNextPage()) {
        enableButton(buttonManager.nextBtn);
    } else if (!hasNextChapter) {
        disableButton(buttonManager.nextBtn);
    } else {
        crossChapterButton(buttonManager.nextBtn);
    }
}
var disabledButtonClass = "rbtn_disabled";
var crossChapterButtonClass = "rbtn_cross_chapter";
function disableButton(button) {
    button.addClass(disabledButtonClass);
    button.removeClass(crossChapterButtonClass);
}
function enableButton(button) {
    button.removeClass(disabledButtonClass);
    button.removeClass(crossChapterButtonClass);
}
function crossChapterButton(button) {
    button.removeClass(disabledButtonClass);
    button.addClass(crossChapterButtonClass);
}
function activateZoom(image) {
    if (!image.data("zoom")) {
        image.data("zoom", true);
        image.click(function () {
            $.featherlight('<div class="dragscroll reader_zoom_img"><img class="reader_zoom_img_content" src="' + image.data('src') + '" style="transform: ' + getRotationCSS() + '" alt="Zoomable Image"/></div>', {});
            var content = $(".featherlight-content");
            var content_image = content.find(".reader_zoom_img_content");
            content.addClass("dragscroll");
            content.mousedown(function () {
                content.css("cursor", "move");
            });
            content.mouseup(function () {
                content.css("cursor", "");
            });
            var zoom = 1;

            function updateZoom() {
                content_image.css("transform", getRotationCSS() + " scale(" + zoom + ")");
            }

            content.mousewheel(function (event) {
                var scrollDiff = SCROLL_STEP * event.deltaY;
                var oldZoom = zoom;
                zoom = Math.min(Math.max(zoom + scrollDiff, 1), SCROLL_LIMIT);
                if (oldZoom !== zoom) {
                    updateZoom();
                    //Make sure we don't somehow scroll offscreen (apparently possible) and scroll towards center of screen
                    var iWidth = rawElement(content_image).width;
                    var iHeight = rawElement(content_image).height;
                    var xDiff = scrollDiff * iWidth / 2;
                    var yDiff = scrollDiff * iHeight / 2;
                    var nextScrollLeft = content.scrollLeft() + xDiff;
                    var nextScrollTop = content.scrollTop() + yDiff;
                    var width = content.width();
                    var height = content.height();
                    var xLimit = iWidth * zoom;
                    var yLimit = iHeight * zoom;
                    if (nextScrollLeft + width > xLimit) {
                        nextScrollLeft = xLimit - width;
                    }
                    if (nextScrollTop + height > yLimit) {
                        nextScrollTop = yLimit - height;
                    }
                    content.scrollLeft(nextScrollLeft);
                    content.scrollTop(nextScrollTop);
                }
                return false;
            });
            dragscroll.reset();
        });
    }
}

function goToChapter(offset) {
    window.location.href = "manga_info.html?b=CLOSE&id=" + mangaId + "&lc=" + chapterId + "&lb=" + encodeURIComponent(backLink) + "&nco=" + offset;
}

function setupButtonManager() {
    console.log("Setting up button manager...");
    buttonManager = {};
    buttonManager.previousBtn = $("#rbtn_previous_page");
    buttonManager.nextBtn = $("#rbtn_next_page");
    //Do this before the buttons are switched so after they are switched the keyboard buttons work properly
    $(document).keydown(function (nextBtn, previousBtn) {
        return function (e) {
            //Right or space will go to next page
            if (e.key === "ArrowRight" || (!rightToLeft && (e.keyCode === 0 || e.keyCode === 32))) {
                nextBtn.click();
            } else if (e.key === "ArrowLeft" || (rightToLeft && (e.keyCode === 0 || e.keyCode === 32))) {
                previousBtn.click();
            }
        };
    }(buttonManager.nextBtn, buttonManager.previousBtn));
    //Swap buttons if left to right
    if (rightToLeft) {
        [buttonManager.previousBtn, buttonManager.nextBtn] = [buttonManager.nextBtn, buttonManager.previousBtn];
    }
    buttonManager.backBtn = $("#back_button");
    buttonManager.fullscreenBtn = $("#fullscreen_button");
    buttonManager.rotateBtn = $("#rotate_button");
    buttonManager.downloadBtn = $("#download_button");
    buttonManager.redownloadBtn = $("#redownload_button");
    buttonManager.lock = false;
    buttonManager.nextBtn.click(function () {
        //Close featherlight
        $(".featherlight-close").click();
        if (hasNextPage()) {
            imageManager.goToImage(parseInt(currentPage) + 1, null, true);
            updateButtons();
        } else if (hasNextChapter) {
            goToChapter(1);
        }
    });
    buttonManager.previousBtn.click(function () {
        //Close featherlight
        $(".featherlight-close").click();
        if (hasPrevPage()) {
            imageManager.goToImage(parseInt(currentPage) - 1, null, true);
            updateButtons();
        } else if (hasPrevChapter) {
            goToChapter(-1);
        }
    });
    buttonManager.backBtn.click(function () {
        if (valid(backLink)) {
            window.location.href = backLink;
        } else {
            window.history.back();
        }
    });
    buttonManager.fullscreenBtn.click(function () {
        toggleFullScreen();
    });
    buttonManager.rotateBtn.click(function () {
        rotateImage();
    });
    buttonManager.downloadBtn.click(function () {
        var cached = cachedPages[currentPage];
        if (valid(cached)) {
            var type = cached.blob.type;
            var extension = type.substr(type.indexOf("/") + 1);
            saveAs(cached.blob, currentPage + "." + extension);
        }
    });
    buttonManager.redownloadBtn.click(function () {
        refreshCurrentPage();
    });
    //Catch shift keys for changing the buttons around
    $(window).keydown(function (e) {
        if (e.keyCode == 16) {
            showExtraButtons();
        }
    });
    $(window).keyup(function (e) {
        if (e.keyCode == 16) {
            hideExtraButtons();
        }
    });
}
function showExtraButtons() {
    buttonManager.downloadBtn.css("display", "none");
    buttonManager.redownloadBtn.css("display", "initial");
}
function hideExtraButtons() {
    buttonManager.downloadBtn.css("display", "initial");
    buttonManager.redownloadBtn.css("display", "none");
}
function refreshCurrentPage() {
    var parsedCurrentPage = parseInt(currentPage);
    jqueryPageElement(parsedCurrentPage).data(loaded, false);
    jqueryPageElement(parsedCurrentPage).css("background-image", "");
    jqueryPageElement(parsedCurrentPage).css("background-size", "");
    cachedPages[parsedCurrentPage] = null;
    tryLoad(jqueryPageElement(parsedCurrentPage), parsedCurrentPage);
}
function setupHudManager() {
    console.log("Setting up HUD manager...");
    hudManager = {};
    hudManager.pageText = $("#page_indicator");
    hudManager.pageSlider = $("#page_slider");
    hudManager.pageSlider.attr("min", 1);
    hudManager.pageSlider.attr("max", maxPages);
    hudManager.pageSlider.on("input change", function () {
        imageManager.goToImage(hudManager.pageSlider[0].value - 1, null, true);
        updateButtons();
    });
    //Rotate slider if RTL
    if (rightToLeft) {
        $("#page_slider_wrapper").css("transform", "rotate(180deg)");
    }
    componentHandler.upgradeElement(hudManager.pageSlider[0]);
    updateHudPageNumber();
}
function updateHudPageNumber() {
    setHudPageNumber(parseInt(currentPage) + 1);
}
function setHudPageNumber(page) {
    hudManager.pageText.text("Page " + page + "/" + maxPages);
    hudManager.pageSlider[0].MaterialSlider.change(page);
}
function rotateImage() {
    imageManager.rotation++;
    if (imageManager.rotation >= 4) {
        imageManager.rotation = 0;
    }
    updateRotation();
}
function updateRotation() {
    for (var i = 0; i < imageManager.readerElements.length; i++) {
        applyRotation(imageManager.readerElements[i]);
    }
}
function getRotationCSS() {
    return "rotate(" + (imageManager.rotation * 90) + "deg)";
}
function applyRotation(image) {
    image.css("transform", getRotationCSS());
    //Reverse dimensions
    if (imageManager.rotation % 2) {
        image.css("width", $(document).height() + "px");
    } else {
        image.css("width", "");
    }
}
function toggleFullScreen() {
    if ((document.fullScreenElement && document.fullScreenElement !== null) ||
        (!document.mozFullScreen && !document.webkitIsFullScreen)) {
        enterFullscreen();
    } else {
        leaveFullscreen();
    }
}
function leaveFullscreen() {
    buttonManager.fullscreenBtn.find(".material-icons").text("fullscreen");
    if (document.cancelFullScreen) {
        document.cancelFullScreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.webkitCancelFullScreen) {
        document.webkitCancelFullScreen();
    }
}
function enterFullscreen() {
    buttonManager.fullscreenBtn.find(".material-icons").text("fullscreen_exit");
    if (document.documentElement.requestFullScreen) {
        document.documentElement.requestFullScreen();
    } else if (document.documentElement.mozRequestFullScreen) {
        document.documentElement.mozRequestFullScreen();
    } else if (document.documentElement.webkitRequestFullScreen) {
        document.documentElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
    }
}
function setImageSource(image, src) {
    image.css("background-image", "url(\"" + src + "\")");
    image.data("src", src);
    properlyScaleImage(image);
}
function properlyScaleImage(image) {
    var src = image.data("src");
    var img = new Image;
    img.onload = function () {
        if (img.width < image.width() && img.height < image.height()) {
            image.css("background-size", "auto auto");
        } else {
            image.css("background-size", "contain");
        }
    };
    img.src = src;
}
//Rescale images on window resize
$(window).resize(function () {
    for (var i = 0; i < maxPages; i++) {
        var image = imageManager.readerElements[i];
        if (image.data(loaded)) {
            properlyScaleImage(image);
            applyRotation(image);
        }
    }
});
var cachedPages = {};
function tryLoad(image, page) {
    if (page >= 0 && page < maxPages && (!image.data(loaded))) {
        if (valid(cachedPages[page])) {
            setImageSource(image, cachedPages[page].url);
            image.data(loaded, true);
            activateZoom(image);
            loadNextImage();
            return;
        }
        var xhr = new XMLHttpRequest();
        xhr.open('GET', TWApi.Commands.Image.buildUrl({
            mangaId: mangaId,
            chapterId: chapterId,
            page: page
        }), true);
        xhr.responseType = 'blob';

        xhr.onload = function () {
            if (this.status !== 200) {
                console.log("Got image with bad status code: " + this.status + "!");
            } else {
                var url = URL.createObjectURL(xhr.response);
                cachedPages[page] = {url: url, blob: xhr.response};
                setImageSource(image, cachedPages[page].url);
                image.data(loaded, true);
                activateZoom(image);
            }
            loadNextImage();
        };
        xhr.onerror = function (e) {
            console.error("Error loading image: " + xhr.statusText, e);
            loadNextImage();
        };
        xhr.send();
    } else {
        loadNextImage();
    }
}
function jqueryPageElement(page) {
    return imageManager.readerElements[page];
}
function loadNextImage() {
    function findNotLoadedPage(from, to) {
        function shouldLoad(page) {
            return !jqueryPageElement(page).data(loaded);
        }

        var i;
        if (to > from) {
            for (i = Math.max(from + 1, 0); i <= Math.min(to, maxPages - 1); i++) {
                if (shouldLoad(i)) {
                    return i;
                }
            }
        } else {
            for (i = Math.min(from - 1, maxPages - 1); i >= Math.max(to, 0); i--) {
                if (shouldLoad(i)) {
                    return i;
                }
            }
        }
        return null;
    }

    var nextPageToLoad = null;
    if (!jqueryPageElement(parseInt(currentPage)).data(loaded)) {
        nextPageToLoad = parseInt(currentPage);
    }
    //TODO Make these configurable???
    //Search three pages ahead, then one behind, then three ahead, then one behind and so on...
    for (var i = 1; i <= maxPages; i++) {
        if (!valid(nextPageToLoad)) {
            //Check next 3 pages
            nextPageToLoad = findNotLoadedPage(parseInt(currentPage), parseInt(currentPage) + (i * 3));
        } else {
            break;
        }
        if (!valid(nextPageToLoad)) {
            //Check previous page
            nextPageToLoad = findNotLoadedPage(parseInt(currentPage), parseInt(currentPage) - i);
        } else {
            break;
        }
    }
    //Load the image if there is one to load!
    if (valid(nextPageToLoad)) {
        console.log("Loading page: " + nextPageToLoad);
        tryLoad(jqueryPageElement(nextPageToLoad), nextPageToLoad);
    } else {
        console.log("No more pages to load!");
    }
}
function animate(time) {
    requestAnimationFrame(animate);
    //Keeps Tween updated
    TWEEN.update(time);
}