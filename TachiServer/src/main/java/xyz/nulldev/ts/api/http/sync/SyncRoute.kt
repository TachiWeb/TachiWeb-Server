package xyz.nulldev.ts.api.http.sync

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.sync.LibrarySyncManager
import eu.kanade.tachiyomi.data.sync.gson.SyncGsonProvider
import eu.kanade.tachiyomi.data.sync.protocol.ReportApplier
import eu.kanade.tachiyomi.data.sync.protocol.ReportGenerator
import eu.kanade.tachiyomi.data.sync.protocol.models.SyncReport
import eu.kanade.tachiyomi.data.sync.protocol.models.common.SyncResponse
import eu.kanade.tachiyomi.data.sync.protocol.snapshot.SnapshotHelper
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute

/**
 * Sync route, executes all sync operations
 *
 * CAVEAT: Ensure this route is able to be fully isolated from the rest of the server
 *         This condition must be satisfied to ensure multiple users can sync to the same server
 */
class SyncRoute(override val kodein: Kodein = Kodein.global) : TachiWebRoute(), KodeinAware {
    private val context: Context by lazy.instance()
    private val db: DatabaseHelper by lazy.instance()
    private val syncManager: LibrarySyncManager by lazy.instance()
    private val snapshots by lazy { SnapshotHelper(context, db) }
    private val lastSyncs by lazy { LastSyncDb(kodein) }

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        fun SyncResponse.toJson() = SyncGsonProvider.gson.toJson(this)

        try {
            var dId = INITAL_SNAPSHOT_NAME

            var result: String? = null

            val syncStartTime = System.currentTimeMillis()

            db.inTransaction {
                var inReport: SyncReport? = null

                //If has write=false or request is GET, then do not expect any sync input
                if (request.queryParams("write")?.toLowerCase() != "false"
                        && request.requestMethod() == "POST") {
                    //Apply client report
                    val body = request.body()
                    inReport = SyncGsonProvider.gson.fromJson(body, SyncReport::class.java)
                    ReportApplier(context).apply(inReport)
                    dId = inReport.deviceId
                }

                //Use stored start time
                val startTime = inReport?.deviceId?.let {
                    lastSyncs.lastSyncs[it]
                } ?: 0L

                //Ensure initial snapshot is taken
                db.takeEmptyMangaCategoriesSnapshot(dId).executeAsBlocking()

                //Generate server report
                val report = ReportGenerator(context, db, syncManager).gen(LOCAL_DEVICE_NAME,
                        dId,
                        startTime,
                        syncStartTime) //Do not include changes made since the sync has started
                                       //as those are the client's changes

                if(inReport != null) {
                    //Correct timestamps AFTER client report generated
                    inReport.tmpApply.applyQueuedTimestamps(db)

                    //Taking snapshots after the sync will cause incoming changes to be repeated
                    //back to the client (but there is no easy way around this)
                    db.deleteMangaCategoriesSnapshot(dId).executeAsBlocking()
                    db.takeMangaCategoriesSnapshot(dId).executeAsBlocking()
                    snapshots.takeSnapshots(dId)

                    //Update sync times for other devices (clone original map)
                    lastSyncs.lastSyncs.toMap().forEach { t, u ->
                        if(t != inReport.deviceId && u > inReport.from)
                            lastSyncs.lastSyncs[t] = inReport.from
                    }
                    //Save last sync time for this device
                    lastSyncs.lastSyncs[dId] = report.to
                    lastSyncs.save()
                }

                response.status(200)
                result = SyncResponse().apply {
                    this.serverChanges = report
                }.toJson()
            }

            return result ?: throw NullPointerException("Sync result is null!")
        } catch(t: Throwable) {
            logger.error("Could not perform sync!", t)
            response.status(500)
            return SyncResponse().apply {
                this.error = "Internal error: ${t.message}"
            }.toJson()
        }
    }

    companion object {
        private val LOCAL_DEVICE_NAME = "server"
        private val INITAL_SNAPSHOT_NAME = "initial"
    }
}
