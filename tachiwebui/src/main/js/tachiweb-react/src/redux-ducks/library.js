// @flow
import { Server } from 'api';
import type { LibraryFlagsType } from 'types';
import { ADD_MANGA } from './mangaInfos';
import { handleHTMLError, transformToMangaIdsArray } from './utils';

// ================================================================================
// Actions
// ================================================================================
const FETCH_LIBRARY_REQUEST = 'library/FETCH_REQUEST';
const FETCH_LIBRARY_SUCCESS = 'library/FETCH_SUCCESS';
const FETCH_LIBRARY_FAILURE = 'library/FETCH_FAILURE';
const FETCH_LIBRARY_CACHE = 'library/FETCH_CACHE';
export const FETCH_LIBRARY = 'library/FETCH';

const FETCH_UNREAD_REQUEST = 'library/FETCH_UNREAD_REQUEST';
const FETCH_UNREAD_SUCCESS = 'library/FETCH_UNREAD_SUCCESS';
const FETCH_UNREAD_FAILURE = 'library/FETCH_UNREAD_FAILURE';
const FETCH_UNREAD_CACHE = 'library/FETCH_UNREAD_CACHE';
export const FETCH_UNREAD = 'library/FETCH_UNREAD';

export const ADD_TO_FAVORITES = 'library/ADD_TO_FAVORITES';
export const REMOVE_FROM_FAVORITES = 'library/REMOVE_FROM_FAVORITES';
export const DECREMENT_UNREAD = 'library/DECREMENT_UNREAD';

const UPLOAD_RESTORE_REQUEST = 'library/UPLOAD_RESTORE_REQUEST';
const UPLOAD_RESTORE_SUCCESS = 'library/UPLOAD_RESTORE_SUCCESS';
const UPLOAD_RESTORE_FAILURE = 'library/UPLOAD_RESTORE_FAILURE';
export const UPLOAD_RESTORE = 'library/UPLOAD_RESTORE';

const SET_FLAG_REQUEST = 'library/SET_FLAG_REQUEST';
const SET_FLAG_SUCCESS = 'library/SET_FLAG_SUCCESS';
const SET_FLAG_FAILURE = 'library/SET_FLAG_FAILURE';
const SET_FLAG_NO_CHANGE = 'library/SET_FLAG_NO_CHANGE';

// ================================================================================
// Reducers
// ================================================================================
type State = {
  +mangaIds: $ReadOnlyArray<number>,
  +libraryLoaded: boolean,
  +unread: { +[mangaId: number]: number },
  +reloadUnread: boolean,
  +flags: LibraryFlagsType,
};

export default function libraryReducer(
  state: State = {
    mangaIds: [], // array of mangaIds that point that data loaded in mangaInfos reducer
    libraryLoaded: false, // Library should be loaded once on first visit
    unread: {}, // { mangaId: int }
    reloadUnread: true, // should refresh unread for library if something new is added
    flags: {
      DOWNLOADED_FILTER: 'ALL',
      READ_FILTER: 'ALL',
      COMPLETED_FILTER: 'ALL',
      SORT_TYPE: 'ALPHABETICALLY',
      SORT_DIRECTION: 'ASCENDING',
    },
  },
  action = {},
) {
  switch (action.type) {
    case FETCH_LIBRARY_SUCCESS:
      return {
        ...state,
        mangaIds: action.mangaIds,
        libraryLoaded: true,
      };

    case FETCH_LIBRARY_CACHE:
      return state;

    case FETCH_UNREAD_SUCCESS:
      return {
        ...state,
        unread: action.unread,
        reloadUnread: false,
      };

    case FETCH_UNREAD_CACHE:
      return state;

    case ADD_TO_FAVORITES:
      return {
        ...state,
        mangaIds: [...state.mangaIds, action.mangaId],
        reloadUnread: true,
      };

    case REMOVE_FROM_FAVORITES: {
      const newMangaIds: Array<number> = state.mangaIds.filter(mangaId => mangaId !== action.mangaId);
      return {
        ...state,
        mangaIds: newMangaIds,
      };
    }
    case DECREMENT_UNREAD: {
      const { unread } = state;
      const { mangaId } = action;
      return {
        ...state,
        unread: {
          ...unread,
          [mangaId]: unread[mangaId] - 1,
        },
      };
    }

    case SET_FLAG_REQUEST:
      return {
        ...state,
        flags: {
          ...state.flags,
          [action.flag]: action.state,
        },
      };

    default:
      return state;
  }
}

// ================================================================================
// Action Creators
// ================================================================================
type Options = { ignoreCache?: boolean };
export function fetchLibrary({ ignoreCache = false }: Options = {}) {
  return (dispatch: Function, getState: Function) => {
    // Return cached mangaLibrary if it's been loaded before
    if (!ignoreCache && getState().library.libraryLoaded) {
      return dispatch({ type: FETCH_LIBRARY_CACHE });
    }

    dispatch({ type: FETCH_LIBRARY_REQUEST });

    return fetch(Server.library())
      .then(handleHTMLError)
      .then(
        (json) => {
          const { content } = json;
          const mangaIds = transformToMangaIdsArray(content);

          dispatch({ type: ADD_MANGA, newManga: content });
          dispatch({ type: FETCH_LIBRARY_SUCCESS, mangaIds });
        },
        error =>
          dispatch({
            type: FETCH_LIBRARY_FAILURE,
            errorMessage: 'Failed to load your library',
            meta: { error },
          }),
      );
  };
}

export function fetchUnread({ ignoreCache = false }: Options = {}) {
  return (dispatch: Function, getState: Function) => {
    if (!ignoreCache && !getState().library.reloadUnread) {
      return dispatch({ type: FETCH_UNREAD_CACHE });
    }

    dispatch({ type: FETCH_UNREAD_REQUEST });

    return fetch(Server.libraryUnread())
      .then(handleHTMLError)
      .then(
        json => dispatch({ type: FETCH_UNREAD_SUCCESS, unread: transformUnread(json.content) }),
        error =>
          dispatch({
            type: FETCH_UNREAD_FAILURE,
            errorMessage: 'Failed to get unread chapters for your library',
            meta: { error },
          }),
      );
  };
}

export function uploadRestoreFile(file: File) {
  return (dispatch: Function) => {
    dispatch({ type: UPLOAD_RESTORE_REQUEST });

    return fetch(Server.restoreUpload(), uploadPostParameters(file))
      .then(handleHTMLError)
      .then(
        // TODO: I'm not currently if the response message says failure or success
        () => dispatch({ type: UPLOAD_RESTORE_SUCCESS }),
        error =>
          dispatch({
            type: UPLOAD_RESTORE_FAILURE,
            errorMessage: `Failed to restore library from ${file.name}`,
            meta: { error },
          }),
      );
  };
}

export function setLibraryFlag(flag: string, state: string) {
  // TODO: there are no library flags on the server yet, so this is all local
  //       Update this function when the server eventually stores this data.
  return (dispatch: Function, getState: Function) => {
    if (getState().library.flags[flag] === state) {
      return dispatch({ type: SET_FLAG_NO_CHANGE, meta: { flag, state } });
    }

    dispatch({ type: SET_FLAG_REQUEST, flag, state });
    return dispatch({ type: SET_FLAG_SUCCESS });
  };
}

// ================================================================================
// Helper functions
// ================================================================================
type Param = Array<{ id: number, unread: number }>;
type Return = { [mangaId: number]: number };

function transformUnread(unreadArray: Param): Return {
  const newUnread = {};
  unreadArray.forEach((unreadObj) => {
    newUnread[unreadObj.id] = unreadObj.unread;
  });
  return newUnread;
}

function uploadPostParameters(file: File): Object {
  const formData = new FormData();
  formData.append('uploaded_file', file);

  return { method: 'POST', body: formData };
}
