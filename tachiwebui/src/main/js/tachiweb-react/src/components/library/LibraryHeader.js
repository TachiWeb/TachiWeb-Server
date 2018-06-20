// @flow
import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import LibraryMore from 'components/library/LibraryMore';
import RefreshButton from 'components/RefreshButton';
import LibrarySearch from 'components/library/LibrarySearch';
import MenuDrawer from 'components/MenuDrawer';
import LibraryFilter from 'components/library/LibraryFilter';
import LibrarySort from 'components/library/LibrarySort';
import type { LibraryFlagsType } from 'types';

type Props = {
  flags: LibraryFlagsType,
  searchQuery: string,
  onSearchChange: Function,
  onRefreshClick: Function,
  setLibraryFlag: Function,
};

const LibraryHeader = ({
  flags,
  searchQuery,
  onSearchChange,
  onRefreshClick,
  setLibraryFlag,
}: Props) => (
  <AppBar color="default" position="static" style={{ marginBottom: 20 }}>
    <Toolbar>
      <MenuDrawer />

      <Typography variant="title" style={{ flex: 1 }}>
        Library
      </Typography>

      <LibrarySearch searchQuery={searchQuery} onSearchChange={onSearchChange} />

      <RefreshButton onClick={onRefreshClick} />

      <LibraryFilter
        flags={flags}
        onReadFilterChange={handleReadFilterChange(setLibraryFlag)}
        onDownloadedFilterChange={handleDownloadedFilterChange(setLibraryFlag)}
        onCompletedFilterChange={handleCompletedFilterChange(setLibraryFlag)}
      />

      <LibrarySort flags={flags} onChange={handleSortChange(setLibraryFlag)} />

      <LibraryMore />
    </Toolbar>
  </AppBar>
);

function handleReadFilterChange(setLibraryFlag) {
  return newReadFilter => setLibraryFlag('READ_FILTER', newReadFilter);
}

function handleDownloadedFilterChange(setLibraryFlag) {
  return newDownloadedFilter => setLibraryFlag('DOWNLOADED_FILTER', newDownloadedFilter);
}

function handleCompletedFilterChange(setLibraryFlag) {
  return newCompletedFilter => setLibraryFlag('COMPLETED_FILTER', newCompletedFilter);
}

function handleSortChange(setLibraryFlag) {
  return (currentFlag, currentDirection, newFlag) => {
    if (currentFlag === newFlag) {
      const newDirection = currentDirection === 'ASCENDING' ? 'DESCENDING' : 'ASCENDING';
      return setLibraryFlag('SORT_DIRECTION', newDirection);
    }

    setLibraryFlag('SORT_TYPE', newFlag);
    return setLibraryFlag('SORT_DIRECTION', 'ASCENDING');
  };
}

export default LibraryHeader;
