// @flow
import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';

const styles = {
  loader: {
    position: 'fixed',
    margin: 'auto',
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
  },
};

type Props = { classes: Object };

const FullScreenLoading = ({ classes }: Props) => (
  <CircularProgress className={classes.loader} size={60} thickness={4.5} />
);

export default withStyles(styles)(FullScreenLoading);
