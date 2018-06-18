// @flow
import React, { Component } from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';

type Props = { errorMessage: string };
type State = { open: boolean, message: string };

// TODO: Honestly, the logic here feels really fragile, and I don't
//       have the best grasp of all the edge cases.
//       Maybe finding a library would be helpful for this.

class ErrorNotifications extends Component<Props, State> {
  state = {
    open: false,
    message: '',
  };

  componentDidUpdate() {
    const { open, message } = this.state;
    const { errorMessage: propsMessage } = this.props;

    if (message === propsMessage) return;

    /* eslint-disable react/no-did-update-set-state */
    if (!propsMessage) {
      this.setState({ open: false, message: '' });
    } else if (!open) {
      this.setState({ open: true, message: propsMessage });
    } else if (open) {
      this.setState({ open: false });
      // then let handleExited() update if necessary
    }
    /* eslint-enable react/no-did-update-set-state */
  }

  handleClose = (event: SyntheticEvent<>, reason: ?string) => {
    if (reason === 'clickaway') return;

    this.setState({ open: false });
  };

  handleExited = () => {
    const { message } = this.state;
    const { errorMessage: propsMessage } = this.props;

    if (message !== propsMessage) {
      this.setState({ open: true, message: propsMessage });
    }
  };

  render() {
    const { message } = this.state;

    return (
      <Snackbar
        key={message}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
        open={this.state.open}
        autoHideDuration={6000}
        onClose={this.handleClose}
        onExited={this.handleExited}
        message={<span id="message-id">{message}</span>}
        action={
          <IconButton onClick={this.handleClose}>
            <Icon>close</Icon>
          </IconButton>
        }
      />
    );
  }
}

export default ErrorNotifications;
