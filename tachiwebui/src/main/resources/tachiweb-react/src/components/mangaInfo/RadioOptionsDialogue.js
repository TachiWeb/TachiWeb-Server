// @flow
import React, { Component } from 'react';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import RadioGroup from '@material-ui/core/RadioGroup';
import Radio from '@material-ui/core/Radio';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import Button from '@material-ui/core/Button';

// Renders a Dialogue with a RadioGroup of options.
// onClose function should handle setting open = false.
// This passes a new value to onClose when the user makes a new selection.

type Props = {
  title: string,
  open: boolean,
  value: string,
  options: Array<{ flagState: string, label: string }>,
  onClose: Function,
};

type State = {
  // Keep a local copy of value for the user to edit
  value: string,
};

class RadioOptionsDialogue extends Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      value: this.props.value,
    };
  }

  radioGroup = null;

  handleEntering = () => {
    this.setState({ value: this.props.value }); // reset state on open
    if (this.radioGroup) {
      this.radioGroup.focus();
    }
  };

  handleCancel = () => {
    this.props.onClose();
  };

  handleOk = () => {
    this.props.onClose(this.state.value);
  };

  handleChange = (event: SyntheticEvent<>, value: string) => {
    this.setState({ value });
  };

  render() {
    const { title, open, options } = this.props;
    const { value } = this.state;

    return (
      <Dialog open={open} onEntering={this.handleEntering} onClose={this.handleCancel}>
        <DialogTitle>{title}</DialogTitle>

        <DialogContent>
          <RadioGroup
            ref={(node) => {
              this.radioGroup = node;
            }}
            value={value}
            onChange={this.handleChange}
          >
            {options.map(option => (
              <FormControlLabel
                value={option.flagState}
                key={option.flagState}
                control={<Radio />}
                label={option.label}
              />
            ))}
          </RadioGroup>
        </DialogContent>

        <DialogActions>
          <Button onClick={this.handleCancel}>Cancel</Button>
          <Button onClick={this.handleOk}>Select</Button>
        </DialogActions>
      </Dialog>
    );
  }
}

export default RadioOptionsDialogue;
