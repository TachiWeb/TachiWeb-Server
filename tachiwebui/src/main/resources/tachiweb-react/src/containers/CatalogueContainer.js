// @flow
import { connect } from 'react-redux';
import { fetchSources, FETCH_SOURCES } from 'redux-ducks/sources';
import {
  fetchCatalogue,
  fetchNextCataloguePage,
  resetCatalogue,
  updateSearchQuery,
  changeSourceId,
  FETCH_CATALOGUE,
  CATALOGUE_ADD_PAGE,
} from 'redux-ducks/catalogue';
import {
  fetchChapters,
  updateChapters,
  FETCH_CHAPTERS,
  UPDATE_CHAPTERS,
} from 'redux-ducks/chapters';
import {
  fetchFilters,
  resetFilters,
  updateLastUsedFilters,
  updateCurrentFilters,
} from 'redux-ducks/filters';
import { fetchMangaInfo, updateMangaInfo, FETCH_MANGA, UPDATE_MANGA } from 'redux-ducks/mangaInfos';
import Catalogue from 'pages/Catalogue';
import { createLoadingSelector } from 'redux-ducks/loading';
import type { SourceType, ChapterType, MangaType } from 'types';
import type { FilterAnyType } from 'types/filters';

const sourcesAreLoading: Function = createLoadingSelector([FETCH_SOURCES]);
const catalogueIsLoading: Function = createLoadingSelector([FETCH_CATALOGUE, CATALOGUE_ADD_PAGE]);
const mangaInfoIsLoading: Function = createLoadingSelector([
  FETCH_MANGA,
  UPDATE_MANGA,
  FETCH_CHAPTERS,
  UPDATE_CHAPTERS,
]);

type StateToProps = {
  sources: Array<SourceType>,

  hasNextPage: boolean,
  searchQuery: string,
  sourceId: ?number,

  chaptersByMangaId: { [mangaId: number]: Array<ChapterType> },
  mangaLibrary: Array<MangaType>,

  initialFilters: Array<FilterAnyType>,
  lastUsedFilters: Array<FilterAnyType>,
  currentFilters: Array<FilterAnyType>,

  sourcesAreLoading: boolean,
  catalogueIsLoading: boolean,
  mangaInfoIsLoading: boolean,
};

const mapStateToProps = (state): StateToProps => {
  const {
    mangaIds, hasNextPage, searchQuery, sourceId,
  } = state.catalogue;
  const mangaLibrary = mangaToShow(state.mangaInfos, mangaIds);

  return {
    // Sources props
    sources: state.sources,
    // Catalogue props
    hasNextPage,
    searchQuery,
    sourceId,
    // Chapter props
    chaptersByMangaId: state.chapters,
    // Library props
    mangaLibrary,
    // Filter props
    initialFilters: state.filters.initialFilters,
    lastUsedFilters: state.filters.lastUsedFilters,
    currentFilters: state.filters.currentFilters,
    // Fetching props
    sourcesAreLoading: sourcesAreLoading(state),
    catalogueIsLoading: catalogueIsLoading(state),
    mangaInfoIsLoading: mangaInfoIsLoading(state),
  };
};

type DispatchToProps = {
  fetchSources: Function,
  fetchCatalogue: Function,
  fetchNextCataloguePage: Function,
  resetCatalogue: Function,
  updateSearchQuery: Function,
  changeSourceId: Function,

  fetchChapters: Function,
  updateChapters: Function,
  updateMangaInfo: Function,
  fetchMangaInfo: Function,

  fetchFilters: Function,
  resetFilters: Function,
  updateLastUsedFilters: Function,
  updateCurrentFilters: Function,
};

const mapDispatchToProps = (dispatch): DispatchToProps => ({
  fetchSources: () => dispatch(fetchSources()),
  // Passing in the new catalogue search settings
  fetchCatalogue: () => dispatch(fetchCatalogue()),
  fetchNextCataloguePage: () => dispatch(fetchNextCataloguePage()),
  resetCatalogue: () => dispatch(resetCatalogue()),
  updateSearchQuery: newSearchQuery => dispatch(updateSearchQuery(newSearchQuery)),
  changeSourceId: newSourceId => dispatch(changeSourceId(newSourceId)),

  fetchChapters: mangaId => dispatch(fetchChapters(mangaId)),
  updateChapters: mangaId => dispatch(updateChapters(mangaId)),
  updateMangaInfo: mangaId => dispatch(updateMangaInfo(mangaId)),
  fetchMangaInfo: mangaId => dispatch(fetchMangaInfo(mangaId)),

  fetchFilters: () => dispatch(fetchFilters()),
  resetFilters: () => dispatch(resetFilters()),
  updateLastUsedFilters: () => dispatch(updateLastUsedFilters()),
  updateCurrentFilters: newFilters => dispatch(updateCurrentFilters(newFilters)),
});

// Helper functions
function mangaToShow(mangaLibrary, mangaIds) {
  return mangaIds.map(mangaId => mangaLibrary[mangaId]);
}

export type CatalogueContainerProps = StateToProps & DispatchToProps;
export default connect(mapStateToProps, mapDispatchToProps)(Catalogue);
