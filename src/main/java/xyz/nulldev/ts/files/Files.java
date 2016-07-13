package xyz.nulldev.ts.files;

import java.io.File;

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

    private static File mkdirs(File file) {
        file.mkdirs();
        return file;
    }
}
