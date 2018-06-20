// @flow
import * as React from 'react';
import Grid from '@material-ui/core/Grid';

// TODO: tweak defailt maxWidth to something that makes sense (on an average monitor)

// TODO: add an 'outerGridClassName' prop if necessary in the future

// Parent Grid centers the child Grid
// Also constrains child Grid's max width
// https://stackoverflow.com/questions/49251454/grid-container-like-bootstrap-container

// NOTE: Material-ui Grid has a weird limitation where you must add
//       padding or it will overflow.
// https://material-ui.com/layout/grid/#negative-margin

// Based on material-ui's grid breakpoints (max val, not min val)
// https://material-ui.com/customization/default-theme/#default-theme
const breakpoints = {
  xs: 600 - 1,
  sm: 960 - 1,
  md: 1280 - 1,
  lg: 1920 - 1,
  xl: 1920, // TODO: not sure what to do with xl size
};

type Props = {
  children: React.Node,

  // Optional props
  spacing: number,
  maxWidth: number | 'xs' | 'sm' | 'md' | 'lg' | 'xl',
}; // other props get passed to the inner grid

const ResponsiveGrid = ({
  children, spacing, maxWidth, ...otherProps
}: Props) => {
  const calcMaxWidth = typeof maxWidth === 'string' ? breakpoints[maxWidth] : maxWidth;
  const maxWidthStyle = { maxWidth: calcMaxWidth };

  const padding = Math.max(8, spacing / 2); // at least 8px on each side
  const fixXOverflow = {
    paddingLeft: padding,
    paddingRight: padding,
  };

  return (
    <Grid container justify="center" style={fixXOverflow}>
      <Grid container {...otherProps} spacing={spacing} style={maxWidthStyle}>
        {children}
      </Grid>
    </Grid>
  );
};

ResponsiveGrid.defaultProps = {
  spacing: 16,
  maxWidth: 'md',
};

export default ResponsiveGrid;
