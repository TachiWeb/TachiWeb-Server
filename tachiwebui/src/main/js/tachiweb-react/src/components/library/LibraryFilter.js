// @flow
import React, { Component } from 'react';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';
import Tooltip from '@material-ui/core/Tooltip';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import type { LibraryFlagsType } from 'types';

// NOTE: Refer to MangaInfoFilter for more details, components are extremely similar

// TODO: can I create a generic component since they are so similar?

type Props = {
  flags: LibraryFlagsType,
  onReadFilterChange: Function,
  onDownloadedFilterChange: Function,
  onCompletedFilterChange: Function,
};

type State = { anchorEl: ?HTMLElement };

class LibraryFilter extends Component<Props, State> {
  state = {
    anchorEl: null,
  };

  handleClick = (event: SyntheticEvent<HTMLButtonElement>) => {
    this.setState({ anchorEl: event.currentTarget });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  handleUnreadClick = (e: SyntheticEvent<>) => {
    e.preventDefault();
    const { flags, onReadFilterChange } = this.props;
    const newReadFlag = flags.READ_FILTER === 'ALL' ? 'UNREAD' : 'ALL';
    onReadFilterChange(newReadFlag);
  };

  handleDownloadedClick = (e: SyntheticEvent<>) => {
    e.preventDefault();
    const { flags, onDownloadedFilterChange } = this.props;
    const newDownloadedFlag = flags.DOWNLOADED_FILTER === 'ALL' ? 'DOWNLOADED' : 'ALL';
    onDownloadedFilterChange(newDownloadedFlag);
  };

  handleCompletedClick = (e: SyntheticEvent<>) => {
    e.preventDefault();
    const { flags, onCompletedFilterChange } = this.props;
    const newCompletedFlag = flags.COMPLETED_FILTER === 'ALL' ? 'COMPLETED' : 'ALL';
    onCompletedFilterChange(newCompletedFlag);
  };

  render() {
    const { flags } = this.props;
    const { anchorEl } = this.state;

    return (
      <React.Fragment>
        <Tooltip title="Filter">
          <IconButton onClick={this.handleClick}>
            <Icon>filter_list</Icon>
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
          <MenuItem onClick={this.handleDownloadedClick}>
            <FormControlLabel
              label="Downloaded"
              control={<Checkbox checked={flags.DOWNLOADED_FILTER === 'DOWNLOADED'} />}
            />
          </MenuItem>

          <MenuItem onClick={this.handleUnreadClick}>
            <FormControlLabel
              label="Unread"
              control={<Checkbox checked={flags.READ_FILTER === 'UNREAD'} />}
            />
          </MenuItem>

          <MenuItem onClick={this.handleCompletedClick}>
            <FormControlLabel
              label="Completed"
              control={<Checkbox checked={flags.COMPLETED_FILTER === 'COMPLETED'} />}
            />
          </MenuItem>
        </Menu>
      </React.Fragment>
    );
  }
}

export default LibraryFilter;
