// @flow
import { connect } from 'react-redux';
import { fetchMangaInfo } from 'redux-ducks/mangaInfos';
import { fetchChapters } from 'redux-ducks/chapters';
import { fetchPageCount } from 'redux-ducks/pageCounts';
import Reader from 'pages/Reader';
import type { MangaType, ChapterType } from 'types';

type StateToProps = {
  mangaInfo: ?MangaType,
  chapters: Array<ChapterType>,
  chapter: ?ChapterType,
  chapterId: number, // never null because it's pulled from the URL
  pageCounts: { [chapterId: number]: number },
  pageCount: number, // TODO: is this correct?
  page: number, // never null because it's pulled from the URL
  prevChapterId: ?number,
  nextChapterId: ?number,
  urlPrefix: string, // prepend to all urls in Reader (bit of a hack)
};

const mapStateToProps = (state, ownProps): StateToProps => {
  const { mangaInfos, chapters, pageCounts } = state;
  const { mangaId, chapterId, page } = ownProps.match.params;
  const { urlPrefix } = ownProps;

  return {
    mangaInfo: mangaInfos[mangaId],
    chapters: chapters[mangaId] || [],
    chapter: findChapter(chapters[mangaId], chapterId),
    chapterId: parseInt(chapterId, 10),
    pageCounts,
    pageCount: pageCounts[chapterId] || 0,
    page: parseInt(page, 10),
    prevChapterId: getPrevChapterId(chapters[mangaId], chapterId),
    nextChapterId: getNextChapterId(chapters[mangaId], chapterId),
    urlPrefix: urlPrefix || '',
  };
};

type DispatchToProps = {
  fetchMangaInfo: Function,
  fetchChapters: Function,
  fetchPageCount: Function,
};

const mapDispatchToProps = (dispatch, ownProps): DispatchToProps => {
  const { mangaId } = ownProps.match.params;

  return {
    fetchMangaInfo: () => dispatch(fetchMangaInfo(mangaId)),
    fetchChapters: () => dispatch(fetchChapters(mangaId)),
    fetchPageCount: chapterId => dispatch(fetchPageCount(mangaId, chapterId)),
  };
};

// Helper functions
function findChapter(chapters: Array<ChapterType>, chapterId: number): ?ChapterType {
  if (!chapters || chapters.length === 0) return null;

  return chapters.find(chapter => chapter.id === parseInt(chapterId, 10));
}

function findChapterIndex(chapters: Array<ChapterType>, thisChapterId: number): number {
  // If not found, returns -1. BUT this shouldn't ever happen.
  return chapters.findIndex(chapter => chapter.id === parseInt(thisChapterId, 10));
}

function getPrevChapterId(chapters: Array<ChapterType>, thisChapterId: number): ?number {
  if (!chapters) return null;

  const thisChapterIndex: number = findChapterIndex(chapters, thisChapterId);
  if (thisChapterIndex === 0) {
    return null;
  }
  return chapters[thisChapterIndex - 1].id;
}

function getNextChapterId(chapters: Array<ChapterType>, thisChapterId: number): ?number {
  if (!chapters) return null;

  const thisChapterIndex: number = findChapterIndex(chapters, thisChapterId);
  if (thisChapterIndex === chapters.length - 1) {
    return null;
  }
  return chapters[thisChapterIndex + 1].id;
}

export type ReaderContainerProps = StateToProps & DispatchToProps;
export default connect(mapStateToProps, mapDispatchToProps)(Reader);
