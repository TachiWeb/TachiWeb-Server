package eu.kanade.tachiyomi.source

import xyz.nulldev.ts.util.invokeIn

class SourceInjector(val sourceManager: SourceManager) {
    fun registerSource(source: Source, overwrite: Boolean = false) {
        this::registerSource.invokeIn(sourceManager, source, overwrite)
    }
}