/*
 * Simple MDL settings page generator
 */
var Options = {};
(function () {
    const PREFS_URL = "prefs/schema.json";
    const OPTION_ELEMENT_CLASSNAME = "option-list";

    var errorHandlers = [];
    var onError = function (error) {
        for (var i = 0; i < errorHandlers.length; i++) {
            errorHandlers[i](error);
        }
    };

    //Generators for each type of settings
    var optionElementGenerators = {};

    var optionsData = null; //Local copy of preference states
    var optionsSchema = null; //UI layout (stack)
    var optionsTitles = ["Settings"]; //Header titles (stack)

    var domInitialized = false; //For initializing components that cannot be initialized with the DOM being fully loaded

    var optionsElements = null; //Elements that the options menu is attached to (can be attached to multiple elements to mirror user actions across elements)
    var ensureInitialized = function () {
        if (optionsElements === null) {
            throw "Options not initialized (ensure Options.registerJqueryElement was called)!";
        }
    };

    //Update title and back button
    var updateHeader = function () {
        //Update back button
        var drawerButton = $(".mdl-layout__drawer-button");
        var backButton = $(".back-button");
        if (optionsSchema.length === null || optionsSchema.length <= 1) {
            drawerButton.show();
            backButton.hide();
        } else {
            drawerButton.hide();
            backButton.show();
        }
        //Update title
        $("#title").text(Options.getCurrentTitle());
    };

    //Initialize components that require the DOM to be fully loaded (only called once)
    var initialSetup = function () {
        //Back button
        $(".back-button").click(function () {
            Options.popSchema();
        });
        //Dialog polyfills
        var dialog = Options.getDialogElement();
        if (!dialog[0].showModal) {
            dialogPolyfill.registerDialog(dialog[0]);
        }
        //Do not initialize again
        domInitialized = true;
    };

    /**
     * Register an element as a holder for the settings menu
     * @param element The element to register
     */
    Options.registerJqueryElement = function (element) {
        if (!domInitialized) {
            initialSetup();
        }
        //Create options list and append to inside of element
        var optionElement = document.createElement("ul");
        optionElement.className = OPTION_ELEMENT_CLASSNAME + " mdl-list";
        componentHandler.upgradeElement(optionElement);
        optionElement = $(optionElement);
        if (optionsElements === null) {
            optionsElements = optionElement;
        } else {
            optionsElements.add(optionElement);
        }
        optionElement.appendTo(element);
        Options.updateUI();
    };

    /**
     * Fetch the latest preference states form the server
     */
    Options.refreshData = function () {
        TWApi.Commands.GetPrefs.execute(function (resp) {
            optionsData = resp.prefs;
            Options.updateUI();
        }, onError);
    };

    /**
     * Fetch the UI layout from the server
     */
    Options.refreshSchema = function () {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", PREFS_URL, true);
        xhr.onload = function () {
            optionsSchema = [JSON.parse(xhr.responseText)];
            Options.updateUI();
        };
        xhr.onerror = function () {
            onError();
        };
        xhr.send();
    };

    //Render settings menu
    Options.updateUI = function () {
        //Make sure we have at least one element registered to hold the menu
        ensureInitialized();
        //Make sure we have all the required data before proceeding
        if (optionsData === null) {
            Options.refreshData();
            return;
        } else if (optionsSchema === null || optionsSchema.length <= 0) {
            Options.refreshSchema();
            return;
        }
        //Clear old render state
        optionsElements.empty();
        //Get UI layout at top of stack
        var currentSchema = Options.getCurrentSchema();
        for (var i = 0; i < currentSchema.length; i++) {
            var optionSchema = currentSchema[i];
            //Determine current value of this setting
            var value = undefined;
            if (optionSchema.key !== null && optionSchema.key !== undefined) {
                var optionsDataElement = optionsData[optionSchema.key];
                if (optionsDataElement !== null && optionsDataElement !== undefined) {
                    value = optionsDataElement.v;
                }
            }
            //Fallback to default if the value of this setting was not specified
            if (value === undefined || value === null) {
                value = optionSchema.default;
            }
            //Render element and append to UI element list
            resolveOptionsElement(optionSchema.type, optionSchema, value).appendTo(optionsElements);
        }
        updateHeader();
    };

    //Save data changes (and revert changes if the save failed)
    Options.saveDataChange = function (element, schemaEntry, oldValue, newValue) {
        var generator = getElementGenerator(element);
        TWApi.Commands.SetPref.execute(function () {
            //Save changes to local data structure if it was successfully saved to the remote data structure
            var prefObj = optionsData[schemaEntry.key];
            if (prefObj === null || prefObj === undefined) {
                prefObj = {};
                optionsData[schemaEntry.key] = prefObj;
            }
            prefObj.v = newValue;
        }, function () {
            //Error saving changes, revert them on our side
            onError();
            generator.setValue(element, oldValue);
        }, {
            key: schemaEntry.key,
            type: generator.type,
            value: newValue
        });
    };

    /**
     * Must return a jQuery element!
     *
     * Get the generator responsible for generating the element for this type of setting
     */
    var resolveOptionsElement = function (type, schema, value) {
        var generator = optionElementGenerators[type];
        if (generator === null) {
            console.error("No generator found for this element!");
            return null;
        }
        var element = generator.generate(schema);
        if (value !== null && value !== undefined && generator.setValue) {
            generator.setValue(element, value);
        }
        element.data("generator", generator);
        return element;
    };

    /**
     * Get the generator of an element
     * @param element The element
     */
    var getElementGenerator = function (element) {
        return element.data("generator");
    };

    /**
     * Register an error handler
     * @param handler The error handler to register
     */
    Options.registerErrorHandler = function (handler) {
        //Do not register the same handler twice
        if (errorHandlers.indexOf(handler) === -1) {
            errorHandlers.push(handler);
        }
    };

    /**
     * Register an element generator
     * @param name The type of setting this generator can generate elements for
     * @param generator The generator
     */
    Options.registerOptionsElement = function (name, generator) {
        optionElementGenerators[name] = generator;
    };

    /**
     * Get the UI layout at the top of the stack
     */
    Options.getCurrentSchema = function () {
        return optionsSchema[optionsSchema.length - 1];
    };

    /**
     * Get the title at the top of the stack
     * @returns {string}
     */
    Options.getCurrentTitle = function () {
        return optionsTitles[optionsTitles.length - 1];
    };

    /**
     * Push a new UI layout to the top of the stack (enter submenu)
     * @param newSchema The new UI layout to display
     * @param title The title of the new UI layout
     */
    Options.pushSchema = function (newSchema, title) {
        optionsSchema.push(newSchema);
        optionsTitles.push(title);
        Options.updateUI();
    };

    /**
     * Remove the topmost UI layout from the top of the stack (exiting submenu)
     */
    Options.popSchema = function () {
        var newSchema = optionsSchema.pop();
        optionsTitles.pop();
        Options.updateUI();
        return newSchema;
    };

    /**
     * Utility method for generating a simple no-op setting element
     * @param text The title of this setting element
     * @param desc The description of this setting element (optional)
     * @returns {{listItem: Element, title: Element, primaryContent: Element, secondaryContent: Element}}
     */
    Options.generateBaseItem = function (text, desc) {
        var listItem = document.createElement("li");
        listItem.className = "mdl-list__item preference-item";
        var primaryContent = document.createElement("span");
        primaryContent.className = "mdl-list__item-primary-content";
        var title = document.createElement("span");
        title.textContent = text;
        primaryContent.appendChild(title);
        if (desc !== "" && desc !== null && desc !== undefined && desc.trim() !== "") {
            listItem.classList.add("mdl-list__item--two-line");
            var description = document.createElement("span");
            description.className = "mdl-list__item-sub-title";
            description.textContent = desc;
            primaryContent.appendChild(description);
        }
        listItem.appendChild(primaryContent);
        var secondaryContent = document.createElement("span");
        secondaryContent.className = "mdl-list__item-secondary-action";
        listItem.appendChild(secondaryContent);
        return {
            listItem: listItem,
            title: title,
            primaryContent: primaryContent,
            secondaryContent: secondaryContent
        };
    };

    /**
     * Get the dialog object.
     */
    Options.getDialogElement = function () {
        return $("#dialog");
    };
})();
(function () {

    /**
     * Generate an icon button
     * @param icon The icon of this button
     * @param onclick The action to execute on click
     * @returns {Element}
     */
    var generateBaseButton = function (icon, onclick) {
        var button = document.createElement("button");
        button.className = "mdl-button mdl-js-button mdl-button--icon mdl-button--colored";
        button.onclick = onclick;
        var iconElement = document.createElement("i");
        iconElement.className = "material-icons";
        iconElement.textContent = icon;
        button.appendChild(iconElement);
        return button;
    };

    //Switches and checkboxes (toggles)
    (function () {

        //Toggle types
        const TYPE_CHECKBOX = 1;
        const TYPE_SWITCH = 2;

        /**
         * Generate a basic toggle
         * @param schema The metadata for this toggle
         * @param type The type of toggle (TYPE_CHECKBOX/TYPE_SWITCH)
         * @returns {jQuery|HTMLElement}
         */
        var generateBaseToggle = function (schema, type) {
            var baseItem = Options.generateBaseItem(schema.label, schema.description);
            var element = $(baseItem.listItem);
            var checkboxId = schema.key;
            var label = document.createElement("label");
            label.classList.add("mdl-js-ripple-effect");
            label.setAttribute("for", checkboxId);
            var toggle = document.createElement("input");
            toggle.setAttribute("type", "checkbox");
            toggle.id = checkboxId;
            toggle.onchange = function () {
                Options.saveDataChange(element, schema, !toggle.checked, toggle.checked);
            };
            label.appendChild(toggle);
            baseItem.secondaryContent.appendChild(label);
            if (type === TYPE_CHECKBOX) {
                label.className = "mdl-checkbox mdl-js-checkbox";
                toggle.className = "mdl-checkbox__input";
            } else if (type === TYPE_SWITCH) {
                label.className = "mdl-switch mdl-js-switch";
                toggle.className = "mdl-switch__input";
            } else {
                throw "Invalid type!";
            }
            componentHandler.upgradeElement(baseItem.listItem);
            componentHandler.upgradeElement(label);
            return element;
        };

        Options.registerOptionsElement("checkbox", {
            generate: function (schema) {
                return generateBaseToggle(schema, TYPE_CHECKBOX);
            },
            setValue: function (element, value) {
                var obj = element.find("label")[0].MaterialCheckbox;
                if (value) {
                    obj.check();
                } else {
                    obj.uncheck();
                }
            },
            type: "boolean"
        });

        Options.registerOptionsElement("switch", {
            generate: function (schema) {
                return generateBaseToggle(schema, TYPE_SWITCH);
            },
            setValue: function (element, value) {
                var obj = element.find("label")[0].MaterialSwitch;
                if (value) {
                    obj.on();
                } else {
                    obj.off();
                }
            },
            type: "boolean"
        });
    })();

    //Multiselect, text
    (function () {

        //Textfield types
        const TYPE_TEXT = 1;
        const TYPE_PASSWORD = 2;
        const TYPE_MULTILINE = 3;

        /**
         * Show the dialog
         */
        var showDialog = function () {
            var dialog = Options.getDialogElement()[0];
            componentHandler.upgradeElement(dialog);
            dialog.showModal();
        };

        /**
         * Close the dialog
         */
        var closeDialog = function () {
            Options.getDialogElement()[0].close();
        };

        /**
         * Get the dialog actions
         * @returns {jQuery|HTMLElement}
         */
        var getDialogActions = function () {
            return $("#dialog_actions");
        };

        /**
         * Append a button to the dialog
         * @param label The label of the button
         * @param onclick The action to perform when the button is clicked
         */
        var appendDialogButton = function (label, onclick) {
            var button = document.createElement("button");
            button.classList = "mdl-button mdl-js-button mdl-js-ripple-effect";
            button.setAttribute("type", "button");
            button.onclick = onclick;
            button.textContent = label;
            getDialogActions()[0].appendChild(button);
        };

        /**
         * Remove all buttons from the dialog
         */
        var clearDialogButtons = function () {
            getDialogActions().empty();
        };

        /**
         * Set the title of the dialog
         * @param title The new title of the dialog
         */
        var setDialogTitle = function (title) {
            $("#dialog_title").text(title);
        };

        /**
         * Get the content of the dialog
         * @returns {jQuery|HTMLElement}
         */
        var getDialogContent = function () {
            return $("#dialog_content");
        };

        /**
         * Remove all content from the dialog
         */
        var clearDialogContent = function () {
            getDialogContent().empty();
        };

        /**
         * Append a radio button to the dialog
         * @param id The id of the new radio button
         * @param label The label of the new radio button
         * @param onselect The action to perform when the radio button is selected
         * @param checked Whether or not the radio button should be checked
         * @returns {Element}
         */
        var appendRadioButton = function (id, label, onselect, checked) {
            var parent = document.createElement("label");
            parent.className = "mdl-radio mdl-js-radio mdl-js-ripple-effect";
            parent.setAttribute("for", id);
            var radio = document.createElement("input");
            radio.setAttribute("type", "radio");
            radio.id = id;
            radio.className = "mdl-radio__button";
            radio.name = "select";
            radio.onchange = function () {
                onselect(id);
            };
            radio.checked = checked;
            var labelElement = document.createElement("span");
            labelElement.className = "mdl-radio__label";
            labelElement.textContent = label;
            parent.appendChild(radio);
            parent.appendChild(labelElement);
            getDialogContent()[0].appendChild(parent);
            componentHandler.upgradeElement(parent);
            return parent;
        };

        var appendTextField = function (id, hint, initialValue, type, onchanged, rows) {
            var parent = document.createElement("div");
            parent.className = "mdl-textfield mdl-js-textfield";
            var input;
            if (type !== TYPE_MULTILINE) {
                input = document.createElement("input");
                if (type === TYPE_TEXT) {
                    input.setAttribute("type", "text");
                } else if (type === TYPE_PASSWORD) {
                    input.setAttribute("type", "password");
                }
                input.value = initialValue;
            } else {
                input = document.createElement("textarea");
                input.setAttribute("type", "text");
                input.setAttribute("rows", rows);
                input.textContent = initialValue;
            }
            input.className = "mdl-textfield__input";
            input.id = id;
            input.onchange = function () {
                onchanged(input.value);
            };
            parent.appendChild(input);
            var label = document.createElement("label");
            label.className = "mdl-textfield__label";
            label.setAttribute("for", id);
            label.textContent = hint;
            parent.appendChild(label);
            getDialogContent()[0].appendChild(parent);
            componentHandler.upgradeElement(parent);
            return parent;
        };

        Options.registerOptionsElement("select-single", {
            generate: function (schema) {
                var that = this;
                var baseItem = Options.generateBaseItem(schema.label, schema.description);
                var element = $(baseItem.listItem);
                var button = generateBaseButton("mode_edit", function () {
                    //Title
                    setDialogTitle(schema.label);
                    //Close button
                    clearDialogButtons();
                    var listenerMap = {};
                    appendDialogButton("Close", function () {
                        element.data("listener", null);
                        listenerMap = null;
                        closeDialog();
                    });
                    //Content
                    clearDialogContent();
                    //Append radio buttons
                    var choices = Object.keys(schema.choices);
                    for (var a = 0; a < choices.length; a++) {
                        var key = choices[a];
                        var choice = schema.choices[key];
                        listenerMap[key] = appendRadioButton(key, choice, function (newValue) {
                                var oldValue = element.data("curvalue");
                                that.setValue(element, newValue);
                                Options.saveDataChange(element, schema, oldValue, newValue);
                            },
                            //If the currently selected value equals this radio button, check this radio button
                            element.data("curvalue") === key);
                    }
                    //Allows setValue to operate when dialog is open
                    element.data("listener", function (id) {
                        var keys = Object.keys(listenerMap);
                        for (var i = 0; i < keys.length; i++) {
                            listenerMap[keys[i]].MaterialRadio.uncheck();
                        }
                        listenerMap[id].MaterialRadio.check();
                    });
                    //Show dialog
                    showDialog();
                });
                baseItem.secondaryContent.appendChild(button);
                return element;
            },
            setValue: function (element, value) {
                element.data("curvalue", value);
                //Call listener (to update UI if the dialog is still open)
                var listener = element.data("listener");
                if (listener !== undefined && listener !== null) {
                    listener(value);
                }
            },
            type: "string"
        });

        var registerText = function (name, type) {
            Options.registerOptionsElement(name, {
                generate: function (schema) {
                    var that = this;
                    var baseItem = Options.generateBaseItem(schema.label, schema.description);
                    var element = $(baseItem.listItem);
                    var button = generateBaseButton("mode_edit", function () {
                        //Title
                        setDialogTitle(schema.label);
                        //Close button
                        clearDialogButtons();
                        var listener = {};
                        appendDialogButton("Close", function () {
                            element.data("listener", null);
                            listener = null;
                            closeDialog();
                        });
                        //Content
                        clearDialogContent();
                        //Append textfield
                        listener = appendTextField(schema.key, schema.hint, element.data("curvalue"), type, function (newValue) {
                            var oldValue = element.data("curvalue");
                            that.setValue(element, newValue);
                            Options.saveDataChange(element, schema, oldValue, newValue);
                        }, schema.rows);
                        //Allows setValue to operate when dialog is open
                        element.data("listener", function (newValue) {
                            listener.MaterialTextfield.change(newValue);
                        });
                        //Show dialog
                        showDialog();
                    });
                    baseItem.secondaryContent.appendChild(button);
                    return element;
                },
                setValue: function (element, value) {
                    element.data("curvalue", value);
                    //Call listener (to update UI if the dialog is still open)
                    var listener = element.data("listener");
                    if (listener !== undefined && listener !== null) {
                        listener(value);
                    }
                },
                type: "string"
            });
        };

        registerText("text-single", TYPE_TEXT);
        registerText("text-password", TYPE_PASSWORD);
        registerText("text-multi", TYPE_MULTILINE);
    })();

    //Nested preferences support
    Options.registerOptionsElement("nested", {
        generate: function (schema) {
            var baseItem = Options.generateBaseItem(schema.label, schema.description);
            var button = generateBaseButton("chevron_right", function () {
                Options.pushSchema(schema.prefs, schema.label);
            });
            baseItem.secondaryContent.appendChild(button);
            return $(baseItem.listItem);
        }
    });

    //API call support
    Options.registerOptionsElement("api-call", {
        generate: function (schema) {
            var baseItem = Options.generateBaseItem(schema.label, schema.description);
            var button = generateBaseButton(schema.button_icon, function () {
                TWApi.Commands[schema.command].execute(function () {
                    if (valid(schema.success_message)) {
                        snackbar.showSnackbar({
                            message: schema.success_message,
                            timeout: 1000
                        });
                    }
                }, function () {
                    if (valid(schema.error_message)) {
                        snackbar.showSnackbar({
                            message: schema.error_message,
                            timeout: 1000
                        });
                    }
                }, schema.parameters);
            });
            baseItem.secondaryContent.appendChild(button);
            return $(baseItem.listItem);
        }
    });
})();

//Setup options
var optionsBody;
function onLoad() {
    optionsBody = $("#options_body");

    Options.registerErrorHandler(function () {
        snackbar.showSnackbar({
            message: "Error connecting to server!",
            timeout: 2000
        });
    });
    Options.registerJqueryElement(optionsBody);
}