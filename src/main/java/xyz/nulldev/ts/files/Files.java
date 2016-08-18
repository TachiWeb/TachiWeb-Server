package xyz.nulldev.ts.files;

import java.io.File;
import java.util.Objects;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 10/07/16
 */
public class Files {
    static final String STORAGE_DIR = "data";

    public static File getStorageDir() {
        return mkdirs(new File(STORAGE_DIR));
    }

    public static File getPrefsDir() {
        return mkdirs(new File(getStorageDir(), "prefs"));
    }

    public static File getCacheDir() {
        return mkdirs(new File(getStorageDir(), "cache"));
    }

    public static File getExtCacheDir() {
        return mkdirs(new File(getStorageDir(), "external_cache"));
    }

    public static File getLibraryDir() {
        return mkdirs(new File(getStorageDir(), "library"));
    }

    public static File getDefaultDownloadsDir() {
        return mkdirs(new File(getStorageDir(), "downloads"));
    }

    public static File getSyncDir() {
        return mkdirs(new File(getStorageDir(), "sync"));
    }

    private static File mkdirs(File file) {
        file.mkdirs();
        return file;
    }

    public static boolean arrayContainsFileWithName(File[] list, String file) {
        for(File file1 : list) {
            if(Objects.equals(file1.getName(), file)) {
                return true;
            }
        }
        return false;
    }
}
