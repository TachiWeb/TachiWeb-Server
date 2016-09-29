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
