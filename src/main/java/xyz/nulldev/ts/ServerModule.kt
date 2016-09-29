package xyz.nulldev.ts

import android.content.Context
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.network.NetworkHelper
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.source.SourceManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.*
import xyz.nulldev.ts.android.CustomContext
import xyz.nulldev.ts.api.task.TaskManager
import xyz.nulldev.ts.library.Library

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 28/09/16
 *
 * Holds single factories for core components.
 */
class ServerModule(): InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory { CustomContext() }

        //Alias CustomContext -> Context
        Injekt.addSingletonFactory(fullType<Context>(), { get<CustomContext>() })

        addSingletonFactory { NetworkHelper(get()) }

        addSingletonFactory { ChapterCache(get()) }

        addSingletonFactory { PreferencesHelper(get()) }

        addSingletonFactory { SourceManager(get()) }

        addSingletonFactory { CoverCache(get()) }

        addSingletonFactory { BackupManager() }

        addSingletonFactory { DownloadManager(get(), get(), get()) }

        addSingletonFactory { TaskManager() }

        addSingletonFactory { Library() }
    }
}