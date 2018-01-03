
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
import spark.Spark.redirect
import spark.Spark.staticFiles
import xyz.nulldev.ts.config.ConfigManager
import xyz.nulldev.ts.config.ServerConfig
import java.io.File

/**
 * UI Server
 */
class TachiWebUIServer {
    val serverConfig by lazy { ConfigManager.module<ServerConfig>() }

    fun start() {
        staticFiles.header("Access-Control-Allow-Origin", "*")
        //Use external static files if specified
        if(serverConfig.useExternalStaticFiles) {
            val externalLoc = File(serverConfig.externalStaticFilesFolder)

            if(!externalLoc.exists())
                throw RuntimeException("External static files folder does not exist!")

            staticFiles.externalLocation(externalLoc.absolutePath)
        } else {
            staticFiles.location("/tachiweb-ui")
        }
        redirect.any("/", "/library.html", Redirect.Status.TEMPORARY_REDIRECT)
        redirect.any("", "/library.html", Redirect.Status.TEMPORARY_REDIRECT)
    }
}