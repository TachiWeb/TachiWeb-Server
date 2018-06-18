// @flow
import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import MenuDrawer from 'components/MenuDrawer';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import type { SourceType } from 'types';
import Input from '@material-ui/core/Input';
import { withStyles } from '@material-ui/core/styles';

const styles = {
  catalogueSelect: {
    paddingLeft: 8,
  },
  searchInput: {
    flex: 1, // fill remaining width
    marginLeft: 16,
  },
};

type Props = {
  classes: Object, // injected styles
  sourceId: ?number,
  sources: Array<SourceType>,
  searchQuery: string,
  onSourceChange: Function,
  onSearchChange: Function,
};

const CatalogueHeader = ({
  classes,
  sourceId,
  sources,
  searchQuery,
  onSourceChange,
  onSearchChange,
}: Props) => {
  const sourcesExist = sources && sources.length > 0 && sourceId != null;
  const sourceIndex = sources.findIndex(source => source.id === sourceId);

  return (
    <AppBar color="default" position="static" style={{ marginBottom: 20 }}>
      <Toolbar>
        <MenuDrawer />

        {sourcesExist && (
          <React.Fragment>
            <Select
              value={sourceIndex}
              onChange={onSourceChange}
              classes={{ select: classes.catalogueSelect }}
            >
              {sources.map((source, index) => (
                <MenuItem value={index} key={source.id}>
                  {source.name}
                </MenuItem>
              ))}
            </Select>

            <Input
              className={classes.searchInput}
              placeholder="Search"
              value={searchQuery}
              onChange={onSearchChange}
            />
          </React.Fragment>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default withStyles(styles)(CatalogueHeader);
