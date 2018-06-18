// @flow
import React from 'react';
import Typography from '@material-ui/core/Typography';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Icon from '@material-ui/core/Icon';
import IconButton from '@material-ui/core/IconButton';
import RefreshButton from 'components/RefreshButton';
import MangaInfoTabs from 'components/mangaInfo/MangaInfoTabs';
import type { MangaType } from 'types';
import BackButton from 'components/BackButton';
import MangaInfoMore from 'components/mangaInfo/MangaInfoMore';
import Tooltip from '@material-ui/core/Tooltip';
import MangaInfoFilter from 'components/mangaInfo/MangaInfoFilter';

// NOTE: empty href in IconButton will not render <a>

type Props = {
  mangaInfo: ?MangaType,
  tabValue: number,
  handleChangeTab: Function,
  onBackClick: string | Function,
  onRefreshClick: Function,
  setFlag: Function,
};

const MangaInfoHeader = ({
  mangaInfo,
  tabValue,
  handleChangeTab,
  onBackClick,
  onRefreshClick,
  setFlag,
}: Props) => (
  <AppBar color="default" position="static" style={{ marginBottom: 20 }}>
    <Toolbar>
      {mangaInfo && (
        <React.Fragment>
          <BackButton onBackClick={onBackClick} />
          <Typography variant="title" style={{ flex: 1 }}>
            {mangaInfo.title}
          </Typography>

          <RefreshButton onClick={onRefreshClick} />

          <MangaInfoFilter
            flags={mangaInfo.flags}
            onReadFilterChange={handleReadFilterChange(setFlag)}
            onDownloadedFilterChange={handleDownloadedFilterChange(setFlag)}
          />

          <Tooltip title="Sort">
            <IconButton onClick={handleSortClick(setFlag, mangaInfo.flags)}>
              <Icon>sort_by_alpha</Icon>
            </IconButton>
          </Tooltip>

          <MangaInfoMore
            sourceUrl={mangaInfo.url}
            flags={mangaInfo.flags}
            onDisplayModeChange={handleDisplayModeChange(setFlag)}
            onSortTypeChange={handleSortTypeChange(setFlag)}
          />
        </React.Fragment>
      )}
    </Toolbar>

    <MangaInfoTabs tabValue={tabValue} handleChange={handleChangeTab} />
  </AppBar>
);

function handleSortClick(setFlag, flags) {
  return () => {
    const newState = flags.SORT_DIRECTION === 'DESCENDING' ? 'ASCENDING' : 'DESCENDING';
    setFlag('SORT_DIRECTION', newState);
  };
}

function handleDisplayModeChange(setFlag) {
  return newDisplayMode => setFlag('DISPLAY_MODE', newDisplayMode);
}

function handleSortTypeChange(setFlag) {
  return newSortType => setFlag('SORT_TYPE', newSortType);
}

function handleReadFilterChange(setFlag) {
  return newReadFilter => setFlag('READ_FILTER', newReadFilter);
}

function handleDownloadedFilterChange(setFlag) {
  return newDownloadedFilter => setFlag('DOWNLOADED_FILTER', newDownloadedFilter);
}

export default MangaInfoHeader;
