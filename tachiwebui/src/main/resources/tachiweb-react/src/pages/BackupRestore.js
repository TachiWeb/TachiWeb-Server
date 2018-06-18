// @flow
import React, { Component } from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import MenuDrawer from 'components/MenuDrawer';
import ResponsiveGrid from 'components/ResponsiveGrid';
import { Grid } from '@material-ui/core';
import BackupCard from 'components/backup-restore/BackupCard';
import RestoreCard from 'components/backup-restore/RestoreCard';
import type { BackupRestoreContainerProps as Props } from 'containers/BackupRestoreContainer';
import { Helmet } from 'react-helmet';

// TODO: Use a very small max width for the responsive grid in this page
//       will require updating ResponsiveGrid's code

class BackupRestore extends Component<Props> {
  handleUploadRestore = (file: File) => {
    this.props.uploadRestoreData(file);
  };

  render() {
    return (
      <React.Fragment>
        <Helmet>
          <title>Backup and Restore - TachiWeb</title>
        </Helmet>

        <AppBar color="default" position="static" style={{ marginBottom: 20 }}>
          <Toolbar>
            <MenuDrawer />
            <Typography variant="title">Backup / Restore</Typography>
          </Toolbar>
        </AppBar>

        <ResponsiveGrid maxWidth="xs">
          <Grid item xs={12}>
            <BackupCard />
          </Grid>

          <Grid item xs={12}>
            <RestoreCard onClickRestore={this.handleUploadRestore} />
          </Grid>
        </ResponsiveGrid>
      </React.Fragment>
    );
  }
}

export default BackupRestore;
