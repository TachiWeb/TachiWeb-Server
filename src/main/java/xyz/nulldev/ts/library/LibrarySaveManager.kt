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

package xyz.nulldev.ts.library

import android.content.Context
import android.content.SharedPreferences
import eu.kanade.tachiyomi.data.backup.BackupManager
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.gzip.GzipParameters
import org.slf4j.LoggerFactory
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.files.Files
import java.io.*
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.concurrent.thread

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class LibrarySaveManager {

    private val timer = Timer() //Schedule a timer to auto-save the library every 30 minutes (specified in SAVE_INTERVAL)

    private val backupManager: BackupManager = Injekt.get()
    private val library: Library = Injekt.get()

    init {
        //Setup auto save on library close
        timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        saveLibrary()
                    }
                },
                SAVE_INTERVAL.toLong(),
                SAVE_INTERVAL.toLong())

        //Load the previously persisted library
        if (libraryFile.exists()) {
            loadLibrary()
        }

        //Setup shutdown hooks
        setupShutdownHooks()
    }

    /**
     * Get the file where the latest library should be stored.
     */
    val libraryFile: File
        get() = File(Files.getLibraryDir(), "library.json")

    /**
     * Load the last persisted library
     */
    fun loadLibrary() {
        try {
            backupManager.restoreFromFile(libraryFile, library)
            //Get last int and long ids
            val preferences = librarySharedPrefs
            library.lastIntId = preferences.getInt(KEY_LAST_LIBRARY_INT, library.lastIntId)
            library.lastLongId = preferences.getLong(KEY_LAST_LIBRARY_LONG, library.lastLongId)
        } catch (e: IOException) {
            logger.error("Failed to load library, falling back to empty library!", e)
        }
    }

    /**
     * Save the library.

     * Moves old library to 'library_old_[id].json' before saving the new library.
     */
    fun saveLibrary() {
        logger.info("Saving library...")
        val libraryFile = libraryFile
        //Move library file if it already exists
        if (libraryFile.exists()) {
            //List files in library folder
            val oldLibraryFiles = Files.getLibraryDir().listFiles() ?: emptyArray()

            //Loop through possible names for the old library file
            var lastLibraryId = 0
            var oldLibraryMoveTarget: String
            do {
                lastLibraryId++
                oldLibraryMoveTarget = "library_old_$lastLibraryId.json"
            } while (Files.arrayContainsFileWithName(oldLibraryFiles, oldLibraryMoveTarget))
            //Actually move the file
            try {
                java.nio.file.Files.move(
                        libraryFile.toPath(),
                        File(Files.getLibraryDir(), oldLibraryMoveTarget).toPath(),
                        StandardCopyOption.REPLACE_EXISTING)
            } catch (e: IOException) {
                logger.error(
                        "Failed to move old library from {} to {}!",
                        libraryFile.name,
                        oldLibraryMoveTarget,
                        e)
            }

        }
        //Clean up library folder
        cleanupLibraryFolder()
        //Save the library to the library file
        try {
            backupManager.backupToFile(libraryFile, library, false)
            //Save last int and long ids
            librarySharedPrefs.edit()
                    .putInt(KEY_LAST_LIBRARY_INT, library.lastIntId)
                    .putLong(KEY_LAST_LIBRARY_LONG, library.lastLongId)
                    .commit()
        } catch (e: IOException) {
            logger.error("Failed to save library!", e)
        }
    }

    /**
     * Cleanup the library folder, 'tar.gz's all old library files if there are more than/equal to the max library files setting
     */
    fun cleanupLibraryFolder() {
        val oldLibraryFiles = Files.getLibraryDir().listFiles() ?: emptyArray()

        //Filter out files not ending in ".json"
        val filteredLibraryFiles = oldLibraryFiles
                .filter { file -> file.name.endsWith(".json") }

        if (filteredLibraryFiles.size >= MAX_LIBRARY_FILES) {
            logger.info("Too many library files, compressing some...")
            //Find archive output
            var archiveOutputFile: File
            var lastId = 0
            do {
                lastId++
                archiveOutputFile = File(Files.getLibraryDir(), "library_old_compressed_$lastId.tar.gz")
            } while (archiveOutputFile.exists())

            //Setup gzip parameters
            val gzipParameters = GzipParameters()
            gzipParameters.comment = "Generated by: TachiWeb, contains old library files."
            gzipParameters.compressionLevel = 9 //High compression!
            gzipParameters.filename = archiveOutputFile.name

            //Actually do compression
            try {
                FileOutputStream(archiveOutputFile).use { fileOutputStream ->
                    GzipCompressorOutputStream(fileOutputStream).use { gzipCompressorOutputStream ->
                        TarArchiveOutputStream(gzipCompressorOutputStream).use { tarArchiveOutputStream ->
                            //Write files
                            for (file in filteredLibraryFiles) {
                                val archiveEntry = tarArchiveOutputStream.createArchiveEntry(file, file.name)
                                tarArchiveOutputStream.putArchiveEntry(archiveEntry)
                                //Write file content into archive
                                try {
                                    FileInputStream(file).use { inputStream: InputStream ->
                                        val buffer = ByteArray(1024 * 4)
                                        while (true) {
                                            val len = inputStream.read(buffer)
                                            if(len <= -1) {
                                                break
                                            }
                                            tarArchiveOutputStream.write(buffer, 0, len)
                                        }
                                        tarArchiveOutputStream.flush()
                                    }
                                } catch (e: IOException) {
                                    logger.error("Failed to compress file: '{}'!", file.name)
                                    throw e
                                }

                                tarArchiveOutputStream.closeArchiveEntry()
                            }
                            //Delete compressed library files
                            for (file in filteredLibraryFiles) {
                                file.delete()
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                logger.error("Failed to cleanup library folder!", e)
            }

        }
    }

    /**
     * Setup shutdown hook for library persistence
     */
    fun setupShutdownHooks() {
        Runtime.getRuntime().addShutdownHook(
                thread {
                    logger.info("Server shutting down...")
                    timer.cancel() //Cancel the auto-save timer
                    saveLibrary() //Save the library
                })
    }

    companion object {

        private val KEY_LAST_LIBRARY_INT = "lastInt"
        private val KEY_LAST_LIBRARY_LONG = "lastLong"

        //Maximum number of library files to keep stored
        private val MAX_LIBRARY_FILES = 100

        private val SAVE_INTERVAL = 30 * 60 * 1000 //The interval between library saves

        private val logger = LoggerFactory.getLogger(LibrarySaveManager::class.java)

        private val librarySharedPrefs: SharedPreferences
            get() = Injekt.get<Context>().getSharedPreferences("libraryPrefs", Context.MODE_PRIVATE)
    }
}
