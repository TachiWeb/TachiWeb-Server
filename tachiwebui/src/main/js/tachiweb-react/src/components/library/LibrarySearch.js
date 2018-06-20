// @flow
import React, { Component } from 'react';
import IconButton from '@material-ui/core/IconButton';
import Icon from '@material-ui/core/Icon';
import Tooltip from '@material-ui/core/Tooltip';
import FormControl from '@material-ui/core/FormControl';
import InputAdornment from '@material-ui/core/InputAdornment';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';

// TODO: pressing ESC to clear and close the search? That would be cool.

type Props = { searchQuery: string, onSearchChange: Function };

type State = { searchVisible: boolean };

class LibrarySearch extends Component<Props, State> {
  constructor() {
    super();
    this.state = { searchVisible: false };

    // TODO: Animate search bar in and out.
    //       I tried before, but was having trouble figuring out how to do it.

    // this.formControlRef = React.createRef();
    this.inputRef = React.createRef();

    // e.g. a property of Input would be
    // inputRef is Material-UI's non-standard way of handling refs
    // inputRef={(input) => {
    //   this.inputRef = input;
    // }}
  }

  inputRef = null;

  handleClick = () => {
    this.setState({ searchVisible: true });
    // this.inputRef.focus();
  };

  handleChange = (event: SyntheticEvent<HTMLInputElement>) => {
    this.props.onSearchChange(event.currentTarget.value);
  };

  handleClearSearch = () => {
    // this.formControlRef
    // this.inputRef.blur(); // Remove focus from input
    this.setState({ searchVisible: false });
    this.props.onSearchChange('');
  };

  handleBlur = () => {
    // TODO: clicking on the search icon will call this because it's not part of the input
    //       not my intended interaction, but maybe not an important fix?
    if (!this.props.searchQuery) {
      this.setState({ searchVisible: false });
      this.props.onSearchChange('');
    }
  };

  render() {
    const { searchQuery } = this.props;
    const { searchVisible } = this.state;

    return (
      <React.Fragment>
        <Tooltip title="Search">
          <IconButton onClick={this.handleClick}>
            <Icon>search</Icon>
          </IconButton>
        </Tooltip>

        {searchVisible ? (
          <FormControl>
            <InputLabel htmlFor="library-search-text">Search Library</InputLabel>
            <Input
              id="library-search-text"
              value={searchQuery}
              autoFocus
              onChange={this.handleChange}
              onBlur={this.handleBlur}
              endAdornment={
                <InputAdornment position="end">
                  <IconButton onClick={this.handleClearSearch}>
                    <Icon>close</Icon>
                  </IconButton>
                </InputAdornment>
              }
            />
          </FormControl>
        ) : null}
      </React.Fragment>
    );
  }
}

export default LibrarySearch;
