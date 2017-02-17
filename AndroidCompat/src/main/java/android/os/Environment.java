package android.os;

import xyz.nulldev.ts.files.Files;

import java.io.File;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 12/07/16
 */

/**
 * Android compatibility layer for files
 */
public class Environment {
    public static String DIRECTORY_ALARMS = getHomeFolder("Alarms").getAbsolutePath();
    public static String DIRECTORY_DCIM = getHomeFolder("DCIM").getAbsolutePath();
    public static String DIRECTORY_DOCUMENTS = getHomeFolder("Documents").getAbsolutePath();
    public static String DIRECTORY_DOWNLOADS = getHomeFolder("Downloads").getAbsolutePath();
    public static String DIRECTORY_MOVIES = getHomeFolder("Movies").getAbsolutePath();
    public static String DIRECTORY_MUSIC = getHomeFolder("Music").getAbsolutePath();
    public static String DIRECTORY_NOTIFICATIONS = getHomeFolder("Notifications").getAbsolutePath();
    public static String DIRECTORY_PICTURES = getHomeFolder("Pictures").getAbsolutePath();
    public static String DIRECTORY_PODCASTS = getHomeFolder("Podcasts").getAbsolutePath();
    public static String DIRECTORY_RINGTONES = getHomeFolder("Ringtones").getAbsolutePath();
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";
    public static final String MEDIA_CHECKING = "checking";
    public static final String MEDIA_EJECTING = "ejecting";
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
    public static final String MEDIA_NOFS = "nofs";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_SHARED = "shared";
    public static final String MEDIA_UNKNOWN = "unknown";
    public static final String MEDIA_UNMOUNTABLE = "unmountable";
    public static final String MEDIA_UNMOUNTED = "unmounted";

    public static File getHomeFolder() {
        return new File(System.getProperty("user.home"));
    }

    public static File getHomeFolder(String nestedFolder) {
        return new File(getHomeFolder(), nestedFolder);
    }

    public static File getRootDirectory() {
        return File.listRoots()[0];
    }

    public static File getDataDirectory() {
        return Files.getStorageDir();
    }

    public static File getExternalStorageDirectory() {
        return getHomeFolder();
    }

    public static File getExternalStoragePublicDirectory(String type) {
        return getHomeFolder();
    }

    public static File getDownloadCacheDirectory() {
        return Files.getExtCacheDir();
    }

    public static String getExternalStorageState() {
        return MEDIA_MOUNTED;
    }

    /** @deprecated */
    @Deprecated
    public static String getStorageState(File path) {
        //TODO Maybe actually check?
        return MEDIA_MOUNTED;
    }

    public static String getExternalStorageState(File path) {
        //TODO Maybe actually check?
        return MEDIA_MOUNTED;
    }

    public static boolean isExternalStorageRemovable() {
        return false;
    }

    public static boolean isExternalStorageRemovable(File path) {
        //TODO Maybe actually check?
        return false;
    }

    public static boolean isExternalStorageEmulated() {
        return false;
    }

    public static boolean isExternalStorageEmulated(File path) {
        return false;
    }

    public static File getLegacyExternalStorageDirectory() {
        return getHomeFolder();
    }
}
