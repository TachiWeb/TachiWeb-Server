package xyz.nulldev.ts

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.google.gson.Gson
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.track.TrackManager
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.source.SourceManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import xyz.nulldev.ts.ext.kInstanceLazy
import xyz.nulldev.ts.library.LibraryUpdater

class TachiyomiKodeinModule {

    val context: Context by kInstanceLazy()

    fun create() = Kodein.Module {
        //Bridge to Tachiyomi dependencies
        bind<PreferencesHelper>() with singleton { Injekt.get<PreferencesHelper>() }

        bind<DatabaseHelper>() with singleton { Injekt.get<DatabaseHelper>() }

        bind<ChapterCache>() with singleton { Injekt.get<ChapterCache>() }

        bind<CoverCache>() with singleton { Injekt.get<CoverCache>() }

        bind<NetworkHelper>() with singleton { Injekt.get<NetworkHelper>() }

        bind<SourceManager>() with singleton { Injekt.get<SourceManager>() }

        bind<DownloadManager>() with singleton { Injekt.get<DownloadManager>() }

        bind<TrackManager>() with singleton { Injekt.get<TrackManager>() }

        bind<Gson>() with singleton { Injekt.get<Gson>() }

        //Custom dependencies
        bind<BackupManager>() with singleton { BackupManager(context) }

        bind<LibraryUpdater>() with singleton { LibraryUpdater() }

        //Server dependencies
    }
}
