package xyz.nulldev.ts.api.http.sync

import android.content.Context
import eu.kanade.tachiyomi.data.database.DatabaseHelper
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
import xyz.nulldev.ts.ext.kInstanceLazy

class SyncRoute : TachiWebRoute() {
    private val context: Context by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()
    private val snapshots by lazy { SnapshotHelper(context) }

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        fun SyncResponse.toJson() = SyncGsonProvider.gson.toJson(this)

        try {
            var dId = INITAL_SNAPSHOT_NAME

            return db.inTransaction {
                var takeSnapshots = false

                //If has write=false or request is GET, then do not expect any sync input
                if (request.queryParams("write")?.toLowerCase() != "false"
                        && request.requestMethod() == "POST") {
                    //Apply client report
                    val body = request.body()
                    val report = SyncGsonProvider.gson.fromJson(body, SyncReport::class.java)
                    ReportApplier(context).apply(report)
                    dId = report.deviceId

                    takeSnapshots = true
                }

                val startTime = request.queryParams("from")?.toLongOrNull() ?: 0L

                //Ensure initial snapshot is taken
                db.takeEmptyMangaCategoriesSnapshot(dId).executeAsBlocking()

                //Generate server report
                val report = ReportGenerator(context).gen(LOCAL_DEVICE_NAME,
                        dId,
                        startTime)

                //Taking snapshots after the sync will cause incoming changes to be repeated
                //back to the client (but there is no easy way around this)
                if(takeSnapshots) {
                    db.deleteMangaCategoriesSnapshot(dId).executeAsBlocking()
                    db.takeMangaCategoriesSnapshot(dId).executeAsBlocking()
                    snapshots.takeSnapshots(dId)
                }

                response.status(200)
                SyncResponse().apply {
                    this.serverChanges = report
                }.toJson()
            }
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
