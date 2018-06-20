// @flow
import React from 'react';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';
import Tooltip from '@material-ui/core/Tooltip';

type Props = { onClick: Function };

const RefreshButton = ({ onClick }: Props) => (
  <Tooltip title="Refresh">
    <IconButton onClick={onClick}>
      <Icon>refresh</Icon>
    </IconButton>
  </Tooltip>
);

export default RefreshButton;
