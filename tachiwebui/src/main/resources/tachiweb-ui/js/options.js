/*
 * Simple MDL settings page generator
 */
let Options = {};
(function () {
    const PREFS_URL = "prefs/schema.json";
    const OPTION_ELEMENT_CLASSNAME = "option-list";

    let errorHandlers = [];
    let onError = function (error) {
        for (let i = 0; i < errorHandlers.length; i++) {
            errorHandlers[i](error);
        }
    };

    //Generators for each type of settings
    let optionElementGenerators = {};

    let optionsData = null; //Local copy of preference states
    let optionsSchema = null; //UI layout (stack)
    let optionsTitles = ["Settings"]; //Header titles (stack)

    let domInitialized = false; //For initializing components that cannot be initialized with the DOM being fully loaded

    let optionsElements = null; //Elements that the options menu is attached to (can be attached to multiple elements to mirror user actions across elements)
    let ensureInitialized = function () {
        if (optionsElements === null) {
            throw "Options not initialized (ensure Options.registerJqueryElement was called)!";
        }
    };

    //Update title and back button
    let updateHeader = function () {
        //Update back button
        let drawerButton = $(".mdl-layout__drawer-button");
        let backButton = $(".back-button");
        if (optionsTitles.length === null || optionsTitles.length <= 1) {
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
    let initialSetup = function () {
        //Back button
        $(".back-button").click(function () {
            Options.popSchema();
        });
        //Dialog polyfills
        let dialog = Options.getDialogElement();
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
        let optionElement = document.createElement("ul");
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
        const xhr = new XMLHttpRequest();
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
        const currentSchema = Options.getCurrentSchema();
        for (let i = 0; i < currentSchema.length; i++) {
            const optionSchema = currentSchema[i];
            //Determine current value of this setting
            let value = undefined;
            if (optionSchema.key !== null && optionSchema.key !== undefined) {
                //Support preferences mapping to multiple preference keys
                if(Array.isArray(optionSchema.key)) {
                    value = {};
                    optionSchema.key.forEach(function(t) {
                        const optionsDataElement = optionsData[t];
                        if (optionsDataElement !== null && optionsDataElement !== undefined) {
                            value[t] = optionsDataElement.v;
                        } else {
                            value[t] = optionSchema.default[t];
                        }
                        //Deep clone all arrays passed to preference
                        if(value[t] != null && Array.isArray(value[t])) {
                            value[t] = value[t].slice(0);
                        }
                    });
                } else {
                    const optionsDataElement = optionsData[optionSchema.key];
                    if (optionsDataElement !== null && optionsDataElement !== undefined) {
                        value = optionsDataElement.v;
                    }
                }
            }
            //Fallback to default if the value of this setting was not specified
            if (value === undefined || value === null) {
                value = optionSchema.default;
            }
            //Deep clone all arrays passed to preference
            if(value != null && Array.isArray(value)) {
                value = value.slice(0);
            }
            //Render element and append to UI element list
            resolveOptionsElement(optionSchema.type, optionSchema, value).appendTo(optionsElements);
        }
        updateHeader();
    };

    //Save data changes (and revert changes if the save failed)
    Options.saveDataChange = function (element, schemaEntry, oldValue, newValue) {
        const generator = getElementGenerator(element);

        let localNewValue;
        let localOldValue;
        let multipleKeyMapping = Array.isArray(schemaEntry.key);
        const saveQueue = [];
        if(multipleKeyMapping) {
            schemaEntry.key.forEach(function(t) {
                saveQueue.push(t);
            });
            // Shallow clone new value for reverting in the case of failure
            localOldValue = Object.assign({}, newValue);
            localNewValue = newValue;
        } else {
            saveQueue.push(schemaEntry.key);
            localNewValue = {};
            localNewValue[schemaEntry.key] = newValue;
        }

        function fail() {
            //Error saving changes, revert them on our side
            onError();
            //Update entries that have not been saved yet
            if(multipleKeyMapping) {
                saveQueue.forEach(function(t) {
                    localOldValue[t] = oldValue[t];
                });
            }
            generator.setValue(element, multipleKeyMapping ? localOldValue : oldValue);
        }

        function saveNext() {
            if(saveQueue.length > 0) {
                let key = saveQueue[saveQueue.length - 1];
                let type = multipleKeyMapping ? schemaEntry.pref_types[key] : generator.type;
                TWApi.Commands.SetPref.execute(function () {
                    //Save changes to local data structure if it was successfully saved to the remote data structure
                    let prefObj = optionsData[key];
                    if (prefObj === null || prefObj === undefined) {
                        prefObj = {};
                        optionsData[key] = prefObj;
                    }
                    prefObj.v = localNewValue[key];
                    //Set item as saved
                    saveQueue.pop();
                    //Save next item
                    saveNext();
                }, fail, {
                    key: key,
                    //Specify type in schema for multiple-key preferences
                    type: type,
                    value: localNewValue[key]
                });
            }
        }

        //Save first item
        saveNext();
    };

    /**
     * Must return a jQuery element!
     *
     * Get the generator responsible for generating the element for this type of setting
     */
    let resolveOptionsElement = function (type, schema, value) {
        const generator = optionElementGenerators[type];
        if (generator === null) {
            console.error("No generator found for this element!");
            return null;
        }
        const element = generator.generate(schema);
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
    let getElementGenerator = function (element) {
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
        const newSchema = optionsSchema.pop();
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
        const listItem = document.createElement("li");
        listItem.className = "mdl-list__item preference-item";
        const primaryContent = document.createElement("span");
        primaryContent.className = "mdl-list__item-primary-content";
        const title = document.createElement("span");
        title.textContent = text;
        primaryContent.appendChild(title);
        if (desc !== "" && desc !== null && desc !== undefined && desc.trim() !== "") {
            listItem.classList.add("mdl-list__item--two-line");
            const description = document.createElement("span");
            description.className = "mdl-list__item-sub-title";
            description.textContent = desc;
            primaryContent.appendChild(description);
        }
        listItem.appendChild(primaryContent);
        const secondaryContent = document.createElement("span");
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
    const generateBaseButton = function (icon, onclick) {
        const button = document.createElement("button");
        button.className = "mdl-button mdl-js-button mdl-button--icon mdl-button--colored";
        button.onclick = onclick;
        const iconElement = document.createElement("i");
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
        const generateBaseToggle = function (schema, type) {
            const baseItem = Options.generateBaseItem(schema.label, schema.description);
            const element = $(baseItem.listItem);
            const checkboxId = schema.key;
            const label = document.createElement("label");
            label.classList.add("mdl-js-ripple-effect");
            label.setAttribute("for", checkboxId);
            const toggle = document.createElement("input");
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
                const obj = element.find("label")[0].MaterialCheckbox;
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
                const obj = element.find("label")[0].MaterialSwitch;
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
        const showDialog = function () {
            const dialog = Options.getDialogElement()[0];
            componentHandler.upgradeElement(dialog);
            dialog.showModal();
        };

        /**
         * Close the dialog
         */
        const closeDialog = function () {
            Options.getDialogElement()[0].close();
        };

        /**
         * Get the dialog actions
         * @returns {jQuery|HTMLElement}
         */
        const getDialogActions = function () {
            return $("#dialog_actions");
        };

        /**
         * Append a button to the dialog
         * @param label The label of the button
         * @param onclick The action to perform when the button is clicked
         */
        const appendDialogButton = function (label, onclick) {
            const button = document.createElement("button");
            button.classList = "mdl-button mdl-js-button mdl-js-ripple-effect";
            button.setAttribute("type", "button");
            button.onclick = onclick;
            button.textContent = label;
            getDialogActions()[0].appendChild(button);
        };

        /**
         * Remove all buttons from the dialog
         */
        const clearDialogButtons = function () {
            getDialogActions().empty();
        };

        /**
         * Set the title of the dialog
         * @param title The new title of the dialog
         */
        const setDialogTitle = function (title) {
            $("#dialog_title").text(title);
        };

        /**
         * Get the content of the dialog
         * @returns {jQuery|HTMLElement}
         */
        const getDialogContent = function () {
            return $("#dialog_content");
        };

        /**
         * Remove all content from the dialog
         */
        const clearDialogContent = function () {
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
        const appendRadioButton = function (id, label, onselect, checked) {
            const parent = document.createElement("label");
            parent.className = "mdl-radio mdl-js-radio mdl-js-ripple-effect";
            parent.setAttribute("for", id);
            const radio = document.createElement("input");
            radio.setAttribute("type", "radio");
            radio.id = id;
            radio.className = "mdl-radio__button";
            radio.name = "select";
            radio.onchange = function () {
                onselect(id);
            };
            radio.checked = checked;
            const labelElement = document.createElement("span");
            labelElement.className = "mdl-radio__label";
            labelElement.textContent = label;
            parent.appendChild(radio);
            parent.appendChild(labelElement);
            getDialogContent()[0].appendChild(parent);
            componentHandler.upgradeElement(parent);
            return parent;
        };

        const appendTextField = function (id, hint, initialValue, type, onchanged, rows) {
            const parent = document.createElement("div");
            parent.className = "mdl-textfield mdl-js-textfield";
            let input;
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
            const label = document.createElement("label");
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
                const that = this;
                const baseItem = Options.generateBaseItem(schema.label, schema.description);
                const element = $(baseItem.listItem);
                const button = generateBaseButton("mode_edit", function () {
                    //Title
                    setDialogTitle(schema.label);
                    //Close button
                    clearDialogButtons();
                    let listenerMap = {};
                    appendDialogButton("Close", function () {
                        element.data("listener", null);
                        listenerMap = null;
                        closeDialog();
                    });
                    //Content
                    clearDialogContent();
                    //Append radio buttons
                    const choices = Object.keys(schema.choices);
                    for (let a = 0; a < choices.length; a++) {
                        const key = choices[a];
                        const choice = schema.choices[key];
                        listenerMap[key] = appendRadioButton(key, choice, function (newValue) {
                                const oldValue = element.data("curvalue");
                                that.setValue(element, newValue);
                                Options.saveDataChange(element, schema, oldValue, newValue);
                            },
                            //If the currently selected value equals this radio button, check this radio button
                            element.data("curvalue") === key);
                    }
                    //Allows setValue to operate when dialog is open
                    element.data("listener", function (id) {
                        const keys = Object.keys(listenerMap);
                        for (let i = 0; i < keys.length; i++) {
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
                const listener = element.data("listener");
                if (listener !== undefined && listener !== null) {
                    listener(value);
                }
            },
            type: "string"
        });

        const registerText = function (name, type) {
            Options.registerOptionsElement(name, {
                generate: function (schema) {
                    const that = this;
                    const baseItem = Options.generateBaseItem(schema.label, schema.description);
                    const element = $(baseItem.listItem);
                    const button = generateBaseButton("mode_edit", function () {
                        //Title
                        setDialogTitle(schema.label);
                        //Close button
                        clearDialogButtons();
                        let listener = {};
                        appendDialogButton("Close", function () {
                            element.data("listener", null);
                            listener = null;
                            closeDialog();
                        });
                        //Content
                        clearDialogContent();
                        //Append textfield
                        listener = appendTextField(schema.key, schema.hint, element.data("curvalue"), type, function (newValue) {
                            const oldValue = element.data("curvalue");
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
                    const listener = element.data("listener");
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
            const baseItem = Options.generateBaseItem(schema.label, schema.description);
            const button = generateBaseButton("chevron_right", function () {
                Options.pushSchema(schema.prefs, schema.label);
            });
            baseItem.secondaryContent.appendChild(button);
            return $(baseItem.listItem);
        }
    });

    //API call support
    Options.registerOptionsElement("api-call", {
        generate: function (schema) {
            const baseItem = Options.generateBaseItem(schema.label, schema.description);
            const button = generateBaseButton(schema.button_icon, function () {
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

//Sources option
Options.registerOptionsElement("sources", {
    generateSourceUi: function(element) {
        let sources = this.sources;
        let prefs = this.prefs;
        let schema = this.schema;
        if(sources == null || prefs == null || schema == null)
            return;

        //Clear current UI
        element.empty();

        let sourceDb = {};

        sources.forEach(function(source) {
            let langItem = sourceDb[source.lang.name];
            if(langItem == null) {
                langItem = [source.lang, []];
                sourceDb[source.lang.name] = langItem;
            }
            langItem[1].push(source);
        });

        function genSwitch() {
            let rootSwitch = $('<div class="mdc-switch"><div/>');
            let checkbox = $('<input type="checkbox" class="mdc-switch__native-control" />');
            let background = $('<div class="mdc-switch__background">');
            let knob = $('<div class="mdc-switch__knob"></div>');
            background.append(knob);
            rootSwitch.append(checkbox);
            rootSwitch.append(background);
            // Prevent switch click from clicking background
            rootSwitch.click(function(e) {
                e.stopPropagation();
            });
            return rootSwitch;
        }

        function genCheckbox() {
            let checkbox = $(`
                <div class="mdc-checkbox" data-mdc-auto-init="MDCCheckbox">
                  <input type="checkbox"
                         class="mdc-checkbox__native-control"/>
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
            `);
            // Prevent checkbox click from clicking background
            checkbox.click(function(e) {
                e.stopPropagation();
            });
            return checkbox;
        }

        for(let lang in sourceDb) {
            let langItem = sourceDb[lang];
            //Gen lang switch
            let wrapper = $('<div class="sources-group">');
            let langSwitchWrapper = $('<div class="sources-item sources-item-wrapper mdc-ripple-surface" data-mdc-auto-init="MDCRipple"></div>');
            let label = $('<label class="sources-item-label">');
            label.text(langItem[0].display_name);
            let langSwitch = genSwitch();
            langSwitch.addClass("sources-item-switch");
            let langSwitchCheckbox = langSwitch.find("input");
            //Propagate background clicks to switch
            langSwitchWrapper.click(function() {
                langSwitchCheckbox.click();
            });

            let sourceWrapper = $("<div/>");

            //Handle clicks
            langSwitchCheckbox.change(function() {
                let clonedOldPrefs = $.extend(true, {}, prefs);
                if(this.checked) {
                    prefs.source_languages.push(langItem[0].name);
                    sourceWrapper.slideDown();
                } else {
                    prefs.source_languages.splice(prefs.source_languages.indexOf(langItem[0].name), 1);
                    sourceWrapper.slideUp();
                }
                Options.saveDataChange(element, schema, clonedOldPrefs, prefs)
            });

            //Set intial value
            if(prefs.source_languages.includes(langItem[0].name)) {
                langSwitchCheckbox.prop('checked', true);
            } else {
                sourceWrapper.hide();
            }
            langSwitchWrapper.append(label);
            langSwitchWrapper.append(langSwitch);
            wrapper.append(langSwitchWrapper);

            //Gen source checkboxes
            langItem[1].forEach(function(source) {
                let checkBoxWrapper = $('<div class="sources-item mdc-ripple-surface" data-mdc-auto-init="MDCRipple"></div>');
                let checkbox = genCheckbox();
                let cbLabel = $('<label class="sources-item-label sources-source-label">');
                cbLabel.text(source.name);
                let realCheckbox = checkbox.find("input");
                //Propagate background clicks to checkbox
                checkBoxWrapper.click(function() {
                    realCheckbox.click();
                });

                let sourceIdString = source.id.toString();

                //Handle clicks
                realCheckbox.change(function() {
                    let clonedOldPrefs = $.extend(true, {}, prefs);
                    if(this.checked) {
                        prefs.hidden_catalogues.splice(prefs.hidden_catalogues.indexOf(sourceIdString), 1);
                    } else {
                        prefs.hidden_catalogues.push(sourceIdString);
                    }
                    Options.saveDataChange(element, schema, clonedOldPrefs, prefs)
                });

                //Set intial value
                if(!prefs.hidden_catalogues.includes(sourceIdString)) {
                    realCheckbox.prop('checked', true);
                }
                checkBoxWrapper.append(checkbox);
                checkBoxWrapper.append(cbLabel);
                sourceWrapper.append(checkBoxWrapper);
            });

            wrapper.append(sourceWrapper);
            element.append(wrapper);
        }

        mdc.autoInit(element[0]);
    },
    generate: function(schema) {
        let that = this;
        this.schema = schema;
        let element = $(document.createElement("div"));
        element.addClass("sources-wrapper");
        TWApi.Commands.Sources.execute(function(res) {
            that.sources = res.content.filter(function(source) {
                return source.name !== "LocalSource";
            });
            that.generateSourceUi(element);
        }, function() {
            //TODO Handle error
            snackbar.showSnackbar({
                message: schema.error_message,
                timeout: 2000
            });
        });
        return element;
    },
    setValue(element, value) {
        this.prefs = value;
        this.generateSourceUi(element);
    }
});

//Setup options
let optionsBody;

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