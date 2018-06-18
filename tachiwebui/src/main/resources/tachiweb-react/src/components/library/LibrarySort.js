// @flow
import React, { Component } from 'react';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';
import Tooltip from '@material-ui/core/Tooltip';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import type { LibraryFlagsType } from 'types';
import Typography from '@material-ui/core/Typography';
import { withStyles } from '@material-ui/core/styles';

const sorts = [
  { flagState: 'ALPHABETICALLY', description: 'Alphabetically' },
  { flagState: 'UNREAD', description: 'Unread' },
  { flagState: 'TOTAL_CHAPTERS', description: 'Total chapters' },
  { flagState: 'SOURCE', description: 'Source' },
  // others not implemented yet
];

const styles = {
  icon: {
    marginRight: 8,
  },
};

type Props = {
  classes: Object, // injected styles
  flags: LibraryFlagsType,
  onChange: Function,
};

type State = { anchorEl: ?HTMLElement };

class LibrarySort extends Component<Props, State> {
  state = {
    anchorEl: null,
  };

  handleClick = (event: SyntheticEvent<HTMLButtonElement>) => {
    this.setState({ anchorEl: event.currentTarget });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  handleSortChange = (newFlag: string) => () => {
    const { flags, onChange } = this.props;
    onChange(flags.SORT_TYPE, flags.SORT_DIRECTION, newFlag);
  };

  render() {
    const { classes, flags } = this.props;
    const { anchorEl } = this.state;

    return (
      <React.Fragment>
        <Tooltip title="Sort">
          <IconButton onClick={this.handleClick}>
            <Icon>sort_by_alpha</Icon>
          </IconButton>
        </Tooltip>

        {/* getContentAnchorEl must be null to make anchorOrigin work */}
        <Menu
          anchorEl={anchorEl}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
          getContentAnchorEl={null}
          open={Boolean(anchorEl)}
          onClose={this.handleClose}
        >
          {sorts.map(sort => (
            <MenuItem key={sort.flagState} onClick={this.handleSortChange(sort.flagState)}>
              <React.Fragment>
                <Icon className={classes.icon}>
                  {iconValue(flags.SORT_TYPE, flags.SORT_DIRECTION, sort.flagState)}
                </Icon>
                <Typography>{sort.description}</Typography>
              </React.Fragment>
            </MenuItem>
          ))}
        </Menu>
      </React.Fragment>
    );
  }
}

// Helper methods
function iconValue(currentFlag: string, sortDirection: string, thisFlag: string): string {
  if (currentFlag === thisFlag) {
    return sortDirection === 'ASCENDING' ? 'arrow_upward' : 'arrow_downward';
  }
  return '';
}

export default withStyles(styles)(LibrarySort);
