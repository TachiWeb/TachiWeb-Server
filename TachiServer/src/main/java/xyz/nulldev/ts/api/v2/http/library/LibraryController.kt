package xyz.nulldev.ts.api.v2.http.library

import xyz.nulldev.ts.api.v2.http.jvcompat.Context
import xyz.nulldev.ts.api.v2.http.jvcompat.bodyAsClass
import xyz.nulldev.ts.api.v2.http.jvcompat.json
import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.Response

object LibraryController : BaseController() {
    fun getLibraryFlags(ctx: Context) {
        ctx.json(Response.Success(api.library.flags))
    }

    fun setLibraryFlags(ctx: Context) {
        api.library.flags = ctx.bodyAsClass()
        ctx.json(Response.Success())
    }
}