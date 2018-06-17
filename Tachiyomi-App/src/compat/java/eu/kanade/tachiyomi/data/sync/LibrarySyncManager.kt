package eu.kanade.tachiyomi.data.sync

import android.content.Context
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.sync.protocol.snapshot.SnapshotHelper
import uy.kohesive.injekt.injectLazy
import java.util.*

class LibrarySyncManager(private val context: Context) {
    private val prefs: PreferencesHelper by injectLazy()
    
    fun getDeviceId()
        = prefs.syncId().get() ?: regenDeviceId()
    
    fun regenDeviceId(): String {
        val deviceId = UUID.randomUUID().toString().replace('-', '_')
        prefs.syncId().set(deviceId)
        return deviceId
    }
    
    /**
     * The last time the library was synced
     */
    var lastSyncDateTime
        get() = prefs.lastSync().get()!!
        set(v) { prefs.lastSync().set(v) }

    /**
     * Sync snapshots
     */
    val snapshots by lazy { SnapshotHelper(context) }

    companion object {
        //Device ID, used to distinguish between devices when syncing with multiple servers
        //Currently only one server is supported so a static device ID is used
        val TARGET_DEVICE_ID = "server"
        
        val ACCOUNT_TYPE = "${BuildConfig.APPLICATION_ID}.sync-account"
        val AUTHORITY = "${BuildConfig.APPLICATION_ID}.sync-provider"

        const val PROTOCOL_VERSION = 1
    }
}
