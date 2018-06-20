// @flow
import * as React from 'react';
import type { MangaType, ChapterType } from 'types';

const sortFuncs = {
  SOURCE: (a, b) => b.source_order - a.source_order,
  NUMBER: (a, b) => a.chapter_number - b.chapter_number,
};

// READ shows chapters you've completed, UNREAD shows uncompleted chapters
const readFilterFuncs = {
  ALL: () => true,
  READ: chapter => chapter.read,
  UNREAD: chapter => !chapter.read,
};

const downloadedFilterFuncs = {
  ALL: () => true,
  DOWNLOADED: chapter => chapter.download_status === 'DOWNLOADED',
  NOT_DOWNLOADED: chapter => chapter.download_status === 'NOT_DOWNLOADED', // unused
};

type Props = {
  mangaInfo: MangaType,
  chapters: Array<ChapterType>,
};

/* eslint-disable react/prefer-stateless-function */
// Having a named class allows it to show up in react dev tools
function SortFilterChaptersHOC(WrappedComponent: React.ComponentType<Props>): React.ComponentType<Props> {
  return class withSortedFilteredChapters extends React.Component<Props> {
    render() {
      const { mangaInfo, chapters, ...otherProps } = this.props;
      const {
        SORT_TYPE, READ_FILTER, DOWNLOADED_FILTER, SORT_DIRECTION,
      } = mangaInfo.flags;

      let sortedFilteredChapters = chapters
        .slice() // clone array
        .sort(sortFuncs[SORT_TYPE])
        .filter(readFilterFuncs[READ_FILTER])
        .filter(downloadedFilterFuncs[DOWNLOADED_FILTER]);

      // The manga chapters naturally come in ascending order
      if (SORT_DIRECTION === 'DESCENDING') {
        sortedFilteredChapters = sortedFilteredChapters.reverse();
      }

      return (
        <WrappedComponent {...otherProps} mangaInfo={mangaInfo} chapters={sortedFilteredChapters} />
      );
    }
  };
}

export default SortFilterChaptersHOC;
