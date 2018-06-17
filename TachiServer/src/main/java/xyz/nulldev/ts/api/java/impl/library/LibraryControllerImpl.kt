package xyz.nulldev.ts.api.java.impl.library

import com.f2prateek.rx.preferences.Preference
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import uy.kohesive.injekt.injectLazy
import xyz.nulldev.ts.api.java.model.FilterStatus
import xyz.nulldev.ts.api.java.model.SortDirection
import xyz.nulldev.ts.api.java.model.library.*
import eu.kanade.tachiyomi.ui.library.LibrarySort as LibrarySortConsts

class LibraryControllerImpl : LibraryController {
    private val prefs by injectLazy<PreferencesHelper>()

    private val libraryConstMappings = mapOf(
            LibrarySortConsts.ALPHA to LibrarySortType.ALPHA,
            LibrarySortConsts.LAST_READ to LibrarySortType.LAST_READ,
            LibrarySortConsts.LAST_UPDATED to LibrarySortType.LAST_UPDATED,
            LibrarySortConsts.UNREAD to LibrarySortType.UNREAD,
            LibrarySortConsts.TOTAL to LibrarySortType.TOTAL,
            LibrarySortConsts.SOURCE to LibrarySortType.SOURCE
    )

    override var flags: LibraryFlags
        get() = LibraryFlags(
                filters = listOf(
                        loadFilter(FilterType.DOWNLOADED),
                        loadFilter(FilterType.UNREAD),
                        loadFilter(FilterType.COMPLETED)
                ),
                sort = LibrarySort(
                        libraryConstMappings[prefs.librarySortingMode().getOrDefault()]
                                ?: error("Unknown sort type"),
                        if(prefs.librarySortingAscending().getOrDefault()) {
                            SortDirection.ASCENDING
                        } else {
                            SortDirection.DESCENDING
                        }
                ),
                display = if(prefs.libraryAsList().getOrDefault()) {
                    DisplayType.LIST
                } else {
                    DisplayType.GRID
                },
                showDownloadBadges = prefs.downloadBadge().getOrDefault()
        )
        set(value) {
            value.filters.forEach {
                filterTypeToPref(it.type).set(when(it.status) {
                    FilterStatus.ANY -> false
                    FilterStatus.INCLUDE -> true
                    FilterStatus.EXCLUDE -> throw IllegalArgumentException("FilterStatus.EXCLUDE is not supported in this field!")
                })
            }
            prefs.librarySortingMode().set(libraryConstMappings.entries.find { it.value == value.sort.type}?.key
                    ?: error("Unknown sorting mode!"))
            prefs.librarySortingAscending().set(when(value.sort.direction) {
                SortDirection.DESCENDING -> false
                SortDirection.ASCENDING -> true
            })
            prefs.libraryAsList().set(when(value.display) {
                DisplayType.GRID -> false
                DisplayType.LIST -> true
            })
            prefs.downloadBadge().set(value.showDownloadBadges)
        }

    private fun filterTypeToPref(filterType: FilterType): Preference<Boolean> {
        return when(filterType) {
            FilterType.DOWNLOADED -> prefs.filterDownloaded()
            FilterType.UNREAD -> prefs.filterUnread()
            FilterType.COMPLETED -> prefs.filterCompleted()
        }
    }

    private fun loadFilter(type: FilterType): LibraryFilter {
        return LibraryFilter(type, if(filterTypeToPref(type).getOrDefault()) {
            FilterStatus.INCLUDE
        } else {
            FilterStatus.ANY})
    }
}
