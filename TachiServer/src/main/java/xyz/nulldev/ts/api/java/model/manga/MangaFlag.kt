package xyz.nulldev.ts.api.java.model.manga

import eu.kanade.tachiyomi.data.database.models.Manga
import java.util.*

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
enum class MangaFlag constructor(private val flagMask: Int,
                                 vararg val flagStates: MangaFlag.FlagState) {
    SORT_DIRECTION(Manga.SORT_MASK,
            FlagState("DESCENDING", Manga.SORT_DESC),
            FlagState("ASCENDING", Manga.SORT_ASC)),
    DISPLAY_MODE(Manga.DISPLAY_MASK,
            FlagState("NAME", Manga.DISPLAY_NAME),
            FlagState("NUMBER", Manga.DISPLAY_NUMBER)),
    READ_FILTER(Manga.READ_MASK,
            FlagState("READ", Manga.SHOW_READ),
            FlagState("UNREAD", Manga.SHOW_UNREAD),
            FlagState("ALL", Manga.SHOW_ALL)),
    DOWNLOADED_FILTER(Manga.DOWNLOADED_MASK,
            FlagState("DOWNLOADED", Manga.SHOW_DOWNLOADED),
            FlagState("NOT_DOWNLOADED", Manga.SHOW_NOT_DOWNLOADED),
            FlagState("ALL", Manga.SHOW_ALL)),
    SORT_TYPE(Manga.SORTING_MASK,
            FlagState("SOURCE", Manga.SORTING_SOURCE),
            FlagState("NUMBER", Manga.SORTING_NUMBER));

    fun findFlagState(name: String): FlagState? {
        for (state in flagStates) {
            if (state.name == name) {
                return state
            }
        }
        return null
    }

    fun findFlagState(value: Int): FlagState? {
        for (state in flagStates) {
            if (state.value == value) {
                return state
            }
        }
        return null
    }

    operator fun set(manga: Manga, state: FlagState) {
        setFlag(manga, state.value, flagMask)
    }

    private fun setFlag(manga: Manga, flag: Int, mask: Int) {
        manga.chapter_flags = manga.chapter_flags and mask.inv() or (flag and mask)
    }

    operator fun get(manga: Manga): FlagState? {
        return findFlagState(manga.chapter_flags and flagMask)
    }

    override fun toString(): String {
        return "MangaFlag{" +
                "flagMask=" + flagMask +
                ", flagStates=" + Arrays.toString(flagStates) +
                '}'
    }

    class FlagState internal constructor(val name: String?, val value: Int) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false

            val flagState = other as FlagState?

            return value == flagState!!.value && if (name != null) name == flagState.name else flagState.name == null
        }

        override fun hashCode(): Int {
            var result = if (name != null) name.hashCode() else 0
            result = 31 * result + value
            return result
        }

        override fun toString(): String {
            return "FlagState{name='$name', value=$value}"
        }
    }
}