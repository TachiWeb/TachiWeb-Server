package xyz.nulldev.ts.api.http.auth

import io.javalin.Context

object AuthController {
    fun login(ctx: Context) {
        val session: String? = ctx.attribute("session")
        val password = ctx.queryParam("password")

        checkNotNull(session) { "Could not find session" }
        checkNotNull(password) { "Password must not be null" }

        val authPw = SessionManager.authPassword()

        val valid = if(password!!.isEmpty())
            authPw.isEmpty()
        else
            authPw.isNotEmpty() && PasswordHasher.check(password, authPw)

//        return if (valid) {
//            sessionManager.authenticateSession(session)
//            success().put(KEY_TOKEN, session)
//        } else {
//            error("Incorrect password!")
//        }
    }

    fun invalidateSession(ctx: Context) {
    }

    fun invalidateAll(ctx: Context) {
    }

    fun getSession(ctx: Context) {

    }
}