package xyz.nulldev.ts;

import android.content.Context;
import eu.kanade.tachiyomi.data.backup.BackupManager;
import eu.kanade.tachiyomi.data.cache.ChapterCache;
import eu.kanade.tachiyomi.data.cache.CoverCache;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.network.NetworkHelper;
import eu.kanade.tachiyomi.data.preference.PreferencesHelper;
import eu.kanade.tachiyomi.data.source.SourceManager;
import uy.kohesive.injekt.InjektKt;
import uy.kohesive.injekt.api.InjektRegistrar;
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

    public static DIReplacement get() {
        return instance;
    }

    private InjektRegistrar registrar() {
        return InjektKt.getInjekt().getRegistrar();
    }

    public Context getContext() {
        return registrar().getInstance(CustomContext.class);
    }

    public NetworkHelper injectNetworkHelper() {
        return registrar().getInstance(NetworkHelper.class);
    }

    public ChapterCache injectChapterCache() {
        return registrar().getInstance(ChapterCache.class);
    }

    public PreferencesHelper injectPreferencesHelper() {
        return registrar().getInstance(PreferencesHelper.class);
    }

    public SourceManager injectSourceManager() {
        return registrar().getInstance(SourceManager.class);
    }

    public CoverCache injectCoverCache() {
        return registrar().getInstance(CoverCache.class);
    }

    public BackupManager injectBackupManager() {
        return registrar().getInstance(BackupManager.class);
    }

    public DownloadManager injectDownloadManager() {
        return registrar().getInstance(DownloadManager.class);
    }

    public TaskManager getTaskManager() {
        return registrar().getInstance(TaskManager.class);
    }

    public Library getLibrary() {
        return registrar().getInstance(Library.class);
    }
}
