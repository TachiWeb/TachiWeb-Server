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

import org.apache.commons.cli.ParseException
import org.slf4j.LoggerFactory
import spark.Spark
import uy.kohesive.injekt.Injekt
import xyz.nulldev.ts.api.http.HttpAPI
import xyz.nulldev.ts.config.Configuration
import xyz.nulldev.ts.library.LibrarySaveManager

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
object TachiServer {

    private val logger = LoggerFactory.getLogger(TachiServer::class.java)

    lateinit var configuration: Configuration

    private lateinit var librarySaveManager: LibrarySaveManager

    @JvmStatic fun main(args: Array<String>) {
        logger.info("Starting server...")
        //Load config
        try {
            val tempConfig = Configuration.fromArgs(args) ?: return
            configuration = tempConfig

            Spark.ipAddress(configuration.ip)
            Spark.port(configuration.port)
        } catch (e: ParseException) {
            println("Error parsing CLI args: " + e.message)
            e.printStackTrace()
            return
        }

        //Prepare dependency injection
        Injekt.importModule(ServerModule())

        //Setup library persistence manager
        librarySaveManager = LibrarySaveManager()

        //Start UI server
        TachiWebUIServer().start()
        //Start the HTTP API
        HttpAPI().start()
    }
}
