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
 *
 * This was originally written for the history based synchronization system
 *
 * The current synchronization system only syncs the latest updated device so storage of devices and databases is not needed
 */
@Deprecated
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
                .sorted((o1, o2) -> o1.getSaveTime().compareTo(o2.getSaveTime()))
                .collect(Collectors.toList());
    }

    public LibraryOnDisk getLastUploadedLibrary() {
        List<LibraryOnDisk> librariesOnDisk = getLibrariesOnDisk();
        if(librariesOnDisk.size() < 1) {
            return null;
        } else {
            return librariesOnDisk.get(librariesOnDisk.size() - 1);
        }
    }

    public String getDeviceName() {
        ensureDeviceFolderExists();
        return deviceFolder.getName();
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

        List<Operation> allOperations = new ArrayList<>();
        //We need two libraries to compare (at least)
        if(loadedLibraries.size() >= 2) {
            Library oldLibrary = loadedLibraries.get(0);
            for (int i = 1; i < loadedLibraries.size(); i++) {
                Library library = loadedLibraries.get(i);
                LibraryOnDisk diskLibrary = diskLibraries.get(i);
                List<Operation> operations = LibraryComparer.compareLibraries(oldLibrary, library);
                operations.forEach(operation -> operation.setTimestamp(diskLibrary.getSaveTime()));
                allOperations.addAll(operations);
                oldLibrary = library;
            }
        }
        return allOperations;
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }

    public void setBackupManager(BackupManager backupManager) {
        this.backupManager = backupManager;
    }

    public LibraryDatabase getDatabase() {
        return database;
    }

    public void setDatabase(LibraryDatabase database) {
        this.database = database;
    }

    public File getDeviceFolder() {
        return deviceFolder;
    }

    public void setDeviceFolder(File deviceFolder) {
        this.deviceFolder = deviceFolder;
    }
}
