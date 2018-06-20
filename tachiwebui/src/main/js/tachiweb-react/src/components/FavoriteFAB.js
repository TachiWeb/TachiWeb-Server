// @flow
import React from 'react';
import Icon from '@material-ui/core/Icon';
import FAB from 'components/FAB';
import { withStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';
import type { FavoriteFABContainerProps } from 'containers/FavoriteFABContainer';

// NOTE: refer to FAB for specific CSS instructions

// TODO: Loading spinner flickers because of short delay.
//       Would be interesting to create a spinner with a small delay before appearing.

const styles = {
  fabProgress: {
    position: 'absolute',
    animation: 'fadeInFromNone 2.5s ease-out',
  },
};

type Props = { classes: Object };

const FavoriteFAB = ({
  classes,
  isFavorite,
  favoriteIsToggling,
  toggleFavorite,
}: FavoriteFABContainerProps & Props) => (
  <React.Fragment>
    <FAB onClick={toggleFavorite(isFavorite)}>
      {isFavorite ? <Icon>bookmark</Icon> : <Icon>bookmark_border</Icon>}

      {favoriteIsToggling && (
        <CircularProgress size={70} color="secondary" className={classes.fabProgress} />
      )}
    </FAB>
  </React.Fragment>
);

export default withStyles(styles)(FavoriteFAB);
