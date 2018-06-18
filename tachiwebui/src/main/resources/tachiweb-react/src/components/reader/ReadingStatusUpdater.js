// @flow
import { Component } from 'react';
import type { ReadingStatusUpdaterContainerProps } from 'containers/ReadingStatusUpdaterContainer';

// TODO: special case to consider?
//       If you are on the last page of a chapter then go to the next chapter's first page,
//       AND we didn't already mark you as having completed this chapter,
//       your reading status currently won't be updated
//
//       Could this also cause problems if a chapter only has 1 page total?

// TODO: should I bother ignoring page jumps? (within the same chapter)
//       One method of doing so is if the difference in page change isn't 1, ignore it

type Props = ReadingStatusUpdaterContainerProps;

class ReadingStatusUpdater extends Component<Props> {
  componentDidUpdate(prevProps: Props) {
    const {
      updateReadingStatus, mangaId, chapterId, page,
    } = this.props;
    const { mangaId: prevMangaId, chapterId: prevChapterId, page: prevPage } = prevProps;

    // If you're viewing the same manga + chapter, but the page
    // has changed, check if we should update your reading status
    const sameChapter = mangaId === prevMangaId && chapterId === prevChapterId;
    const pageChanged = page !== prevPage;

    if (sameChapter && pageChanged) {
      // this is letting the updateReadingStatus() method check if an update should be made
      // not sure if is the ideal way of doing it?
      updateReadingStatus(mangaId, chapterId, page);
    }
  }

  render() {
    return null;
  }
}

export default ReadingStatusUpdater;
