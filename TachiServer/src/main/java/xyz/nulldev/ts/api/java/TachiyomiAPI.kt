package xyz.nulldev.ts.api.java

import xyz.nulldev.ts.api.java.impl.ServerAPIImpl
import xyz.nulldev.ts.api.java.model.ServerAPIInterface

/**
 * Tachiyomi API
 */
object TachiyomiAPI : ServerAPIInterface by api

// Hidden, private real API reference
private val api = ServerAPIImpl()
