package xyz.nulldev.ts.api.http.sync

import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.sync.gson.GsonProvider
import xyz.nulldev.ts.sync.protocol.ReportGenerator

class SyncRoute : TachiWebRoute() {

    override fun handleReq(request: Request, response: Response): Any {
        val report = ReportGenerator().gen(0L)
        return GsonProvider.gson.toJson(report)
    }
}
