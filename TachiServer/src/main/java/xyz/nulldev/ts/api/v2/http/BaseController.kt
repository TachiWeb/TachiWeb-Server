package xyz.nulldev.ts.api.v2.http

import xyz.nulldev.ts.api.v2.java.Tachiyomi
import xyz.nulldev.ts.api.v2.java.model.ServerAPI

abstract class BaseController {
    protected val api: ServerAPI = Tachiyomi
}