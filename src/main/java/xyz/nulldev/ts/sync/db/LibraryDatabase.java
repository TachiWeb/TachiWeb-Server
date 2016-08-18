package xyz.nulldev.ts.sync.db;

import xyz.nulldev.ts.files.Files;

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

    static List<File> safeListFiles(File dir) {
        File[] files = dir.listFiles();
        List<File> fileList = new ArrayList<>();
        if (files != null) {
            Collections.addAll(fileList, files);
        }
        return fileList;
    }

    public List<String> getDevices() {
        return safeListFiles(rootDirectory)
                .stream()
                .map(File::getName)
                .collect(Collectors.toList());
    }
}
