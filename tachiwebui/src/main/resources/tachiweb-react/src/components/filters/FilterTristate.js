// @flow
import React from 'react';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

type Props = {
  name: string,
  state: number,
  onChange: Function,
};

// +-------+---------+
// | Index | State   |
// +-------+---------+
// | 0     | IGNORE  |
// | 1     | INCLUDE |
// | 2     | EXCLUDE |
// +-------+---------+

const FilterTristate = ({ name, state, onChange }: Props) => {
  const checked: boolean = state === 1;
  const indeterminate: boolean = state === 0;

  return (
    <FormControlLabel
      control={<Checkbox checked={checked} onChange={onChange} indeterminate={indeterminate} />}
      label={name}
    />
  );
};

export default FilterTristate;
