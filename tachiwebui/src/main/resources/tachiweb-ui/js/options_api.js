// Client side options API
var OptionsApi = {};
(function () {
    var optionsReadyHandlers = [];
    var optionsReady = false;

    TWApi.Commands.GetPrefs.execute(function (resp) {
        var i;
        //Insert prefs into OptionsAPI
        var options = resp.prefs;
        var keys = Object.keys(options);
        for (i = 0; i < keys.length; i++) {
            var key = keys[i];
            OptionsApi[key] = options[key].v;
        }
        //Invoke ready handlers
        for (i = 0; i < optionsReadyHandlers.length; i++) {
            optionsReadyHandlers[i](OptionsApi);
        }
        optionsReady = true;
    });

    OptionsApi.onReady = function (handler) {
        //Invoke handler if options are already ready
        if (optionsReady) {
            handler(OptionsApi);
        } else {
            optionsReadyHandlers.push(handler);
        }
    };

    //Defaults
    OptionsApi.pref_default_viewer_key = "left_to_right";
    OptionsApi.pref_reader_theme_key = "white";
    OptionsApi.pref_reader_enable_transitions = true;
    OptionsApi.pref_manga_card_label_background = "gradient";
})();