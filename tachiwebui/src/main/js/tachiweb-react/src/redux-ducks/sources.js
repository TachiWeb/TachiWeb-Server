// @flow
import { Server } from 'api';
import type { SourceType } from 'types';
import { handleHTMLError } from './utils';

// ================================================================================
// Actions
// ================================================================================
const FETCH_REQUEST = 'sources/FETCH_REQUEST';
const FETCH_SUCCESS = 'sources/FETCH_SUCCESS';
const FETCH_FAILURE = 'sources/FETCH_FAILURE';
export const FETCH_SOURCES = 'sources/FETCH';

// ================================================================================
// Reducers
// ================================================================================
type State = $ReadOnlyArray<SourceType>;

export default function sourcesReducer(state: State = [], action = {}) {
  switch (action.type) {
    case FETCH_SUCCESS:
      return action.payload;
    default:
      return state;
  }
}

// ================================================================================
// Action Creators
// ================================================================================
export function fetchSources() {
  return (dispatch: Function) => {
    dispatch({ type: FETCH_REQUEST });

    return fetch(Server.sources())
      .then(handleHTMLError)
      .then(
        json => dispatch({ type: FETCH_SUCCESS, payload: json.content }),
        error =>
          dispatch({
            type: FETCH_FAILURE,
            errorMessage: 'Failed to load sources',
            meta: { error },
          }),
      );
  };
}
