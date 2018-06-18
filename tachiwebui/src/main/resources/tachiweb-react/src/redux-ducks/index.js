// @flow
import { combineReducers } from 'redux';
import loading from './loading';
import error from './error';
import library from './library';
import chapters from './chapters';
import pageCounts from './pageCounts';
import sources from './sources';
import catalogue from './catalogue';
import filters from './filters';
import mangaInfos from './mangaInfos';

export default combineReducers({
  loading,
  error,
  library,
  chapters,
  pageCounts,
  sources,
  catalogue,
  filters,
  mangaInfos,
});

// NOTE: some Thunks (asynchronous calls) may escape early
//       (e.g. return cached data) instead of returning a promise.
//
// A consequence of this is that you can't call .then() on them safely.
// A workaround is to forcefully return a promise so that any function can use
// .then() regardless of cached data or fetch from the server.
//
// Not every function has had this modification made, only the ones that have caused problems.
