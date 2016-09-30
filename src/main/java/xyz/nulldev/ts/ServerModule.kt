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
import xyz.nulldev.ts.library.LibraryUpdater

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

        addSingletonFactory { LibraryUpdater() }
    }
}