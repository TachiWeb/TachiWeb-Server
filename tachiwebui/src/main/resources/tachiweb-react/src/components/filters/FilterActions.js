// @flow
import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';

const styles = {
  // TODO: Position the controls div so that it's always at the top of the viewport
  //       I tried with position sticky and absolute, but it didn't work as intended
  //       Try again in the future
  controls: {
    paddingTop: 12,
    marginBottom: 8,
  },
  actionButtons: {
    marginBottom: 12,
    // Center align and stretch to fit
    display: 'flex',
    justifyContent: 'space-around',
    '& > *': {
      flexBasis: '40%',
    },
  },
};

type Props = {
  classes: Object,
  onResetClick: Function,
  onSearchClick: Function,
};

const FilterActions = ({ classes, onResetClick, onSearchClick }: Props) => (
  <div className={classes.controls}>
    <div className={classes.actionButtons}>
      <Button onClick={onResetClick}>Reset</Button>
      <Button variant="raised" color="primary" onClick={onSearchClick}>
          Search
      </Button>
    </div>
    <Divider />
  </div>
);

export default withStyles(styles)(FilterActions);
