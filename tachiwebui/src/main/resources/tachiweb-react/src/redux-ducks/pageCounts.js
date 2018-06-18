// @flow
import { Server } from 'api';
import { handleHTMLError } from './utils';

// ================================================================================
// Actions
// ================================================================================
const FETCH_REQUEST = 'pageCounts/FETCH_REQUEST';
const FETCH_SUCCESS = 'pageCounts/FETCH_SUCCESS';
const FETCH_FAILURE = 'pageCounts/FETCH_FAILURE';
const FETCH_CACHE = 'pageCounts/FETCH_CACHE';

// ================================================================================
// Reducers
// ================================================================================
type State = { +[chapterId: number]: number };

export default function pageCountsReducer(state: State = {}, action = {}) {
  switch (action.type) {
    case FETCH_SUCCESS:
      return {
        ...state,
        [action.chapterId]: action.pageCount,
      };
    case FETCH_CACHE:
      return state;
    default:
      return state;
  }
}

// ================================================================================
// Action Creators
// ================================================================================
export function fetchPageCount(mangaId: number, chapterId: number) {
  return (dispatch: Function, getState: Function) => {
    // Return manga's chapters' cached pageCount data if they're already in the store
    if (getState().pageCounts[chapterId]) {
      return dispatch({ type: FETCH_CACHE });
    }

    dispatch({ type: FETCH_REQUEST });

    return fetch(Server.pageCount(mangaId, chapterId))
      .then(handleHTMLError)
      .then(
        json =>
          dispatch({
            type: FETCH_SUCCESS,
            chapterId,
            pageCount: json.page_count,
          }),
        error =>
          dispatch({
            type: FETCH_FAILURE,
            errorMessage: 'Failed to get page count',
            meta: { error },
          }),
      );
  };
}
