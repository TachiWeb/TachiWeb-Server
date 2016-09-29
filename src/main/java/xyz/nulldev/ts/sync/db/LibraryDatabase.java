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
import xyz.nulldev.ts.sync.operation.Operation;
import xyz.nulldev.ts.sync.operation.manga.UpdateMangaOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class LibraryDatabase {
    //TODO Keep a master DB
    private File rootDirectory;
    private BackupManager backupManager = new BackupManager();

    public LibraryDatabase(File rootDirectory, BackupManager backupManager) {
        this.rootDirectory = rootDirectory;
        this.backupManager = backupManager;
    }

    static List<File> safeListFiles(File dir) {
        File[] files = dir.listFiles();
        List<File> fileList = new ArrayList<>();
        if (files != null) {
            Collections.addAll(fileList, files);
        }
        return fileList;
    }

    private Stream<File> getDeviceFilesStream() {
        return safeListFiles(rootDirectory).stream().filter(File::isDirectory);
    }

    public List<String> getDeviceNames() {
        return getDeviceFilesStream()
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public List<Device> getDevices() {
        return getDeviceFilesStream()
                .map(f -> new Device(backupManager, LibraryDatabase.this, f))
                .collect(Collectors.toList());
    }

    public List<Operation> compareAllLibraries() {
        List<Operation> operations = new ArrayList<>();
        for (Device device : getDevices()) {
            operations.addAll(device.compareLibraries());
        }
        //Sort operations by timestamp
        Collections.sort(operations, (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
        //Deduplicate update operations
        for(int i = 0; i < operations.size(); i++) {
            Operation operation = operations.get(i);
            if(operation instanceof UpdateMangaOperation) {
                UpdateMangaOperation casted = (UpdateMangaOperation) operation;
                //Iterate backwards through operation list to support removal
                //Basically, we want to remove all updates of the same manga after the first update (so we only update the manga the first time we need it)
                for (int a = operations.size() - 1; a > i; a--) {
                    Operation targetOperation = operations.get(a);
                    if(targetOperation instanceof UpdateMangaOperation) {
                        UpdateMangaOperation castedTargetOperation = (UpdateMangaOperation) targetOperation;
                        //Check if URL and source is the same
                        if(casted.getMangaUrl().equals(castedTargetOperation.getMangaUrl())
                                && casted.getMangaSource() == castedTargetOperation.getMangaSource()) {
                            operations.remove(a);
                        }
                    }
                }
            }
        }
        return operations;
    }

    public void stripOldLibraries() {
        //Get oldest last uploaded library
        LibraryOnDisk oldestLastUploadedLibrary = null;
        for(Device device : getDevices()) {
            LibraryOnDisk lastUploadedLibrary = device.getLastUploadedLibrary();
            if(lastUploadedLibrary == null) {
                continue;
            }
            if(oldestLastUploadedLibrary == null
                    || lastUploadedLibrary.getSaveTime().isBefore(oldestLastUploadedLibrary.getSaveTime())) {
                oldestLastUploadedLibrary = lastUploadedLibrary;
            }
        }
        //TODO Permanently apply all changes of these libraries to the master database and delete the local copies
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }
}
