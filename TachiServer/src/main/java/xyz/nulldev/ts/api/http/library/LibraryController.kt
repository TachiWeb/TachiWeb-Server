package xyz.nulldev.ts.api.http.library

import xyz.nulldev.ts.api.http.BaseController
import xyz.nulldev.ts.api.http.Response
import xyz.nulldev.ts.api.http.jvcompat.Context
import xyz.nulldev.ts.api.http.jvcompat.bodyAsClass
import xyz.nulldev.ts.api.http.jvcompat.json

object LibraryController : BaseController() {
    fun getLibraryFlags(ctx: Context) {
        ctx.json(Response.Success(api.library.flags))
    }

    fun setLibraryFlags(ctx: Context) {
        api.library.flags = ctx.bodyAsClass()
        ctx.json(Response.Success())
    }
}