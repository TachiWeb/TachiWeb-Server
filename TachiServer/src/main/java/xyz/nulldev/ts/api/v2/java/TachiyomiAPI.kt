package xyz.nulldev.ts.api.v2.java

import xyz.nulldev.ts.api.v2.java.model.ServerAPI
import java.util.*

/**
 * Tachiyomi API
 */
object Tachiyomi : ServerAPI by api

// Hidden, private real API reference
private val api = ServiceLoader.load(ServerAPI::class.java).first()
