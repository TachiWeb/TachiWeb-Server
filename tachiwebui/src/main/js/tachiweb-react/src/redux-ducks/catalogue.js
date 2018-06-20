// @flow
import { Server } from 'api';
import type { FilterAnyType } from 'types/filters';
import { ADD_MANGA } from './mangaInfos';
import { handleHTMLError, transformToMangaIdsArray } from './utils';

// ================================================================================
// Actions
// ================================================================================
const RESET_STATE = 'catalogue/RESET_STATE';

const FETCH_CATALOGUE_REQUEST = 'catalogue/FETCH_REQUEST';
const FETCH_CATALOGUE_SUCCESS = 'catalogue/FETCH_SUCCESS';
const FETCH_CATALOGUE_FAILURE = 'catalogue/FETCH_FAILURE';
export const FETCH_CATALOGUE = 'catalogue/FETCH';

const ADD_PAGE_NO_NEXT_PAGE = 'catalogue/ADD_PAGE_NO_NEXT_PAGE'; // failsafe, don't use
const ADD_PAGE_REQUEST = 'catalogue/ADD_PAGE_REQUEST';
const ADD_PAGE_SUCCESS = 'catalogue/ADD_PAGE_SUCCESS';
const ADD_PAGE_FAILURE = 'catalogue/ADD_PAGE_FAILURE';
export const CATALOGUE_ADD_PAGE = 'catalogue/ADD_PAGE';

const UPDATE_SEARCH_QUERY = 'catalogue/UPDATE_SEARCH_QUERY';

const CHANGE_SOURCEID = 'catalogue/CHANGE_SOURCEID';

// ================================================================================
// Reducers
// ================================================================================
type State = {
  +sourceId: ?number,
  +mangaIds: $ReadOnlyArray<number>,
  +page: number,
  +hasNextPage: boolean,
  +searchQuery: string,
};

const initialState = {
  sourceId: null,
  mangaIds: [], // array of mangaIds that point that data loaded in mangaInfos reducer
  page: 1,
  hasNextPage: false,
  searchQuery: '',
};

export default function catalogueReducer(state: State = initialState, action = {}) {
  switch (action.type) {
    case RESET_STATE:
      return initialState;

    case FETCH_CATALOGUE_REQUEST:
      return { ...state, mangaIds: [] }; // also clear manga shown when loading

    case FETCH_CATALOGUE_SUCCESS: {
      const { mangaIds, hasNextPage } = action;
      return {
        ...state,
        mangaIds,
        hasNextPage,
      };
    }

    case ADD_PAGE_SUCCESS: {
      const { mangaIds, page, hasNextPage } = action;
      return {
        ...state,
        // some sources send duplicate results for some reason, so only add unique values
        mangaIds: addUnique(state.mangaIds, mangaIds),
        page,
        hasNextPage,
      };
    }

    case UPDATE_SEARCH_QUERY:
      return { ...state, searchQuery: action.searchQuery };

    case CHANGE_SOURCEID:
      return { ...state, sourceId: action.newSourceId };

    default:
      return state;
  }
}

// ================================================================================
// Action Creators
// ================================================================================
export function fetchCatalogue() {
  return (dispatch: Function, getState: Function) => {
    const { lastUsedFilters } = getState().filters;
    const { searchQuery, sourceId } = getState().catalogue;

    dispatch({
      type: FETCH_CATALOGUE_REQUEST,
      meta: { sourceId, searchQuery, lastUsedFilters },
    });

    if (sourceId == null) {
      return dispatch({
        type: FETCH_CATALOGUE_FAILURE,
        errorMessage: 'No source selected',
        meta: 'fetchCatalogue() sourceId is null',
      });
    }

    // Filters should be null if empty when requesting from the server
    const filtersChecked = lastUsedFilters.length ? lastUsedFilters : null;
    return fetch(
      Server.catalogue(),
      cataloguePostParameters(1, sourceId, searchQuery.trim(), filtersChecked),
    )
      .then(handleHTMLError)
      .then(
        (json) => {
          const { content, has_next: hasNextPage } = json;
          const mangaIds = transformToMangaIdsArray(content);

          dispatch({ type: ADD_MANGA, newManga: content });
          dispatch({
            type: FETCH_CATALOGUE_SUCCESS,
            mangaIds,
            page: 1,
            hasNextPage,
          });
        },
        error =>
          dispatch({
            type: FETCH_CATALOGUE_FAILURE,
            errorMessage: 'Failed to load this catalogue',
            meta: { error },
          }),
      );
  };
}

export function fetchNextCataloguePage() {
  return (dispatch: Function, getState: Function) => {
    const {
      page, hasNextPage, searchQuery, sourceId,
    } = getState().catalogue;
    const { lastUsedFilters } = getState().filters;
    const nextPage = page + 1;

    if (!hasNextPage) {
      // Failsafe - don't actually call this function if there is no next page
      return dispatch({ type: ADD_PAGE_NO_NEXT_PAGE });
    }
    if (sourceId == null) {
      return dispatch({
        type: ADD_PAGE_FAILURE,
        errorMessage: 'There was a problem loading the next page of manga',
        meta: 'fetchNextCataloguePage() sourceId is null',
      });
    }

    dispatch({
      type: ADD_PAGE_REQUEST,
      meta: {
        sourceId,
        nextPage,
        hasNextPage,
        searchQuery,
        lastUsedFilters,
      },
    });

    const filtersChecked = lastUsedFilters.length ? lastUsedFilters : null;
    return fetch(
      Server.catalogue(),
      cataloguePostParameters(nextPage, sourceId, searchQuery.trim(), filtersChecked),
    )
      .then(handleHTMLError)
      .then(
        (json) => {
          const { content, has_next: hasNextPageUpdated } = json;
          const mangaIds = transformToMangaIdsArray(content);

          dispatch({ type: ADD_MANGA, newManga: content });
          dispatch({
            type: ADD_PAGE_SUCCESS,
            mangaIds,
            page: nextPage,
            hasNextPage: hasNextPageUpdated,
          });
        },
        error =>
          dispatch({
            type: ADD_PAGE_FAILURE,
            errorMessage: 'There was a problem loading the next page of manga',
            meta: { error },
          }),
      );
  };
}

export function resetCatalogue() {
  return (dispatch: Function) => dispatch({ type: RESET_STATE });
}

export function updateSearchQuery(newSearchQuery: string) {
  return (dispatch: Function) =>
    dispatch({ type: UPDATE_SEARCH_QUERY, searchQuery: newSearchQuery });
}

export function changeSourceId(newSourceId: number) {
  return (dispatch: Function) => dispatch({ type: CHANGE_SOURCEID, newSourceId });
}

// ================================================================================
// Helper Functions
// ================================================================================
function cataloguePostParameters(
  page: number,
  sourceId: number,
  query: string,
  filters: ?Array<FilterAnyType>,
): Object {
  return {
    method: 'POST',
    body: JSON.stringify({
      page,
      sourceId,
      query,
      filters,
    }),
    headers: new Headers({
      'Content-Type': 'application/json',
    }),
  };
}

function addUnique(oldArray, newArray) {
  const newUniques = newArray.filter(val => !oldArray.includes(val));
  return [...oldArray, ...newUniques];
}
