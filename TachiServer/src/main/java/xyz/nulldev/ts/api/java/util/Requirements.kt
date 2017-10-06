package xyz.nulldev.ts.api.java.util

import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.Source

fun Source?.require() = requireNotNull(this, { "Manga source is required!" })
fun Source?.ensureLoaded() = requireNotNull(this, { "Manga source is not loaded!" })

fun Manga?.ensureInDatabase() = requireNotNull(this, { "Manga not in database!" })
