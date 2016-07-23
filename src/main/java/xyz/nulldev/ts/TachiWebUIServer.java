package xyz.nulldev.ts;

import spark.Spark;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 22/07/16
 */
public class TachiWebUIServer {
    public void start() {
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
        Spark.staticFiles.location("/tachiweb-ui");
    }
}
