package xyz.nulldev.ts;

import spark.Redirect;
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
        Spark.redirect.any("/", "/library.html", Redirect.Status.TEMPORARY_REDIRECT);
        Spark.redirect.any("", "/library.html", Redirect.Status.TEMPORARY_REDIRECT);
    }
}
