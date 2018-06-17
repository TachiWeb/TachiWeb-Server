package xyz.nulldev.ts.api.http

import xyz.nulldev.ts.api.java.TachiyomiAPI
import xyz.nulldev.ts.api.java.model.ServerAPIInterface

abstract class BaseController {
    protected val api: ServerAPIInterface = TachiyomiAPI
}