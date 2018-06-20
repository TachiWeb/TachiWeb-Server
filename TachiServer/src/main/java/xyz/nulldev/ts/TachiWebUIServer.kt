
/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts

import spark.Redirect
import spark.Spark.*
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import xyz.nulldev.ts.ext.kInstance
import java.io.File

/**
 * UI Server
 */
class TachiWebUIServer {
    private val serverConfig by lazy { kInstance<ConfigManager>().module<ServerConfig>() }

    private val NEW_UI_FOLDER = "/tachiweb-react/build"
    private val NEW_UI_INDEX = "$NEW_UI_FOLDER/index.html"

    private val newUiIndexText by lazy {
        this::class.java.getResourceAsStream(NEW_UI_INDEX).bufferedReader().use {
            it.readText()
        }
    }

    fun start() {
        staticFiles.header("Access-Control-Allow-Origin", "*")
        //Use external static files if specified
        if(serverConfig.useExternalStaticFiles) {
            val externalLoc = File(serverConfig.externalStaticFilesFolder)

            if(!externalLoc.exists())
                throw RuntimeException("External static files folder does not exist!")

            staticFiles.externalLocation(externalLoc.absolutePath)
        } else {
            staticFiles.location(if(serverConfig.useOldWebUi)
                "/tachiweb-ui"
            else
                NEW_UI_FOLDER)
        }

        if(serverConfig.useOldWebUi) {
            redirect.any("/", "/library.html", Redirect.Status.TEMPORARY_REDIRECT)
            redirect.any("", "/library.html", Redirect.Status.TEMPORARY_REDIRECT)
        } else {
            redirect.any("/", "/index.html", Redirect.Status.TEMPORARY_REDIRECT)
            redirect.any("", "/index.html", Redirect.Status.TEMPORARY_REDIRECT)
        }
    }

    fun postConfigure() {
        if(!serverConfig.useOldWebUi) {
            // Route all unhandled requests to React
            get("/*") { _, _ -> newUiIndexText }
        }
    }
}