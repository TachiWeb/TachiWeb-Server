var snackbar;

var fadeSpeed = 250;
var QueryString = function () {
    // This function is anonymous, is executed immediately and
    // the return value is assigned to QueryString!
    var query_string = {};
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        // If first entry with this name
        if (typeof query_string[pair[0]] === "undefined") {
            query_string[pair[0]] = decodeURIComponent(pair[1]);
            // If second entry with this name
        } else if (typeof query_string[pair[0]] === "string") {
            query_string[pair[0]] = [query_string[pair[0]], decodeURIComponent(pair[1])];
        } else {
            query_string[pair[0]].push(decodeURIComponent(pair[1]));
        }
    }
    return query_string;
}();

//Remove all children from element
function clearElement(myNode) {
    while (rawElement(myNode).firstChild) {
        rawElement(myNode).removeChild(rawElement(myNode).firstChild);
    }
}

function rawElement(element) {
    if (element instanceof jQuery) {
        return element[0];
    } else {
        return element;
    }
}

function elementExists(element) {
    return !!rawElement(element);
}

function valid(v) {
    return v !== undefined && v !== null;
}

function mdlCheckboxCheck(checkbox, check) {
    //For some stupid reason, we need the parent label to change the checkbox!
    if (check) {
        rawElement(checkbox.parent()).MaterialCheckbox.check();
    } else {
        rawElement(checkbox.parent()).MaterialCheckbox.uncheck();
    }
}
function mdlRadioCheck(radio, check) {
    //For some stupid reason, we need the parent label to change the checkbox!
    if (check) {
        rawElement(radio.parent()).MaterialRadio.check();
    } else {
        rawElement(radio.parent()).MaterialRadio.uncheck();
    }
}

function openInNewTab(url) {
    var win = window.open(url, '_blank');
    win.focus();
}

/**
 * Shift elements in an array
 * @param arr The array to move the element in
 * @param fromIndex The index of the object to move
 * @param toIndex The target index to move the object to
 */
function arraymove(arr, fromIndex, toIndex) {
    let element = arr[fromIndex];
    arr.splice(fromIndex, 1);
    arr.splice(toIndex, 0, element);
}

//Catch onload
window.onload = function () {
    //Setup snackbar
    snackbar = rawElement($("#snackbar"));
    if (elementExists(snackbar)) {
        snackbar = snackbar.MaterialSnackbar;
    }
    //Call other onload listeners
    if (valid(onLoad)) {
        onLoad();
    }
};
