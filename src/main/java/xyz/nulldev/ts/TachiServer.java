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

package xyz.nulldev.ts;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import uy.kohesive.injekt.InjektKt;
import xyz.nulldev.ts.api.http.HttpAPI;
import xyz.nulldev.ts.config.Configuration;
import xyz.nulldev.ts.library.LibrarySaveManager;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 10/07/16
 */
public class TachiServer {

    private static Logger logger = LoggerFactory.getLogger(TachiServer.class);

    private static Configuration configuration;

    private static LibrarySaveManager librarySaveManager;

    public static void main(String[] args) {
        logger.info("Starting server...");
        //Load config
        try {
            configuration = Configuration.fromArgs(args);
            if(configuration == null) {
                return;
            }
            Spark.ipAddress(configuration.getIp());
            Spark.port(configuration.getPort());
        } catch (ParseException e) {
            System.out.println("Error parsing CLI args: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        //Prepare dependency injection
        InjektKt.getInjekt().importModule(new ServerModule());

        //Setup library persistence manager
        librarySaveManager = new LibrarySaveManager();

        //Start UI server
        new TachiWebUIServer().start();
        //Start the HTTP API
        new HttpAPI().start();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
