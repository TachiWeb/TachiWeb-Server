// @flow
import React from 'react';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import Typography from '@material-ui/core/Typography';
import Icon from '@material-ui/core/Icon';
import FormGroup from '@material-ui/core/FormGroup';
import type { FilterTristate as FilterTristateType } from 'types/filters';
import FilterTristate from './FilterTristate';

type Props = {
  name: string,
  state: Array<FilterTristateType>,
  onChange: Function,
};

// NOTE: Assuming that GROUP will only contain TRISTATE children
// NOTE: using name as the key, this shouldn't be a problem

const FilterGroup = ({ name, state, onChange }: Props) => (
  <ExpansionPanel>
    <ExpansionPanelSummary expandIcon={<Icon>expand_more</Icon>}>
      <Typography>{name}</Typography>
    </ExpansionPanelSummary>
    <ExpansionPanelDetails>
      <FormGroup>
        {state.map((tristate, nestedIndex) => (
          <FilterTristate
            name={tristate.name}
            state={tristate.state}
            onChange={onChange(nestedIndex)}
            key={`${name} ${tristate.name}`}
          />
        ))}
      </FormGroup>
    </ExpansionPanelDetails>
  </ExpansionPanel>
);

export default FilterGroup;
