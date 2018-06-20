// @flow
import React, { Component } from 'react';
import LibraryHeader from 'components/library/LibraryHeader';
import MangaGrid from 'components/MangaGrid';
import LibraryMangaCard from 'components/library/LibraryMangaCard';
import FullScreenLoading from 'components/loading/FullScreenLoading';
import type { LibraryContainerProps } from 'containers/LibraryContainer';
import SortFilterLibraryHOC from 'components/library/SortFilterLibraryHOC';
import { Helmet } from 'react-helmet';

// TODO: sort/filter mangaLibrary

// TODO: no feedback of success/errors after clicking the library update button

// FIXME: LibraryMangaCard - unread badge is positioning too far right. This is causing
//        library to have an x-overflow

// NOTE: unread count relies on the server knowing how many chapters there are
//       If for some reason the server hasn't scraped a list of chapters, this number won't appear

type State = { searchQuery: string };

class Library extends Component<LibraryContainerProps, State> {
  state = { searchQuery: '' };

  componentDidMount() {
    this.props.fetchLibrary();
    this.props.fetchUnread();
  }

  handleRefreshClick = () => {
    const {
      mangaLibrary, updateChapters, fetchLibrary, fetchUnread,
    } = this.props;

    // Create an array of promise functions
    // Since calling updateChapters runs the function, create an intermediate function
    const updateChapterPromises = mangaLibrary.map(mangaInfo => () => updateChapters(mangaInfo.id));

    serialPromiseChain(updateChapterPromises)
      .then(() => fetchLibrary({ ignoreCache: true }))
      .then(() => fetchUnread({ ignoreCache: true }));
  };

  handleSearchChange = (newSearchQuery: string) => {
    this.setState({ searchQuery: newSearchQuery });
  };

  render() {
    const {
      mangaLibrary, unread, flags, setLibraryFlag, libraryIsLoading, chaptersAreUpdating,
    } = this.props;
    const { searchQuery } = this.state;

    const SortFilterLibrary = SortFilterLibraryHOC(MangaGrid);

    return (
      <React.Fragment>
        <Helmet>
          <title>Library - TachiWeb</title>
        </Helmet>

        <LibraryHeader
          searchQuery={searchQuery}
          flags={flags}
          onSearchChange={this.handleSearchChange}
          onRefreshClick={this.handleRefreshClick}
          setLibraryFlag={setLibraryFlag}
        />

        <SortFilterLibrary
          mangaLibrary={mangaLibrary}
          flags={flags}
          searchQuery={searchQuery}
          unread={unread}
          cardComponent={<LibraryMangaCard unread={unread} />}
        />

        {(libraryIsLoading || chaptersAreUpdating) && <FullScreenLoading />}
      </React.Fragment>
    );
  }
}

// Helper Functions
// https://decembersoft.com/posts/promises-in-serial-with-array-reduce/
function serialPromiseChain(promiseArray) {
  return promiseArray.reduce(
    (promiseChain, currentPromise) => promiseChain.then(() => currentPromise()),
    Promise.resolve([]),
  );
}

export default Library;
