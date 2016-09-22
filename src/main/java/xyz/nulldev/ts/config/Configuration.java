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
        return configuration;
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("help", "Show this help text.");
        options.addOption("ip", true, "The IP to bind the server to.");
        options.addOption("port", true, "The port to bind the server to.");
        return options;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
