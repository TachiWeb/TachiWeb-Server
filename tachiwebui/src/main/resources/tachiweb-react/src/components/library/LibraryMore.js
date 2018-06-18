// @flow
import React, { Component } from 'react';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';
import Tooltip from '@material-ui/core/Tooltip';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';

type State = {
  anchorEl: ?HTMLElement, // don't know what to put here
  editing: boolean,
};

class LibraryMore extends Component<{}, State> {
  state = {
    anchorEl: null,
    editing: false,
  };

  handleClick = (event: SyntheticEvent<HTMLButtonElement>) => {
    this.setState({ anchorEl: event.currentTarget });
  };

  handleEditCategories = () => {
    // Close the menu as well
    this.setState({
      anchorEl: null,
      editing: !this.state.editing,
    });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  render() {
    const { anchorEl, editing } = this.state;

    return (
      <React.Fragment>
        <Tooltip title="More">
          <IconButton onClick={this.handleClick}>
            <Icon>more_vert</Icon>
          </IconButton>
        </Tooltip>

        {/* getContentAnchorEl must be null to make anchorOrigin work */}
        {/* TODO: add transitionDuration so the changed text isn't visible too early */}
        <Menu
          anchorEl={anchorEl}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
          getContentAnchorEl={null}
          open={Boolean(anchorEl)}
          onClose={this.handleClose}
        >
          <MenuItem onClick={this.handleEditCategories}>
            {!editing ? 'Edit Categories' : 'Exit Category Editor'}
          </MenuItem>
        </Menu>
      </React.Fragment>
    );
  }
}

export default LibraryMore;
