// @flow
import * as React from 'react';
import type { MangaType, LibraryFlagsType } from 'types';

// TODO: Consider using a fuzzy search package for the search filter

// NOTE: localeCompare is for string comparison
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/localeCompare
function stringComparison(a: string, b: string) {
  return a.localeCompare(b, 'en', { sensitivity: 'base' }); // case insensitive
}

// NOTE: sortFuncs.UNREAD and readFilterFuncs require the # of unread chapters for a manga
//       Because I'm keeping unread data separate from the mangaInfo, I need to pass unread
//       to them (even though sortFuncs only needs it for one type).

// If whatever sort comparison is equal, fallback on ordering by title
const sortFuncs = unread => ({
  ALPHABETICALLY: (a, b) => stringComparison(a.title, b.title),
  UNREAD: (a, b) => {
    if (unread[a.id] !== unread[b.id]) {
      return unread[a.id] - unread[b.id];
    }
    return stringComparison(a.title, b.title);
  },
  TOTAL_CHAPTERS: (a, b) => {
    if (a.chapters == null || b.chapters == null) return 0;

    if (a.chapters !== b.chapters) {
      return a.chapters - b.chapters;
    }
    return stringComparison(a.title, b.title);
  },
  SOURCE: (a, b) => {
    if (a.source !== b.source) {
      return stringComparison(a.source, b.source);
    }
    return stringComparison(a.title, b.title);
  },

  // TODO: I don't think I have or can easily get the data needed for these sorts
  // LAST_READ: ,
  // LAST_UPDATED: ,
});

const readFilterFuncs = unread => ({
  ALL: () => true,
  UNREAD: mangaInfo => unread[mangaInfo.id], // 0 (or null/undefined) will be false
});

const downloadedFilterFuncs = {
  ALL: () => true,
  DOWNLOADED: mangaInfo => mangaInfo.downloaded,
};

const completedFilterFuncs = {
  ALL: () => true,

  // could COMPLETED be a different variation?
  // e.g. lowercase or uppercase
  COMPLETED: mangaInfo => mangaInfo.status === 'Completed',
};

const searchFilterFunc = searchQuery => mangaInfo =>
  mangaInfo.title.toUpperCase().includes(searchQuery.toUpperCase());

type InputProps = {
  mangaLibrary: Array<MangaType>,
  flags: LibraryFlagsType,
  searchQuery: string,
  unread: { [mangaId: number]: number },
};

type OutputProps = {} & {
  mangaLibrary: Array<MangaType>,
};

/* eslint-disable react/prefer-stateless-function */
// Having a named class allows it to show up in react dev tools
function SortFilterLibraryHOC(WrappedComponent: React.ComponentType<OutputProps>): React.ComponentType<InputProps> {
  return class withSortedFilteredLibrary extends React.Component<InputProps> {
    render() {
      const {
        mangaLibrary, flags, searchQuery, unread, ...otherProps
      } = this.props;

      const {
        SORT_TYPE, READ_FILTER, DOWNLOADED_FILTER, COMPLETED_FILTER, SORT_DIRECTION,
      } = flags;

      let sortedFilteredLibrary = mangaLibrary
        .slice() // clone array // $FlowFixMe - SORT_TYPE.LAST_READ, LAST_UPDATED not implemented
        .sort(sortFuncs(unread)[SORT_TYPE])
        .filter(readFilterFuncs(unread)[READ_FILTER])
        .filter(downloadedFilterFuncs[DOWNLOADED_FILTER])
        .filter(completedFilterFuncs[COMPLETED_FILTER])
        .filter(searchFilterFunc(searchQuery));

      if (SORT_DIRECTION === 'DESCENDING') {
        sortedFilteredLibrary = sortedFilteredLibrary.reverse();
      }

      return <WrappedComponent {...otherProps} mangaLibrary={sortedFilteredLibrary} />;
    }
  };
}

export default SortFilterLibraryHOC;
