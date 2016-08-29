package xyz.nulldev.ts;

import android.content.Context;
import eu.kanade.tachiyomi.data.backup.BackupManager;
import eu.kanade.tachiyomi.data.cache.ChapterCache;
import eu.kanade.tachiyomi.data.cache.CoverCache;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.network.NetworkHelper;
import eu.kanade.tachiyomi.data.preference.PreferencesHelper;
import eu.kanade.tachiyomi.data.source.SourceManager;
import xyz.nulldev.ts.android.CustomContext;
import xyz.nulldev.ts.api.task.TaskManager;
import xyz.nulldev.ts.files.Files;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.db.LibraryDatabase;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 11/07/16
 */
public class DIReplacement {

    private static final DIReplacement instance = new DIReplacement();
    static {
        instance.setupSourceManager();
        instance.setupDownloadManager();
    }

    public static DIReplacement get() {
        return instance;
    }

    private Context context = new CustomContext();

    public Context getContext() {
        return context;
    }

    private NetworkHelper networkHelper = new NetworkHelper(context);

    public NetworkHelper injectNetworkHelper() {
        return networkHelper;
    }

    private ChapterCache chapterCache = new ChapterCache(context);

    public ChapterCache injectChapterCache() {
        return chapterCache;
    }

    private PreferencesHelper preferencesHelper = new PreferencesHelper(context);

    public PreferencesHelper injectPreferencesHelper() {
        return preferencesHelper;
    }

    private SourceManager sourceManager;

    private void setupSourceManager() {
        sourceManager = new SourceManager(context);
    }
    public SourceManager injectSourceManager() {
        return sourceManager;
    }

    private CoverCache coverCache = new CoverCache(context);
    public CoverCache injectCoverCache() {
        return coverCache;
    }

    private BackupManager backupManager = new BackupManager();
    public BackupManager injectBackupManager() {
        return backupManager;
    }

    private DownloadManager downloadManager;
    private void setupDownloadManager() {
        downloadManager = new DownloadManager(context, sourceManager, preferencesHelper);
    }
    public DownloadManager injectDownloadManager() {
        return downloadManager;
    }

    private LibraryDatabase libraryDatabase = new LibraryDatabase(Files.getSyncDir(), backupManager);

    public LibraryDatabase getLibraryDatabase() {
        return libraryDatabase;
    }

    private TaskManager taskManager = new TaskManager();

    public TaskManager getTaskManager() {
        return taskManager;
    }

    private Library library = new Library();

    public Library getLibrary() {
        return library;
    }
}
