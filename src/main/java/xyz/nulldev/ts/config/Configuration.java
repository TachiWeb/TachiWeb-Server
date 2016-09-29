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

package xyz.nulldev.ts.config;

import org.apache.commons.cli.*;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 22/09/16
 */
public class Configuration {
    private static final Options options = createOptions();

    private String ip = "0.0.0.0";
    private int port = 4567;
    private boolean demoMode = false;

    public static Configuration fromArgs(String[] args) throws ParseException {
        DefaultParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        if(commandLine.hasOption("help")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar TachiServer.jar", options);
            return null;
        }
        Configuration configuration  = new Configuration();
        if(commandLine.hasOption("ip")) {
            configuration.ip = commandLine.getOptionValue("ip");
        }
        if(commandLine.hasOption("port")) {
            String tempPort = commandLine.getOptionValue("port");
            try {
                configuration.port = Integer.parseInt(tempPort);
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid port (not a number)!");
            }
        }
        if(commandLine.hasOption("demo-mode")) {
            configuration.demoMode = true;
        }
        return configuration;
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("help", "Show this help text.");
        options.addOption("ip", true, "The IP to bind the server to.");
        options.addOption("port", true, "The port to bind the server to.");
        options.addOption("demo-mode", "Disable changes to the server settings.");
        return options;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isDemoMode() {
        return demoMode;
    }
}
