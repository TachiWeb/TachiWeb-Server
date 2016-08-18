package xyz.nulldev.ts.sync.db;

import eu.kanade.tachiyomi.data.backup.BackupManager;
import xyz.nulldev.ts.files.Files;
import xyz.nulldev.ts.sync.operation.Operation;
import xyz.nulldev.ts.sync.operation.manga.UpdateMangaOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/08/16
 */
public class LibraryDatabase {
    private File rootDirectory = Files.getSyncDir();
    private BackupManager backupManager = new BackupManager();

    public LibraryDatabase(BackupManager backupManager) {
        this.backupManager = backupManager;
    }

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

    public List<String> getDeviceNames() {
        return safeListFiles(rootDirectory)
                .stream()
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public List<Device> getDevices() {
        return safeListFiles(rootDirectory)
                .stream()
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
                        if(casted.getName().equals(castedTargetOperation.getMangaUrl())
                                && casted.getMangaSource() == castedTargetOperation.getMangaSource()) {
                            operations.remove(a);
                        }
                    }
                }
            }
        }
        return operations;
    }
}
