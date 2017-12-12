package xyz.nulldev.ts.api.http.sync

import eu.kanade.tachiyomi.data.sync.protocol.ReportApplier
import eu.kanade.tachiyomi.data.sync.protocol.ReportGenerator
import eu.kanade.tachiyomi.data.sync.protocol.models.SyncReport
import eu.kanade.tachiyomi.data.sync.protocol.models.common.SyncResponse
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.sync.gson.GsonProvider

class SyncRoute : TachiWebRoute() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        fun SyncResponse.toJson() = GsonProvider.gson.toJson(this)

        try {
            //If has write=false or request is GET, then do not expect any sync input
            if (request.queryParams("write")?.toLowerCase() != "false"
                    && request.requestMethod() == "POST") {
                //Apply client report
                val body = request.body()
                val report = GsonProvider.gson.fromJson(body, SyncReport::class.java)
                ReportApplier().apply(report)
            }

            //Generate server report
            val report = ReportGenerator().gen(0L)
            response.status(200)
            return SyncResponse().apply {
                this.serverChanges = report
            }.toJson()
        } catch(t: Throwable) {
            logger.error("Could not perform sync!", t)
            response.status(500)
            return SyncResponse().apply {
                this.error = "Internal error: ${t.message}"
            }.toJson()
        }
    }
}
