package xyz.nulldev.ts;

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
    public static int SAVE_INTERVAL = 15 * 60 * 1000; //The interval between library saves
    private static Timer timer; //Timers responsible for auto-saving the library

    public static void main(String[] args) {
        //Load the previously persisted library
        if(getLibraryFile().exists()) {
            loadLibrary();
        }
        //Schedule a timer to auto-save the library every 15 minutes (specified in SAVE_INTERVAL)
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveLibrary();
            }
        }, SAVE_INTERVAL, SAVE_INTERVAL);
        //Setup auto save on library close
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
     * Load the last persisted library
     **/
    public static void loadLibrary() {
        try {
            DIReplacement.get().injectBackupManager().restoreFromFile(getLibraryFile());
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
        if(libraryFile.exists()) {
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
                java.nio.file.Files.move(libraryFile.toPath(), new File(Files.getLibraryDir(), oldLibraryMoveTarget).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO Log this
            }
        }
        //Save the library to the library file
        try {
            DIReplacement.get().injectBackupManager().backupToFile(libraryFile, false);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO Log this
        }
    }
}
