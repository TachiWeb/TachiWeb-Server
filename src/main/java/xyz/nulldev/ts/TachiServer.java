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
    public static int SAVE_INTERVAL = 15 * 60 * 1000;
    private static Timer timer;

    public static void main(String[] args) {
        if(getLibraryFile().exists()) {
            loadLibrary();
        }
        new HttpAPI().start();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                saveLibrary();
            }
        }, SAVE_INTERVAL, SAVE_INTERVAL);
        setupShutdownHooks();
    }
    public static void setupShutdownHooks() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            timer.cancel();
            saveLibrary();
        }));
    }
    public static void loadLibrary() {
        try {
            DIReplacement.get().injectBackupManager().restoreFromFile(getLibraryFile());
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
        if(libraryFile.exists()) {
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
                java.nio.file.Files.move(libraryFile.toPath(), new File(Files.getLibraryDir(), oldLibraryMoveTarget).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO Log this
            }
        }
        try {
            DIReplacement.get().injectBackupManager().backupToFile(libraryFile);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO Log this
        }
    }
}
