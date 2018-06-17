package xyz.nulldev.ts.api.http.jvcompat

import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute

class JavalinShim(val method: (Context) -> Unit) : TachiWebRoute() {
    override fun handleReq(request: Request, response: Response): Any? {
        method(request to response)
        return ""
    }
}