package xyz.nulldev.ts;

import android.content.Context;
import android.content.SharedPreferences;
import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.servlet.SparkFilter;
import xyz.nulldev.ts.api.http.HttpAPI;
import xyz.nulldev.ts.config.Configuration;
import xyz.nulldev.ts.files.Files;
import xyz.nulldev.ts.library.Library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
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

    //Maximum number of library files to keep stored
    private static final int MAX_LIBRARY_FILES = 100;

    public static int SAVE_INTERVAL = 15 * 60 * 1000; //The interval between library saves
    private static Timer timer; //Timers responsible for auto-saving the library

    private static Logger logger = LoggerFactory.getLogger(TachiServer.class);

    private static Configuration configuration;

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
        //Load the previously persisted library
        if (getLibraryFile().exists()) {
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
        //Start UI server
        new TachiWebUIServer().start();
        //Start the HTTP API
        new HttpAPI().start();
    }

    /**
     * Setup any necessary shutdown hooks such as library persistence.
     **/
    public static void setupShutdownHooks() {
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    logger.info("Server shutting down...");
                                    timer.cancel(); //Cancel the auto-save timer
                                    saveLibrary(); //Save the library
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
            DIReplacement.get().injectBackupManager().restoreFromFile(getLibraryFile(), DIReplacement.get().getLibrary());
            //Get last int and long ids
            SharedPreferences preferences = getLibrarySharedPrefs();
            Library library = DIReplacement.get().getLibrary();
            library.setLastIntId(preferences.getInt(KEY_LAST_LIBRARY_INT, library.getLastIntId()));
            library.setLastLongId(
                    preferences.getLong(KEY_LAST_LIBRARY_LONG, library.getLastLongId()));
        } catch (IOException e) {
            logger.error("Failed to load library, falling back to empty library!", e);
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
        logger.info("Saving library...");
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
                logger.error(
                        "Failed to move old library from {} to {}!",
                        libraryFile.getName(),
                        oldLibraryMoveTarget,
                        e);
            }
        }
        //Clean up library folder
        cleanupLibraryFolder();
        //Save the library to the library file
        try {
            DIReplacement.get().injectBackupManager().backupToFile(libraryFile, DIReplacement.get().getLibrary(), false);
            //Save last int and long ids
            Library library = DIReplacement.get().getLibrary();
            getLibrarySharedPrefs()
                    .edit()
                    .putInt(KEY_LAST_LIBRARY_INT, library.getLastIntId())
                    .putLong(KEY_LAST_LIBRARY_LONG, library.getLastLongId())
                    .commit();
        } catch (IOException e) {
            logger.error("Failed to save library!", e);
        }
    }

    /**
     * Cleanup the library folder, 'tar.gz's all old library files if there are more than/equal to the max library files setting
     */
    public static void cleanupLibraryFolder() {
        File[] oldLibraryFiles = Files.getLibraryDir().listFiles();
        if (oldLibraryFiles == null) {
            oldLibraryFiles = new File[0];
        }
        //Filter out files not ending in ".json"
        File[] filteredLibraryFiles = Arrays.stream(oldLibraryFiles)
                .filter(file -> file.getName().endsWith(".json"))
                .toArray(File[]::new);
        if (filteredLibraryFiles.length >= MAX_LIBRARY_FILES) {
            logger.info("Too many library files, compressing some...");
            //Find archive output
            File archiveOutputFile;
            int lastId = 0;
            do {
                lastId++;
                archiveOutputFile = new File(Files.getLibraryDir(), "library_old_compressed_" + lastId + ".tar.gz");
            } while(archiveOutputFile.exists());

            //Setup gzip parameters
            GzipParameters gzipParameters = new GzipParameters();
            gzipParameters.setComment("Generated by: TachiWeb, contains old library files.");
            gzipParameters.setCompressionLevel(9); //High compression!
            gzipParameters.setFilename(archiveOutputFile.getName());

            //Actually do compression
            try (FileOutputStream fileOutputStream = new FileOutputStream(archiveOutputFile);
                 GzipCompressorOutputStream gzipCompressorOutputStream = new GzipCompressorOutputStream(fileOutputStream);
                 TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(gzipCompressorOutputStream)) {
                for(File file : filteredLibraryFiles) {
                    ArchiveEntry archiveEntry = tarArchiveOutputStream.createArchiveEntry(file, file.getName());
                    tarArchiveOutputStream.putArchiveEntry(archiveEntry);
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024 * 4];
                        int len;
                        while ((len = inputStream.read(buffer)) > -1 ){
                            tarArchiveOutputStream.write(buffer, 0, len);
                        }
                        tarArchiveOutputStream.flush();
                    } catch (IOException e) {
                        logger.error("Failed to compress file: '{}'!", file.getName());
                        throw e;
                    }
                    tarArchiveOutputStream.closeArchiveEntry();
                }
                //Delete compressed library files
                for(File file : filteredLibraryFiles) {
                    file.delete();
                }
            } catch (IOException e) {
                logger.error("Failed to cleanup library folder!", e);
            }
        }
    }
}
