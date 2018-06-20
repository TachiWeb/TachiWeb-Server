// @flow
import React, { Component } from 'react';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import { withStyles } from '@material-ui/core/styles';
import CenteredLoading from 'components/loading/CenteredLoading';
import DialogActions from '@material-ui/core/DialogActions';
import Button from '@material-ui/core/Button';
import { Client } from 'api';
import Link from 'components/Link';

const styles = {
  dialog: {
    width: 320,
    height: 126, // TODO: using an arbitrary height for all dialog states. Make this less bad.
  },
};

type Props = {
  classes: Object, // injected styles
  open: boolean,
  onClose: Function,
  isLoading: boolean,
  failed: boolean,
  tryAgain: Function,
};

class RestoreDialog extends Component<Props> {
  handleClose = () => {
    if (this.props.isLoading) return;
    this.props.onClose();
  };

  content = () => {
    const { isLoading, failed, tryAgain } = this.props;

    if (isLoading) {
      return (
        <React.Fragment>
          <DialogTitle>Restoring Library...</DialogTitle>
          <CenteredLoading />
        </React.Fragment>
      );
    }

    if (failed) {
      return (
        <React.Fragment>
          <DialogTitle>Failed to Restore Library</DialogTitle>
          <DialogActions>
            <Button onClick={this.handleClose} color="primary">
              Cancel
            </Button>
            <Button onClick={tryAgain} color="primary" autoFocus>
              Try Again
            </Button>
          </DialogActions>
        </React.Fragment>
      );
    }

    return (
      <React.Fragment>
        <DialogTitle>Restore Successful</DialogTitle>
        <DialogActions>
          <Button onClick={this.handleClose} color="primary">
            Close
          </Button>
          <Button component={Link} to={Client.library()} color="primary" autoFocus>
            Go To Library
          </Button>
        </DialogActions>
      </React.Fragment>
    );
  };

  render() {
    const { classes, open } = this.props;

    return (
      <Dialog open={open} onClose={this.handleClose} classes={{ paper: classes.dialog }}>
        {this.content()}
      </Dialog>
    );
  }
}

export default withStyles(styles)(RestoreDialog);
