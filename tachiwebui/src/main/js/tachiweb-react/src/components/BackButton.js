// @flow
import React from 'react';
import Icon from '@material-ui/core/Icon';
import IconButton from '@material-ui/core/IconButton';
import Link from 'components/Link';

type Props = { onBackClick: string | Function };

const BackButton = ({ onBackClick }: Props) => {
  if (typeof onBackClick === 'function') {
    return (
      <IconButton onClick={onBackClick}>
        <Icon>arrow_back</Icon>
      </IconButton>
    );
  } else if (typeof onBackClick === 'string') {
    return (
      <IconButton component={Link} to={onBackClick}>
        <Icon>arrow_back</Icon>
      </IconButton>
    );
  }

  console.error('No valid back button action passed to BackButton');
  return (
    <IconButton disabled>
      <Icon>arrow_back</Icon>
    </IconButton>
  );
};

export default BackButton;
