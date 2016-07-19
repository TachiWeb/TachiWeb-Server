package xyz.nulldev.ts;

import android.content.Context;
import android.content.SharedPreferences;
import xyz.nulldev.ts.api.http.HttpAPI;
import xyz.nulldev.ts.files.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 10/07/16
 */
public class TachiServer {

    private static final String KEY_LAST_LIBRARY_INT = "lastInt";
    private static final String KEY_LAST_LIBRARY_LONG = "lastLong";

    public static int SAVE_INTERVAL = 15 * 60 * 1000;
    private static Timer timer;

    public static void main(String[] args) {
        if (getLibraryFile().exists()) {
            loadLibrary();
        }
        new HttpAPI().start();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        saveLibrary();
                    }
                },
                SAVE_INTERVAL,
                SAVE_INTERVAL);
        setupShutdownHooks();
    }

    public static void setupShutdownHooks() {
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    timer.cancel();
                                    saveLibrary();
                                }));
    }

    private static SharedPreferences getLibrarySharedPrefs() {
        return DIReplacement.get()
                .getContext()
                .getSharedPreferences("libraryPrefs", Context.MODE_PRIVATE);
    }

    public static void loadLibrary() {
        try {
            DIReplacement.get().injectBackupManager().restoreFromFile(getLibraryFile());
            //Get last int and long ids
            SharedPreferences preferences = getLibrarySharedPrefs();
            Library library = DIReplacement.get().getLibrary();
            library.setLastIntId(preferences.getInt(KEY_LAST_LIBRARY_INT, library.getLastIntId()));
            library.setLastLongId(
                    preferences.getLong(KEY_LAST_LIBRARY_LONG, library.getLastLongId()));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO Log this
        }
    }

    public static File getLibraryFile() {
        return new File(Files.getLibraryDir(), "library.json");
    }
    
    public static void saveLibrary() {
        //TODO Log instead of println
        System.out.println("Saving library...");
        File libraryFile = getLibraryFile();
        if (libraryFile.exists()) {
            File[] oldLibraryFiles = Files.getLibraryDir().listFiles();
            if (oldLibraryFiles == null) {
                oldLibraryFiles = new File[0];
            }
            int lastLibraryId = 0;
            String oldLibraryMoveTarget;
            do {
                lastLibraryId++;
                oldLibraryMoveTarget = "library_old_" + lastLibraryId + ".json";
            } while (Files.arrayContainsFileWithName(oldLibraryFiles, oldLibraryMoveTarget));
            try {
                java.nio.file.Files.move(
                        libraryFile.toPath(),
                        new File(Files.getLibraryDir(), oldLibraryMoveTarget).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO Log this
            }
        }
        try {
            DIReplacement.get().injectBackupManager().backupToFile(libraryFile, false);
            //Save last int and long ids
            Library library = DIReplacement.get().getLibrary();
            getLibrarySharedPrefs()
                    .edit()
                    .putInt(KEY_LAST_LIBRARY_INT, library.getLastIntId())
                    .putLong(KEY_LAST_LIBRARY_LONG, library.getLastLongId())
                    .commit();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO Log this
        }
    }
}
