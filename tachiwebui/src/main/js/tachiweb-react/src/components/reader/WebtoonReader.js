// @flow
import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import ResponsiveGrid from 'components/ResponsiveGrid';
import Button from '@material-ui/core/Button';
import Icon from '@material-ui/core/Icon';
import ImageWithLoader from 'components/reader/ImageWithLoader';
import type { ChapterType } from 'types';
import { Server, Client } from 'api';
import { withRouter } from 'react-router-dom';
import Link from 'components/Link';
import Waypoint from 'react-waypoint';
import queryString from 'query-string-es5';

// Waypoints that wrap around components require special code
// However, it automatically works with normal elements like <div>
// So I'm wrapping <ImageWithLoader> with <div>
// https://github.com/brigade/react-waypoint#children

// There's no built in way to get information on what element fired Waypoint onEnter/onLeave
// need to use anonymous functions to work around this
// https://github.com/brigade/react-waypoint/issues/160

// I'm using pagesToLoad to lazy load so I don't request every page from the server at once.
// It's currently using the same number of pages to load ahead as ImagePreloader.
// From my basic testing (looking at the console Network tab), they don't seem to be interfering.

// When you change chapter, the chapterId in the URL changes.
// This triggers the next page to render, THEN componentDidUpdate() runs.
// I'm using each image's source URL as a key to determine if it should start loading.
// I was previously just using the page #, but all the images were rendering
// before componentDidUpdate() could clear pagesToLoad.

// TODO: Might want to do custom <ScrollToTop /> behavior specifically for this reader
//       or create a custom scroll-to-top component that's customizable with whatever params passed

// FIXME: (at least in dev) there seems to be some lag when the URL changes
//        Also, a possibly related minor issue where spinners will reset when page changes
//
//        I believe these are related to the component updated on URL change
//        Should be fixable using shouldComponentUpdate()

// FIXME: weird bug that I happens like 10% of the time
//        When you jump to a page, it instead shows an adjacent image...
//        Really not sure why that's happening.

// FIXME: since we position new pages to the bottom of the viewport, you can see pages above
//        and it'll load those images. This loads more content and pushes the position of your
//        image lower than it was originally.
//
//        At the time of writing this, I felt like it's too much effort for a small benefit,
//        so I'm not working on this yet.

// TODO: have some sort of interaction where you go to the next chapter if you keep scrolling down
//       sort of similar to the idea of keyboard interactions, don't rely on mouse clicks

const styles = {
  page: {
    width: '100%',
  },
  navButtonsParent: {
    display: 'flex',
    justifyContent: 'center',
    marginTop: 40,
    marginBottom: 40,
  },
  topOffset: {
    marginTop: 144,
  },
};

type Props = {
  classes: Object, // styles
  urlPrefix: string,
  mangaId: number,
  pageCount: number,
  chapter: ChapterType,
  nextChapterUrl: string,
  prevChapterUrl: string,

  // React router props
  match: Object,
  history: Object,
  location: Object,
};

type State = {
  pagesInView: Array<number>, // make sure to always keep this sorted
  pagesToLoad: Array<string>, // urls for the image, acts as a unique key
};

class WebtoonReader extends Component<Props, State> {
  state = {
    pagesInView: [],
    pagesToLoad: [],
  };

  componentDidMount() {
    window.scrollTo(0, 0);

    const { match } = this.props;

    // If you're directly loading a specific page number, jump to it
    if (match.params.page !== '0') {
      this.handlePageJump(match.params.page);
    }
  }

  // NOTE: not checking if the mangaId in the URL changed. Don't think this is a problem
  componentDidUpdate(prevProps, prevState) {
    const {
      urlPrefix, mangaId, chapter, match, history, location,
    } = this.props;

    const { pagesInView } = this.state;
    const { pagesInView: prevPagesInView } = prevState;

    // Scroll to top when the chapter changes
    const chapterChanged = match.params.chapterId !== prevProps.match.params.chapterId;
    if (chapterChanged) {
      window.scrollTo(0, 0);

      // Also reset state
      /* eslint-disable react/no-did-update-set-state */
      this.setState({ pagesInView: [], pagesToLoad: [] });
      /* eslint-enable react/no-did-update-set-state */
    }

    // If the url contains the query param '?jumpTo=true' then jump to that page
    const queryParams = queryString.parse(location.search);
    const shouldJumpToPage = queryParams.jumpTo === 'true';

    if (!chapterChanged && shouldJumpToPage) {
      this.handlePageJump(match.params.page);
    }

    // Update the URL to reflect what page the user is currently looking at
    // NOTE: It seems that if you rapidly scroll, page becomes undefined.
    //       Also, on hot-reload or debug mode reload, lastPage is undefined.
    //       ^ would cause an infinite loop when I wasn't checking if lastpage != null
    const lastPage = pagesInView[pagesInView.length - 1];
    const prevLastPage = prevPagesInView[prevPagesInView.length - 1];

    if (lastPage != null && lastPage !== prevLastPage && !shouldJumpToPage) {
      history.replace(urlPrefix + Client.page(mangaId, chapter.id, lastPage));
    }
  }

  handlePageJump = (pageId: string) => {
    const { history, location } = this.props;

    const page = document.getElementById(pageId);
    const vh = window.innerHeight;

    if (!page) return;

    // To keep consistent with the URL corresponding to the bottom most visible page
    // Keep the page you jump to as the bottom page, whether it's larger or smaller than the vh
    if (page.scrollHeight >= vh) {
      // For large images, jump to the top
      window.scrollTo(0, page.offsetTop);
    } else {
      // For smaller images, align the bottom with the bottom of the viewport
      // scrollTo a negative number seems to be fine
      const extraOffset = vh - page.scrollHeight;
      window.scrollTo(0, page.offsetTop - extraOffset);
    }

    // Clear the '?jumpTo=true' query param from the URL if it exists
    // NOTE: This is how I got around prior issues with jumping to pages in componentDidUpdate()
    if (location.search) {
      history.replace(location.pathname);
    }
  };

  pageOnEnter = (page) => {
    const numLoadAhead = 3;
    const { mangaId, chapter, pageCount } = this.props;

    this.setState((prevState) => {
      // Update pagesInView
      const pagesCopy = prevState.pagesInView.slice();
      pagesCopy.push(page);
      const newPagesInView = pagesCopy.sort();

      // Add more images that can start loading
      const newPagesToLoad = addMorePagesToLoad(
        mangaId,
        chapter.id,
        numLoadAhead,
        pageCount,
        newPagesInView,
        prevState.pagesToLoad,
      );

      return {
        pagesInView: newPagesInView,
        pagesToLoad: newPagesToLoad,
      };
    });
  };

  pageOnLeave = (page) => {
    this.setState((prevState) => {
      const { pagesInView } = prevState;
      return {
        pagesInView: pagesInView.filter(pageInView => pageInView !== page),
      };
    });
  };

  render() {
    const {
      classes, mangaId, chapter, pageCount, nextChapterUrl, prevChapterUrl,
    } = this.props;
    const { pagesToLoad } = this.state;

    const sources = createImageSrcArray(mangaId, chapter.id, pageCount);

    return (
      <React.Fragment>
        <ResponsiveGrid spacing={0} className={classes.topOffset}>
          {sources.map((source, index) => (
            <Grid item xs={12} key={source} id={index}>
              <Waypoint
                onEnter={() => this.pageOnEnter(index)}
                onLeave={() => this.pageOnLeave(index)}
              >
                <div> {/* Refer to notes on Waypoint above for why this <div> is necessary */}
                  <ImageWithLoader
                    src={pagesToLoad.includes(source) ? source : null}
                    className={classes.page}
                    alt={`${chapter.name} - Page ${index + 1}`}
                  />
                </div>
              </Waypoint>
            </Grid>
          ))}

          <Grid item xs={12} className={classes.navButtonsParent}>
            <Button component={Link} to={prevChapterUrl} disabled={!prevChapterUrl}>
              <Icon>navigate_before</Icon>
              Previous Chapter
            </Button>
            <Button component={Link} to={nextChapterUrl} disabled={!nextChapterUrl}>
              Next Chapter
              <Icon>navigate_next</Icon>
            </Button>
          </Grid>
        </ResponsiveGrid>
      </React.Fragment>
    );
  }
}

// Helper functions
function createImageSrcArray(mangaId, chapterId, pageCount) {
  const sources = [];
  for (let page = 0; page < pageCount; page += 1) {
    sources.push(Server.image(mangaId, chapterId, page));
  }
  return sources;
}

// Adds the next img sources to load to the current array of img sources to load
function addMorePagesToLoad(mangaId, chapterId, numLoadAhead, pageCount, pagesInView, oldArray) {
  if (!pagesInView.length) return oldArray; // pages can sometimes be empty if scrolling too fast

  const newPages = [];
  for (let i = 0; i < numLoadAhead + pagesInView.length; i += 1) {
    // includes the current pages just to be safe
    if (pagesInView[0] + i < pageCount) {
      newPages.push(Server.image(mangaId, chapterId, pagesInView[0] + i));
    }
  }

  const arrayCopy = oldArray.slice();
  arrayCopy.push(...newPages);

  return [...new Set(arrayCopy)]; // unique values only
}

export default withRouter(withStyles(styles)(WebtoonReader));
