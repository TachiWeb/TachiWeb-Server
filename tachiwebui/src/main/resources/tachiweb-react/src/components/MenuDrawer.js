// @flow
import React, { Component } from 'react';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';
import Drawer from '@material-ui/core/Drawer';
import MenuList from 'components/MenuList';

type State = {
  drawerOpen: boolean,
};

class MenuDrawer extends Component<{}, State> {
  state = {
    drawerOpen: false,
  };

  toggleDrawer = (isOpen: boolean) => () => {
    this.setState({ drawerOpen: isOpen });
  };

  render() {
    const { drawerOpen } = this.state;

    return (
      <React.Fragment>
        <IconButton onClick={this.toggleDrawer(true)}>
          <Icon>menu</Icon>
        </IconButton>

        <Drawer open={drawerOpen} onClose={this.toggleDrawer(false)}>
          <div
            tabIndex={0}
            role="button"
            onClick={this.toggleDrawer(false)}
            onKeyDown={this.toggleDrawer(false)}
          >
            <MenuList />
          </div>
        </Drawer>
      </React.Fragment>
    );
  }
}

export default MenuDrawer;
