// @flow
import { connect } from 'react-redux';
import ImagePreloader from 'components/reader/ImagePreloader';
import { withRouter } from 'react-router-dom';
import type { ChapterType } from 'types';

type StateToProps = {
  mangaId: number,
  chapterId: number,
  page: number,
  pageCount: number,
  nextChapterId: ?number,
};

const mapStateToProps = (state, ownProps): StateToProps => {
  const { chapters, pageCounts } = state;
  const { mangaId, chapterId, page } = ownProps.match.params;

  return {
    mangaId: parseInt(mangaId, 10),
    chapterId: parseInt(chapterId, 10),
    page: parseInt(page, 10),
    pageCount: pageCounts[chapterId] || 0,
    nextChapterId: getNextChapterId(chapters[mangaId], chapterId),
  };
};

// Helper functions

// TODO: copied getNextChapterId() and findChapterIndex() from ReaderContainer,
//       consider moving this to a shared util file
function getNextChapterId(chapters: Array<ChapterType>, thisChapterId: number): ?number {
  if (!chapters) return null;

  const thisChapterIndex: number = findChapterIndex(chapters, thisChapterId);
  if (thisChapterIndex === chapters.length - 1) {
    return null;
  }
  return chapters[thisChapterIndex + 1].id;
}

function findChapterIndex(chapters: Array<ChapterType>, thisChapterId: number): number {
  // If not found, returns -1. BUT this shouldn't ever happen.
  return chapters.findIndex(chapter => chapter.id === parseInt(thisChapterId, 10));
}

export type ImagePreloaderContainerProps = StateToProps;
export default withRouter(connect(mapStateToProps)(ImagePreloader));
