// @flow
import { Server } from 'api';
import type { FilterAnyType } from 'types/filters';
import { handleHTMLError } from './utils';

// ================================================================================
// Actions
// ================================================================================
const FETCH_REQUEST = 'filters/FETCH_REQUEST';
const FETCH_SUCCESS = 'filters/FETCH_SUCCESS';
const FETCH_FAILURE = 'filters/FETCH_FAILURE';

const RESET_FILTERS = 'filters/RESET_FILTERS';
const UPDATE_LAST_USED_FILTERS = 'filters/UPDATE_LAST_USED_FILTERS';
const UPDATE_CURRENT_FILTERS = 'filters/UPDATE_CURRENT_FILTERS';

// ================================================================================
// Reducers
// ================================================================================
type State = {
  +initialFilters: $ReadOnlyArray<FilterAnyType>,
  +lastUsedFilters: $ReadOnlyArray<FilterAnyType>, // use this for the actual search fetches

  // having this in the redux store is going to create a ton of actions being logged
  // the benefit is that any un-searched changes will remain when you leave and return to catalogue
  +currentFilters: $ReadOnlyArray<FilterAnyType>, // stores changes that haven't been submitted yet
};
const initialState: State = {
  initialFilters: [],
  lastUsedFilters: [],
  currentFilters: [],
};
export default function filtersReducer(state: State = initialState, action = {}) {
  switch (action.type) {
    case FETCH_REQUEST:
      return initialState;

    case FETCH_SUCCESS:
      return {
        initialFilters: action.filters,
        lastUsedFilters: action.filters,
        currentFilters: action.filters,
      };

    case RESET_FILTERS:
      // This is specifically for what data in the UI the user is seeing/using
      return { ...state, currentFilters: state.initialFilters };

    case UPDATE_LAST_USED_FILTERS:
      // record the current filters as what was last used to search
      return { ...state, lastUsedFilters: state.currentFilters };

    case UPDATE_CURRENT_FILTERS:
      return { ...state, currentFilters: action.filters };

    default:
      return state;
  }
}

// ================================================================================
// Action Creators
// ================================================================================
export function fetchFilters() {
  return (dispatch: Function, getState: Function) => {
    const { sourceId }: { sourceId: ?number } = getState().catalogue;
    dispatch({ type: FETCH_REQUEST, meta: { sourceId } });

    if (sourceId == null) {
      return dispatch({
        type: FETCH_FAILURE,
        errorMessage: 'Failed to get the filters.',
        meta: 'fetchFilters() sourceId is null',
      });
    }

    return fetch(Server.filters(sourceId))
      .then(handleHTMLError)
      .then(
        json => dispatch({ type: FETCH_SUCCESS, filters: json.content }),
        error =>
          dispatch({
            type: FETCH_FAILURE,
            errorMessage: 'Failed to get the filters for this source',
            meta: { error },
          }),
      );
  };
}

export function resetFilters() {
  return (dispatch: Function) => dispatch({ type: RESET_FILTERS });
}

export function updateLastUsedFilters() {
  return (dispatch: Function) => dispatch({ type: UPDATE_LAST_USED_FILTERS });
}

export function updateCurrentFilters(filters: Array<FilterAnyType>) {
  return (dispatch: Function) => dispatch({ type: UPDATE_CURRENT_FILTERS, filters });
}
