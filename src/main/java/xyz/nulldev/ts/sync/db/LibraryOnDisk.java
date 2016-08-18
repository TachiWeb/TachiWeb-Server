package xyz.nulldev.ts.sync.db;

import eu.kanade.tachiyomi.data.backup.BackupManager;
import xyz.nulldev.ts.library.Library;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/08/16
 */
public class LibraryOnDisk {
    private BackupManager backupManager;
    private Device device;
    private File libraryFile;

    public LibraryOnDisk(BackupManager backupManager, Device device, File libraryFile) {
        this.backupManager = backupManager;
        this.device = device;
        this.libraryFile = libraryFile;
    }

    private void ensureLibraryFileExists() {
        if (!libraryFile.exists()) {
            throw new IllegalStateException("Library file does not exist!");
        }
    }

    public LocalDateTime getSaveTime() {
        ensureLibraryFileExists();
        String fileName = libraryFile.getName().split("\\.")[0];
        long parsedFileName;
        try {
            parsedFileName = Long.parseLong(fileName);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Could not parse library save time!", e);
        }
        return Instant.ofEpochSecond(parsedFileName).atOffset(ZoneOffset.UTC).toLocalDateTime();
    }

    public File getLibraryFile() {
        return libraryFile;
    }

    public Library toLibrary() throws IOException {
        ensureLibraryFileExists();

        Library library = new Library();
        backupManager.restoreFromFile(libraryFile, library);
        return library;
    }
}
