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

    public static int SAVE_INTERVAL = 15 * 60 * 1000; //The interval between library saves
    private static Timer timer; //Timers responsible for auto-saving the library

    public static void main(String[] args) {
        //Load the previously persisted library
        if(getLibraryFile().exists()) {
            loadLibrary();
        }
        //Schedule a timer to auto-save the library every 15 minutes (specified in SAVE_INTERVAL)
        timer = new Timer();
        //Setup auto save on library close
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
        //Start the HTTP API
        new HttpAPI().start();
    }

    /**
     * Setup any necessary shutdown hooks such as library persistence.
     **/
    public static void setupShutdownHooks() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //Cancel the auto-save timer
            timer.cancel();
            //Save the library
            saveLibrary();
        }));
    }

    /**
     * Setup any necessary shutdown hooks such as library persistence.
     **/
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

    /**
     * Load the last persisted library
     **/
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

    /**
     * Get the file where the latest library should be stored.
     **/
    public static File getLibraryFile() {
        return new File(Files.getLibraryDir(), "library.json");
    }
    
    /**
     * Save the library.
     * 
     * Moves old library to 'library_old_[id].json' before saving the new library.
     **/
    public static void saveLibrary() {
        //TODO Log instead of println
        System.out.println("Saving library...");
        File libraryFile = getLibraryFile();
        //Move library file if it already exists
        if (libraryFile.exists()) {
            //List files in library folder
            File[] oldLibraryFiles = Files.getLibraryDir().listFiles();
            if (oldLibraryFiles == null) {
                oldLibraryFiles = new File[0];
            }
            //Loop through possible names for the old library file
            int lastLibraryId = 0;
            String oldLibraryMoveTarget;
            do {
                lastLibraryId++;
                oldLibraryMoveTarget = "library_old_" + lastLibraryId + ".json";
            } while (Files.arrayContainsFileWithName(oldLibraryFiles, oldLibraryMoveTarget));
            //Actually move the file
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
        //Save the library to the library file
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
