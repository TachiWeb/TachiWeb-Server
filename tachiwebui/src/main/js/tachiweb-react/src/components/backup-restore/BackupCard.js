// @flow
import React from 'react';
import Button from '@material-ui/core/Button';
import Icon from '@material-ui/core/Icon';
import Card from '@material-ui/core/Card';
import { Server } from 'api';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import CardContent from '@material-ui/core/CardContent';

const styles = {
  icon: { marginRight: 8 },
  button: {
    width: '100%',
    marginTop: 16,
  },
};

type Props = { classes: Object };

const BackupCard = ({ classes }: Props) => (
  <Card>
    <CardContent>
      <Typography gutterBottom variant="headline">
        Create a Backup
      </Typography>
      <Typography>
        A backup file can be used to restore your current library in TachiWeb or Tachiyomi
      </Typography>

      <Button
        className={classes.button}
        variant="raised"
        color="primary"
        href={Server.backupDownload()}
        download
      >
        <Icon className={classes.icon}>cloud_download</Icon>
        Download
      </Button>
    </CardContent>
  </Card>
);

export default withStyles(styles)(BackupCard);
