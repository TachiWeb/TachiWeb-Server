let TWApi = {
    //Setup the API
    init: function () {
        this.Commands = this.Commands();
        delete this.init;
        return this;
    },
    //API Endpoints
    Endpoints: {
        Root: "/api"
    },
    Commands: function () {
        let that = this;
        let built = {};

        function ApiCommand(name, endpoint, customUrlBuilder, protocol = "GET") {
            endpoint = that.Endpoints.Root + endpoint;
            this.endpoint = function () {
                return that.Endpoints[name];
            };
            that.Endpoints[name] = endpoint;
            built[name] = this;
            let objectBeingConstructed = this;
            //Setup URL builder
            if (!customUrlBuilder) {
                customUrlBuilder = function () {
                    return objectBeingConstructed.endpoint();
                }
            }
            this.buildUrl = customUrlBuilder;
            //API Request function
            this.execute = function (onSuccess, onError, parameters, onComplete, rawResponseProcessor, preProcessor, xhrBuilder, xhrSender) {
                let onSuccessWrapper = function (res, xhr) {
                    if (onSuccess) onSuccess(res, xhr);
                };
                let onErrorWrapper = function (e, xhr) {
                    if (onError) onError(e, xhr);
                };
                let onCompleteWrapper = function () {
                    if (onComplete) onComplete();
                };
                let builtUrl = objectBeingConstructed.buildUrl(parameters);
                let xhr;
                //Override XHR creation
                if(xhrBuilder) {
                    xhr = xhrBuilder(builtUrl);
                }
                if(!xhr) {
                    xhr = new XMLHttpRequest();
                    xhr.open(protocol, builtUrl, true);
                }
                xhr.onload = function () {
                    try {
                        if (rawResponseProcessor) {
                            rawResponseProcessor(xhr);
                        } else {
                            let res = JSON.parse(xhr.responseText);
                            if (res.success) {
                                onSuccessWrapper(res, xhr);
                            } else {
                                if (res.error === "Not authenticated!") {
                                    //Not authenticated
                                    console.error("Not authenticated, redirecting to auth page!");
                                    window.location = "/auth.html?fu=" + encodeURIComponent(window.location.pathname + window.location.search);
                                } else {
                                    console.error("API error!", res.error);
                                    onErrorWrapper(res.error);
                                }
                            }
                        }
                    }
                    catch (e) {
                        console.error("Error processing API response!", e);
                        onErrorWrapper(e);
                    }
                    onCompleteWrapper();
                };
                xhr.onerror = function (e) {
                    console.error("Error getting API response!", e);
                    onErrorWrapper(e, xhr);
                    onCompleteWrapper();
                };
                if (preProcessor) {
                    preProcessor(xhr);
                }
                //Override XHR sending
                if(xhrSender) {
                    xhrSender(xhr);
                } else {
                    xhr.send();
                }
            }
        }

        //Add API commands
        new ApiCommand("Image", "/img", function (parameters) {
            return this.endpoint() + "/" + parameters.mangaId + "/" + parameters.chapterId + "/" + parameters.page;
        });
        new ApiCommand("Cover", "/cover", function(parameters) {
            return this.endpoint() + "/" + parameters.mangaId;
        });
        new ApiCommand("Library", "/library");
        new ApiCommand("MangaInfo", "/manga_info", function(parameters) {
            return this.endpoint() + "/" + parameters.mangaId;
        });
        new ApiCommand("Chapters", "/chapters", function(parameters) {
            return this.endpoint() + "/" + parameters.mangaId;
        });
        new ApiCommand("PageCount", "/page_count", function(parameters) {
            return this.endpoint() + "/" + parameters.mangaId + "/" + parameters.chapterId;
        });
        new ApiCommand("RestoreFile", "/restore_file");
        new ApiCommand("Backup", "/backup");
        new ApiCommand("Favorite", "/fave", function(parameters) {
            return this.endpoint() + "/" + parameters.mangaId + "?fave=" + parameters.favorite;
        });
        new ApiCommand("ReadingStatus", "/reading_status", function(parameters) {
            let currentUrl = this.endpoint() + "/" + parameters.mangaId + "/" + parameters.chapterId;
            let usedQuestionMark = false;
            if (parameters.read !== undefined && parameters.read !== null) {
                currentUrl += usedQuestionMark ? "&" : "?";
                currentUrl += "read=" + parameters.read;
                usedQuestionMark = true;
            }
            if (parameters.lastReadPage !== undefined && parameters.lastReadPage !== null) {
                currentUrl += usedQuestionMark ? "&" : "?";
                currentUrl += "lp=" + parameters.lastReadPage;
            }
            return currentUrl;
        });
        new ApiCommand("Update", "/update", function(parameters) {
            return this.endpoint() + "/" + parameters.mangaId + "/" + parameters.updateType;
        });
        new ApiCommand("Sources", "/sources");
        new ApiCommand("Catalogue", "/catalogue", null, "POST");
        new ApiCommand("LoginSources", "/list_login_sources");
        new ApiCommand("SourceLogin", "/source_login", function (parameters) {
            return this.endpoint() + "/" + parameters.sourceId
                + "?username=" + encodeURIComponent(parameters.username)
                + "&password=" + encodeURIComponent(parameters.password);
        });
        new ApiCommand("Download", "/download", function (parameters) {
            let builtUrl = this.endpoint() + "/" + parameters.mangaId + "/" + parameters.chapterId;
            if (parameters.del) {
                builtUrl += "?delete=true";
            }
            return builtUrl;
        });
        new ApiCommand("DownloadsOperation", "/downloads_op", function (parameters) {
            return this.endpoint() + "/" + parameters.operation;
        });
        new ApiCommand("GetDownloads", "/get_downloads");
        new ApiCommand("SetFlag", "/set_flag", function (parameters) {
            return this.endpoint() + "/" + parameters.mangaId + "/" + parameters.flag + "/" + parameters.state;
        });
        new ApiCommand("GetPrefs", "/prefs");
        new ApiCommand("SetPref", "/set_pref", function (parameters) {
            let string = this.endpoint() + "/" + parameters.key + "/" + parameters.type;
            if (parameters.value !== null && parameters.value !== undefined) {
                string += "?value=" + encodeURIComponent(parameters.value);
            }
            return string;
        });
        new ApiCommand("Auth", "/auth", function (parameters) {
            return this.endpoint() + "?password=" + encodeURIComponent(parameters.password);
        });
        new ApiCommand("ClearSessions", "/clear_sessions");
        new ApiCommand("GetCategories", "/get_categories");
        new ApiCommand("EditCategories", "/edit_categories", function(parameters) {
            let currentUrl = this.endpoint() + "/" + parameters.operation;

            let usedQuestionMark = false;
            if (parameters.categories != null) {
                currentUrl += usedQuestionMark ? "&" : "?";
                currentUrl += "categories=" + encodeURIComponent(JSON.stringify(parameters.categories));
                usedQuestionMark = true;
            }
            if (parameters.id != null) {
                currentUrl += usedQuestionMark ? "&" : "?";
                currentUrl += "id=" + parameters.id;
                usedQuestionMark = true;
            }
            if (parameters.name != null) {
                currentUrl += usedQuestionMark ? "&" : "?";
                currentUrl += "name=" + encodeURIComponent(parameters.name);
            }

            return currentUrl;
        });
        new ApiCommand("GetFilters", "/get_filters", function(parameters) {
            return this.endpoint() + "/" + parameters.id;
        });
        return built;
    }
}.init();
