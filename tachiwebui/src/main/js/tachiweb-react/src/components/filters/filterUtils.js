// @flow
import * as React from 'react';
import TextField from '@material-ui/core/TextField';
import type {
  FilterAnyType,
  FilterText as FilterTextType,
  FilterSelect as FilterSelectType,
  FilterTristate as FilterTristateType,
  FilterSort as FilterSortType,
  FilterGroup as FilterGroupType,
} from 'types/filters';
import FilterSelect from './FilterSelect';
import FilterTristate from './FilterTristate';
import FilterGroup from './FilterGroup';
import FilterSort from './FilterSort';

// FIXME: Still feels a little laggy in dev mode.
//        May partially be caused in dev by React DevTools
//        https://stackoverflow.com/questions/32911519/react-slow-with-multiple-controlled-text-inputs
//
//        Try a production build to see how bad it is.
//
//        If it's still bad, try using immer - https://github.com/mweststrate/immer
//        Other suggestions here - https://medium.freecodecamp.org/handling-state-in-react-four-immutable-approaches-to-consider-d1f5c00249d5

/* eslint-disable import/prefer-default-export, no-underscore-dangle */
export function filterElements(
  filters: Array<FilterAnyType>,
  onChange: Function,
): Array<React.Node> {
  return filters.map((filter: FilterAnyType, index: number) => {
    // TODO: header, separator, checkbox
    //       not doing right now because none of the sources use it

    // NOTE: using filter.name as the key. I doubt it'll be a problem.

    if (filter._type === 'HEADER') {
      console.error('DynamicSourcesFilters HEADER not implemented');
      return null;
    } else if (filter._type === 'SEPARATOR') {
      console.error('DynamicSourcesFilters SEPARATOR not implemented');
      return null;
    } else if (filter._type === 'CHECKBOX') {
      console.error('DynamicSourcesFilters CHECKBOX not implemented');
      return null;
    } else if (filter._type === 'TEXT') {
      return (
        <TextField
          label={filter.name}
          value={filter.state}
          onChange={handleTextChange(index, filter, filters, onChange)}
          key={filter.name}
        />
      );
    } else if (filter._type === 'SELECT') {
      return (
        <FilterSelect
          index={index}
          values={filter.values}
          name={filter.name}
          state={filter.state}
          onChange={handleSelectChange(index, filter, filters, onChange)}
          key={filter.name}
        />
      );
    } else if (filter._type === 'TRISTATE') {
      return (
        <FilterTristate
          name={filter.name}
          state={filter.state}
          onChange={handleTristateChange(index, filter, filters, onChange)}
          key={filter.name}
        />
      );
    } else if (filter._type === 'GROUP') {
      // NOTE: Assuming that GROUP will only contain TRISTATE children
      return (
        <FilterGroup
          name={filter.name}
          state={filter.state}
          onChange={handleGroupChange(index, filter, filters, onChange)}
          key={filter.name}
        />
      );
    } else if (filter._type === 'SORT') {
      return (
        <FilterSort
          values={filter.values}
          name={filter.name}
          state={filter.state}
          onChange={handleSortChange(index, filter, filters, onChange)}
          key={filter.name}
        />
      );
    }

    return null;
  });
}

function handleTextChange(
  index: number,
  filter: FilterTextType,
  filters: Array<FilterAnyType>,
  onChange: Function,
) {
  return (event: SyntheticEvent<HTMLInputElement>) => {
    const updatedFilter: FilterTextType = { ...filter, state: event.currentTarget.value };
    onChange(updateArray(index, updatedFilter, filters));
  };
}

function handleSelectChange(
  index: number,
  filter: FilterSelectType,
  filters: Array<FilterAnyType>,
  onChange: Function,
) {
  // NOTE: LIElement is actually within a select
  return (event: SyntheticEvent<HTMLLIElement>) => {
    const newSelection = parseInt(event.currentTarget.dataset.value, 10);
    const updatedFilter: FilterSelectType = { ...filter, state: newSelection };
    onChange(updateArray(index, updatedFilter, filters));
  };
}

function handleTristateChange(
  index: number,
  filter: FilterTristateType,
  filters: Array<FilterAnyType>,
  onChange: Function,
) {
  return () => {
    const updatedFilter: FilterTristateType = { ...filter, state: updateTristate(filter.state) };
    onChange(updateArray(index, updatedFilter, filters));
  };
}

function handleGroupChange(
  index: number,
  filter: FilterGroupType,
  filters: Array<FilterAnyType>,
  onChange: Function,
) {
  // NOTE: Assuming that GROUP will only contain TRISTATE children
  return (clickedIndex: number) => () => {
    // Nested filters, so it's a bit more complex to update
    // First update the nested tristate's state
    const tristate = filter.state[clickedIndex];
    const updatedTristate: FilterTristateType = {
      ...tristate,
      state: updateTristate(tristate.state),
    };

    // Then insert the updated tristate into the original array of tristates
    const updatedTristateArray = updateArray(clickedIndex, updatedTristate, filter.state);

    // Then update the group's state with the new array and update the whole state
    const updatedFilter: FilterGroupType = { ...filter, state: updatedTristateArray };
    onChange(updateArray(index, updatedFilter, filters));
  };
}

type SortState = { index: number, ascending: boolean };
function handleSortChange(
  index: number,
  filter: FilterSortType,
  filters: Array<FilterAnyType>,
  onChange: Function,
) {
  return (clickedIndex: number) => () => {
    const isAscending = filter.state.ascending;
    const currentIndex = filter.state.index;

    const newState: SortState = updateSort(currentIndex, clickedIndex, isAscending);
    const updatedFilter: FilterSortType = { ...filter, state: newState };
    onChange(updateArray(index, updatedFilter, filters));
  };
}

// Helper Functions
function updateTristate(oldState: number): number {
  if (oldState < 2) {
    return oldState + 1;
  }
  return 0;
}

function updateSort(index: number, clickedIndex: number, isAscending: boolean): SortState {
  return {
    index: clickedIndex,
    ascending: index === clickedIndex ? !isAscending : false,
  };
}

function updateArray<T>(index: number, newElement: T, array: Array<T>): Array<T> {
  return [...array.slice(0, index), newElement, ...array.slice(index + 1)];
}
