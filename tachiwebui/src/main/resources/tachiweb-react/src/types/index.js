// @flow
export type FlagsType = {
  DISPLAY_MODE: 'NAME' | 'NUMBER',
  READ_FILTER: 'READ' | 'UNREAD' | 'ALL',
  SORT_DIRECTION: 'ASCENDING' | 'DESCENDING',
  SORT_TYPE: 'SOURCE' | 'NUMBER',
  DOWNLOADED_FILTER: 'DOWNLOADED' | 'NOT_DOWNLOADED' | 'ALL',
};

export type MangaType = {
  // NOTE: Many non-required fields may be missing because the server needs time to
  //       scrape the website, but returns a barebones object early anyway.

  // Must be included
  id: number,
  favorite: boolean,
  title: string,

  // I believe these will always be incliuded
  source: string,
  url: string,
  downloaded: boolean,
  flags: FlagsType,

  chapters: ?number,
  unread: ?number,
  author: ?string,
  description: ?string,
  thumbnail_url: ?string,
  genres: ?string,
  categories: ?Array<string>,
  status: ?string,
};

export type ChapterType = {
  date: number,
  source_order: number,
  read: boolean,
  name: string,
  chapter_number: number,
  download_status: string,
  id: number,
  last_page_read: number,
};

export type SourceType = {
  name: string,
  supports_latest: boolean,
  id: number,
  lang: {
    name: string,
    display_name: string,
  },
};

// TODO: I'm inventing my own library flags. Update to match and integrate with the server
//       when that eventually gets implemented into the backend.
export type LibraryFlagsType = {
  +DOWNLOADED_FILTER: 'DOWNLOADED' | 'ALL',
  +READ_FILTER: 'UNREAD' | 'ALL',
  +COMPLETED_FILTER: 'COMPLETED' | 'ALL',
  +SORT_TYPE:
    | 'ALPHABETICALLY'
    | 'LAST_READ'
    | 'LAST_UPDATED'
    | 'UNREAD'
    | 'TOTAL_CHAPTERS'
    | 'SOURCE',
  +SORT_DIRECTION: 'ASCENDING' | 'DESCENDING',
};
