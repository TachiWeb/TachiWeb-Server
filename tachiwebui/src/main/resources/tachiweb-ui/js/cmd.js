/** Cross-page browser command API **/
var intercom = Intercom.getInstance();

var BrowserCommand = {
    init: function () {
        var that = this;

        function DefaultBrowserCommand(commandName) {
            that[commandName] = {
                name: commandName,
                send: function (data) {
                    intercom.emit("BrowserCommand", {
                        command: commandName,
                        data: data
                    });
                },
                listeners: [],
                on: function (callback) {
                    this.listeners.push(callback);
                }
            };
        }

        new DefaultBrowserCommand("Favorite");

        return this;
    }
}.init();

intercom.on("BrowserCommand", function (data) {
    var command = BrowserCommand[data.command];
    if (!command) {
        console.warn("Invalid browser command received!", data);
        return;
    }
    for (var i = 0; i < command.listeners.length; i++) {
        var listener = command.listeners[i];
        if (listener) {
            listener(data.data);
        }
    }
});