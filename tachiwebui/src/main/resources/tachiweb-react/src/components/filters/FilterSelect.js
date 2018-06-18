// @flow
import React from 'react';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';

// NOTE: Odd obsevations about choosing the key (No errors though)
//
// test is prepended with '.$.$', name is also prepended with '.$.$'
// but oddly enough, when you combine them together, only the first one is prepended with '.$.$'
// e.g. key={`${name} ${text}`} -> key=".$.$Type Any"
//
// I think because MenuItem is so deeply nested in other components (via material-ui)
// it makes passing values behave oddly...

type Props = {
  name: string,
  values: Array<string>,
  index: number,
  state: number,
  onChange: Function,
};

const FilterSelect = ({
  name, values, index, state, onChange,
}: Props) => (
  <FormControl>
    <InputLabel htmlFor={generateId(index)}>{name}</InputLabel>
    <Select value={state} onChange={onChange} inputProps={{ id: generateId(index) }}>
      {values.map((text, valuesIndex) => (
        <MenuItem value={valuesIndex} key={`${name} ${text}`}>
          {text}
        </MenuItem>
      ))}
    </Select>
  </FormControl>
);

// Helper function
function generateId(index: number): string {
  return `filter-select-${index}`;
}

export default FilterSelect;
