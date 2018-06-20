// @flow
export type FilterText = {
  _cmaps: {
    name: 'java.lang.String',
    state: 'java.lang.String',
  },
  name: string,
  state: string,
  _type: 'TEXT',
};

export type FilterSelect = {
  values: Array<string>,
  _cmaps: {
    name: 'java.lang.String',
    state: 'java.lang.Integer',
  },
  name: string,
  state: number,
  _type: 'SELECT',
};

export type FilterTristate = {
  _cmaps: {
    name: 'java.lang.String',
    state: 'java.lang.Integer',
  },
  name: string,
  state: number,
  _type: 'TRISTATE',
};

export type FilterGroup = {
  state: Array<FilterTristate>,
  _cmaps: {
    name: 'java.lang.String',
  },
  name: string,
  _type: 'GROUP',
};

export type FilterSort = {
  values: Array<string>,
  state: {
    index: number,
    ascending: boolean,
  },
  _cmaps: {
    name: 'java.lang.String',
  },
  name: string,
  _type: 'SORT',
};

// UNUSED FILTER TYPES BELOW?

export type FilterHeader = {
  _cmaps: {
    name: 'java.lang.String',
  },
  name: string,
  _type: 'HEADER',
};

export type FilterSeparator = {
  _cmaps: {
    name: 'java.lang.String',
  },
  name: string,
  _type: 'SEPARATOR',
};

export type FilterCheckbox = {
  _cmaps: {
    name: 'java.lang.String',
    state: 'java.lang.Boolean',
  },
  name: string,
  state: boolean,
  _type: 'CHECKBOX',
};

// Consolidated type

export type FilterAnyType =
  | FilterText
  | FilterSelect
  | FilterTristate
  | FilterGroup
  | FilterSort
  | FilterHeader
  | FilterSeparator
  | FilterCheckbox;
