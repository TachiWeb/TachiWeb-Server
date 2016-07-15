package xyz.nulldev.ts;

import eu.kanade.tachiyomi.data.backup.BackupManager;
import xyz.nulldev.ts.api.http.HttpAPI;

import java.io.File;
import java.io.IOException;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 10/07/16
 */
public class TachiServer {
    public static void main(String[] args) {
        try {
            new BackupManager().restoreFromFile(new File("/home/nulldev/tmp/tachiyomi-2016-07-14.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Library library = DIReplacement.get().getLibrary();
        new HttpAPI().start();
    }
}
