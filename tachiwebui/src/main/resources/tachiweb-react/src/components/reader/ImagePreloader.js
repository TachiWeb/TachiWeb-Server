// @flow
import { Component } from 'react';
import type { ImagePreloaderContainerProps } from 'containers/ImagePreloaderContainer';
import { Server } from 'api';

// This expects whatever component is using it to load pageCount for the current chapter

// TODO: Do we want to preload one behind as well?
//       Loading from only this chapter would be trivial.
//
//       However, if you want to load from the previous chapter, it would
//       require the previous chapter object and prev chapter pageCount.

// https://www.photo-mark.com/notes/image-preloading/
// https://stackoverflow.com/questions/1787319/preload-hidden-css-images?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

type Props = ImagePreloaderContainerProps;

class ImagePreloader extends Component<Props> {
  componentDidMount() {
    this.preloadImages();
  }

  componentDidUpdate(prevProps: Props) {
    const { mangaId, chapterId, page } = this.props;

    const mangaChanged = mangaId !== prevProps.mangaId;
    const chapterChanged = chapterId !== prevProps.chapterId;
    const pageChanged = page !== prevProps.page;

    if (mangaChanged || chapterChanged || pageChanged) {
      this.preloadImages();
    }
  }

  preloadImages = () => {
    const {
      mangaId, chapterId, page, pageCount, nextChapterId,
    } = this.props;
    const numPreloadAhead = 3;

    for (let i = 1; i <= numPreloadAhead; i += 1) {
      // Chrome only seems to preload if a new image object is used every time
      const image = new Image();

      if (page + i < pageCount) {
        // Load pages from this chapter
        image.src = Server.image(mangaId, chapterId, page + i);
      } else if (nextChapterId) {
        // Load pages from next chapter
        // NOTE: Not bothering to check next chapter's pageCount. Doubt this will be a problem.
        /* eslint-disable no-mixed-operators */
        image.src = Server.image(mangaId, nextChapterId, page + i - pageCount);
      }
    }
  };

  render() {
    return null;
  }
}

export default ImagePreloader;
