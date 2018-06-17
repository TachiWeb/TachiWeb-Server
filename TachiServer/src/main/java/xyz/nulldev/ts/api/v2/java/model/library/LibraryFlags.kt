package xyz.nulldev.ts.api.v2.java.model.library

import xyz.nulldev.ts.api.v2.java.model.FilterStatus
import xyz.nulldev.ts.api.v2.java.model.SortDirection

data class LibraryFlags(
        val filters: List<LibraryFilter>,
        val sort: LibrarySort,
        var display: DisplayType,
        var showDownloadBadges: Boolean
) {
    companion object {
        private val DEFAULT =
                LibraryFlags(
                        filters = mutableListOf(
                                LibraryFilter(FilterType.DOWNLOADED, FilterStatus.ANY),
                                LibraryFilter(FilterType.UNREAD, FilterStatus.ANY),
                                LibraryFilter(FilterType.COMPLETED, FilterStatus.ANY)
                        ),
                        sort = LibrarySort(LibrarySortType.ALPHA, SortDirection.ASCENDING),
                        display = DisplayType.GRID,
                        showDownloadBadges = false
                )
    }
}

data class LibraryFilter(var type: FilterType, var status: FilterStatus)

data class LibrarySort(var type: LibrarySortType,
                       var direction: SortDirection)

enum class LibrarySortType {
    ALPHA,
    LAST_READ,
    LAST_UPDATED,
    UNREAD,
    TOTAL,
    SOURCE
}

enum class FilterType {
    DOWNLOADED,
    UNREAD,
    COMPLETED
}

enum class DisplayType {
    GRID,
    LIST
}
