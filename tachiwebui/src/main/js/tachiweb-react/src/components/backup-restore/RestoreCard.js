// @flow
import React, { Component } from 'react';
import Button from '@material-ui/core/Button';
import Icon from '@material-ui/core/Icon';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Dropzone from 'react-dropzone';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import RestoreDialog from 'components/backup-restore/RestoreDialog';
import type { RestoreCardContainerProps } from 'containers/RestoreCardContainer';

// TODO: clear all files from state when user successfully restores library

// TODO: use custom styling for dropzone
//       I'm currently using the default styles included with dropzone (which I copied over).
//
// Can use this for reference (and copy code)
// https://css-tricks.com/drag-and-drop-file-uploading/

// About the [accept="application/json,.json"]
// https://stackoverflow.com/questions/46663063/inputs-accept-attribute-doesnt-recognise-application-json

// React dropzone docs
// https://github.com/react-dropzone/react-dropzone

// using 'rejectedFiles' array to show if invalid files were passed
/* eslint-disable react/no-unused-state */

const styles = {
  icon: { marginRight: 8 },
  button: {
    width: '100%',
    marginTop: 16,
  },
  dropzoneDefault: {
    borderWidth: 2,
    borderColor: '#666',
    borderStyle: 'dashed',
    borderRadius: 5,

    width: '100%',
    height: 200,
    padding: 16,
  },
  dropzoneActive: {
    borderStyle: 'solid',
    borderColor: '#6c6',
    backgroundColor: '#eee',
  },
  // Dragging a file that won't be accepted will add both the active and rejected class
  // using '!important' to force the correct borderColor. (this is just how dropzone works I guess)
  dropzoneRejected: {
    borderColor: '#c66 !important',
  },
};

type Props = RestoreCardContainerProps & { classes: Object };

type State = {
  acceptedFiles: Array<File>,
  rejectedFiles: Array<File>,
  dialogueOpen: boolean,
};

class RestoreCard extends Component<Props, State> {
  state = {
    // Only accept 1 file, but dropzone gives us an array regardless
    acceptedFiles: [],
    rejectedFiles: [],

    dialogueOpen: false,
  };

  handleDrop = (acceptedFiles: Array<File>, rejectedFiles: Array<File>) => {
    this.setState({ acceptedFiles, rejectedFiles });
  };

  // If dropzone's child node is a function, it will inject params
  // refer to dropzone docs for more details
  dropzoneContent = ({ isDragReject, acceptedFiles, rejectedFiles }: Object) => {
    if (isDragReject) {
      return 'Only a single JSON backup file is accepted';
    } else if (rejectedFiles.length) {
      return 'Invalid file selected';
    } else if (acceptedFiles.length) {
      return `${acceptedFiles[0].name}`;
    }
    return 'Drag and Drop or Click Here to upload your backup file';
  };

  handleUpload = () => {
    const { uploadRestoreFile } = this.props;
    const { acceptedFiles } = this.state;

    // Checking that files exist just in case (even though button should be disabled)
    if (acceptedFiles.length) {
      uploadRestoreFile(acceptedFiles[0]);
      this.setState({ dialogueOpen: true }); // open loading dialog
    }
  };

  handleCloseDialog = () => {
    this.setState({ dialogueOpen: false });
  };

  render() {
    const { classes, restoreIsLoading, restoreFailed } = this.props;
    const { acceptedFiles, rejectedFiles, dialogueOpen } = this.state;

    const buttonDisabled: boolean = acceptedFiles.length !== 1 || rejectedFiles.length > 0;

    return (
      <React.Fragment>
        <Card>
          <CardContent>
            <Typography gutterBottom variant="headline">
              Restore Your Library
            </Typography>

            <Dropzone
              className={classes.dropzoneDefault}
              activeClassName={classes.dropzoneActive}
              acceptClassName={classes.dropzoneAccept}
              rejectClassName={classes.dropzoneRejected}
              onDrop={this.handleDrop}
              multiple={false}
              accept="application/json,.json"
            >
              {this.dropzoneContent}
            </Dropzone>

            <Button
              className={classes.button}
              variant="raised"
              color="primary"
              disabled={buttonDisabled}
              onClick={this.handleUpload}
            >
              <Icon className={classes.icon}>cloud_upload</Icon>
              Restore
            </Button>
          </CardContent>
        </Card>

        <RestoreDialog
          open={dialogueOpen}
          onClose={this.handleCloseDialog}
          isLoading={restoreIsLoading}
          failed={restoreFailed}
          tryAgain={this.handleUpload}
        />
      </React.Fragment>
    );
  }
}

export default withStyles(styles)(RestoreCard);
