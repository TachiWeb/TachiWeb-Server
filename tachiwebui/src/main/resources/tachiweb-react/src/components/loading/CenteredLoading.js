// @flow
import React from 'react';
import CircularProgress from '@material-ui/core/CircularProgress';
import CenteredHOC from 'components/CenteredHOC';

type Props = {
  className: ?string, // optional - parent passing a styled className
}; // otherProps will be passed to CircularProgress

const CenteredLoading = ({ className, ...otherProps }: Props) => {
  const CenteredCircularProgress = CenteredHOC(CircularProgress);
  return <CenteredCircularProgress className={className} {...otherProps} />;
};

CenteredLoading.defaultProps = { className: null };

export default CenteredLoading;
