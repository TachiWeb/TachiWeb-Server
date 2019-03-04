package xyz.nulldev.ts.api.v3.models.manga

import xyz.nulldev.ts.api.v3.models.chapters.WChapter

data class WRecentlyRead(
        val chapter: WChapter,
        val manga: WManga
)