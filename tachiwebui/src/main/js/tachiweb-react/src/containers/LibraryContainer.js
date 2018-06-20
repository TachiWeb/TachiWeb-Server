// @flow
import { connect } from 'react-redux';
import {
  fetchLibrary,
  fetchUnread,
  setLibraryFlag,
  FETCH_LIBRARY,
  FETCH_UNREAD,
} from 'redux-ducks/library';
import Library from 'pages/Library';
import { createLoadingSelector } from 'redux-ducks/loading';
import type { MangaType, LibraryFlagsType } from 'types';
import { updateChapters, UPDATE_CHAPTERS, FETCH_CHAPTERS } from 'redux-ducks/chapters';

const libraryIsLoading = createLoadingSelector([FETCH_LIBRARY, FETCH_UNREAD]);
const chaptersAreUpdating = createLoadingSelector([UPDATE_CHAPTERS, FETCH_CHAPTERS]);

type StateToProps = {
  mangaLibrary: Array<MangaType>,
  unread: { [mangaId: number]: number },
  libraryIsLoading: boolean,
  chaptersAreUpdating: boolean,
  flags: LibraryFlagsType,
};

const mapStateToProps = state =>
  ({
    mangaLibrary: getMangaLibrary(state.mangaInfos, state.library.mangaIds),
    unread: state.library.unread,
    flags: state.library.flags,
    libraryIsLoading: libraryIsLoading(state),
    chaptersAreUpdating: chaptersAreUpdating(state),
  }: StateToProps);

type DispatchToProps = {
  fetchLibrary: Function,
  fetchUnread: Function,
  updateChapters: Function,
  setLibraryFlag: Function,
};

const mapDispatchToProps = (dispatch): DispatchToProps => ({
  fetchLibrary: options => dispatch(fetchLibrary(options)),
  fetchUnread: options => dispatch(fetchUnread(options)),
  updateChapters: mangaId => dispatch(updateChapters(mangaId)),
  setLibraryFlag: (flag, state) => dispatch(setLibraryFlag(flag, state)),
});

// Helper Functions
function getMangaLibrary(mangaInfos, mangaIds): Array<MangaType> {
  return mangaIds.map(mangaId => mangaInfos[mangaId]);
}

export type LibraryContainerProps = StateToProps & DispatchToProps;
export default connect(mapStateToProps, mapDispatchToProps)(Library);
