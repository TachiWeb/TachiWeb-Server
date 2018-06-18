// @flow
import React, { Component } from 'react';
import Typography from '@material-ui/core/Typography';
import 'rc-slider/assets/index.css';
import Slider, { createSliderWithTooltip } from 'rc-slider';
import { withStyles } from '@material-ui/core/styles';

const SliderWithTooltip = createSliderWithTooltip(Slider);

// need whiteSpace: 'pre' so it doesn't wrap. rc-slider's width was forcing them to be too small
const marginSlider = 24;
const marginButton = 8;
const styles = {
  leftText: {
    whiteSpace: 'pre',
    marginLeft: marginButton,
    marginRight: marginSlider,
  },
  rightText: {
    whiteSpace: 'pre',
    marginLeft: marginSlider,
    marginRight: marginButton,
  },
};

type Props = {
  classes: Object,
  pageCount: number,
  page: number,
  onJumpToPage: Function,
};

type State = {
  sliderValue: number,
};

// rc-slider is finicky. Use state.sliderValue as the value of the slider at all times
// update it onChange, and use onAfterChange to fire any actual events
//
// TODO: use Material-UI slider component if/when it releases

// FIXME: I added some CSS to index.css
//        ReaderOverlay has a z-index, which is interfering with the tooltip.
//        Ideally, this CSS wouldn't be necessary

class PageSlider extends Component<Props, State> {
  static getDerivedStateFromProps(nextProps) {
    // Set the initial sliderValue to always reflect the page # in the URL
    // 1 indexed for human readability
    return { sliderValue: nextProps.page + 1 };
  }

  state = {
    sliderValue: 1,
  };

  updateSliderValue = (value: number) => {
    this.setState({ sliderValue: value });
  };

  render() {
    const {
      pageCount, page, onJumpToPage, classes,
    } = this.props;
    const { sliderValue } = this.state;

    return (
      <React.Fragment>
        <Typography className={classes.leftText}>{`Page ${page + 1}`}</Typography>
        <SliderWithTooltip
          min={1}
          max={pageCount}
          value={sliderValue}
          onChange={this.updateSliderValue}
          onAfterChange={onJumpToPage}
          tipFormatter={value => `Page ${value}`}
        />
        <Typography className={classes.rightText}>{pageCount}</Typography>
      </React.Fragment>
    );
  }
}

export default withStyles(styles)(PageSlider);
