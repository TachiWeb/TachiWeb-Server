var LigFill = {};
(function() {
    let fetchedCps = [];
    let cp = {};
    let bound = [];

    let parseCodePoints = function(data) {
        data.split('\n').map(x => x.split(' ')).forEach(function(pair) {
            cp[pair[0]] = String.fromCharCode(parseInt(pair[1], 16));
        });
    };

    LigFill.update = function() {
        bound.forEach(function(selector) {
            let elements = document.querySelectorAll(selector);
            for(let i = 0; i < elements.length; i++) {
                let element = elements[i];
                let key = element.textContent.trim();
                let value = cp[key];

                if(value != null) {
                    element.textContent = value;
                }
            }
        });
    };

    LigFill.bind = function(...selectors) {
        selectors.forEach(function(it) {
            if(bound.indexOf(it) < 0)
                bound.push(it);
        });
        LigFill.update();
    };

    LigFill.addCodepoints = function(codepoints) {
        //Fetch codepoints
        if(fetchedCps.indexOf(codepoints) < 0) {
            fetchedCps.push(codepoints);
            let request = new XMLHttpRequest();
            request.addEventListener("load", function () {
                parseCodePoints(request.responseText);
                LigFill.update();
            });
            request.open("GET", codepoints);
            request.send();
        } else {
            LigFill.update();
        }
    };
})();