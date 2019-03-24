package xyz.nulldev.ts.api.v3.models.manga

import eu.kanade.tachiyomi.data.database.models.Manga

interface WMangaFlag {
    val mask: Int

    val value: Int
}

fun <T : WMangaFlag> Array<T>.firstForManga(manga: Manga): T? {
    return find {
        manga.chapter_flags and it.mask == it.value
    }
}

fun Manga.setFlag(flag: WMangaFlag) {
    chapter_flags = chapter_flags and flag.mask.inv() or (flag.value and flag.mask)
}