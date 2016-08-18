package xyz.nulldev.ts.sync.db;

import eu.kanade.tachiyomi.data.backup.BackupManager;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.LibraryComparer;
import xyz.nulldev.ts.sync.operation.Operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/08/16
 */
public class Device {
    private BackupManager backupManager;
    private LibraryDatabase database;
    private File deviceFolder;

    public Device(BackupManager backupManager, LibraryDatabase database, File deviceFolder) {
        this.backupManager = backupManager;
        this.database = database;
        this.deviceFolder = deviceFolder;
    }

    private void ensureDeviceFolderExists() {
        if (!deviceFolder.exists()) {
            throw new IllegalStateException("Library file does not exist!");
        }
    }

    public List<LibraryOnDisk> getLibrariesOnDisk() {
        ensureDeviceFolderExists();
        return LibraryDatabase.safeListFiles(deviceFolder)
                .stream()
                .map(file -> new LibraryOnDisk(backupManager, Device.this, file))
                .collect(Collectors.toList());
    }

    public List<Operation> compareLibraries() {
        //Load all libraries
        List<LibraryOnDisk> diskLibraries = getLibrariesOnDisk();
        List<Library> loadedLibraries = diskLibraries.stream().map(libraryOnDisk -> {
            try {
                return libraryOnDisk.toLibrary();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }).collect(Collectors.toList());

        //We need two libraries to compare (at least)
        if(loadedLibraries.size() < 2) {
            throw new IllegalStateException("Not enough libraries to compare (need at least 2).");
        }
        List<Operation> allOperations = new ArrayList<>();
        Library oldLibrary = loadedLibraries.get(0);
        for(int i = 1; i < loadedLibraries.size(); i++) {
            Library library = loadedLibraries.get(i);
            LibraryOnDisk diskLibrary = diskLibraries.get(i);
            List<Operation> operations = LibraryComparer.compareLibraries(oldLibrary, library);
            operations.forEach(operation -> operation.setTimestamp(diskLibrary.getSaveTime()));
            allOperations.addAll(operations);
            oldLibrary = library;
        }
        return allOperations;
    }
}
