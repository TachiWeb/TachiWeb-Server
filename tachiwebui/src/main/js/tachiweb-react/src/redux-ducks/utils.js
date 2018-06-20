// @flow
import type { MangaType } from 'types';

// TODO: Not sure if these flow types are 100% correct
export function handleHTMLError(res: Response): Promise<Object> {
  // NOTE: This should be used in tandem with a Promise

  // Sometimes the server will return <html><body><h2>500 Internal Error</h2></body></html>
  // This is not JSON, and causes a crash if the error handler is expecting JSON only.

  if (res.status === 500) {
    // Server Error occurred, html returned. Throw error.
    return Promise.reject(new Error('500 Server Error encountered when trying to fetch catalogue'));
  }
  return res.json();
  // handleHTMLError returns res.json() (type Promise)
  // This is convenient because if it only returned res, the next .then() would
  // be .then(res => res.json, [CATCH_FUNCTION]).then([HANDLE_JSON])
  //
  // The problem is CATCH_FUNCTION would dispatch an error to the store,
  // but then run HANDLE_JSON, which would obviously break.
  // To break the promise chain in CATCH_FUNCTION would require me to return a Promise.reject(),
  // which is just extra code to write.
  //
  // Plus it seems safe to assume that all responses from the server are JSON format.
}

export function transformToMangaIdsArray(mangaArray: Array<MangaType>): Array<number> {
  return mangaArray.map(manga => manga.id);
}
