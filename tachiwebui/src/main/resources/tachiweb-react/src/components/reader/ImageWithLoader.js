// @flow
import React, { Component } from 'react';
import CenteredLoading from 'components/loading/CenteredLoading';
import Button from '@material-ui/core/Button';
import Icon from '@material-ui/core/Icon';
import { withStyles } from '@material-ui/core/styles';

// https://www.javascriptstuff.com/detect-image-load/

// I'm manually setting the image's key. Possibly a little hacky?
// But this fixes 2 problems by forcefully refreshing the <img> when its key changes.
//
//   1. If you change page and the new image needs time to load, React will continue
//      to show the previous image until the new image loads.
//      I believe this is a quirk of how React diffs images. (even though the src is changed)
//
//   2. If you successfully reload the image after it fails (handleRetryClick),
//      for some reason, it won't actually show the new image.
//      It instead shows the image error placeholder as if it failed.
//      I'm guessing React is confused that it's image (in cache) changed, but the src/key didn't.

const styles = {
  verticallyCenter: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
  },
};

type Props = {
  classes: Object, // injected styles (for this component)
  src: ?string, // if src == null, <img> will not render
  alt: string,
  notLoadedHeight?: number | string, // any valid height
}; // extra props will be passed to <img>

type State = {
  status: 'LOADING' | 'LOADED' | 'FAILED',
  retries: number,
};

class ImageWithLoader extends Component<Props, State> {
  static defaultProps = {
    notLoadedHeight: '40vh',
  };

  state = {
    status: 'LOADING',
    retries: 0,
  };

  /* eslint-disable react/no-did-update-set-state */
  componentDidUpdate(prevProps: Props) {
    // Changing the img src doesn't trigger any event to update
    // status so you have to do it manually.
    if (prevProps.src !== this.props.src) {
      this.setState({
        status: 'LOADING',
        retries: 0,
      });
    }
  }

  handleImageLoad = () => this.setState({ status: 'LOADED' });

  handleImageError = () => this.setState({ status: 'FAILED' });

  handleRetryClick = () => {
    const { src } = this.props;
    if (!src) return;

    // https://stackoverflow.com/questions/23160107/javascript-how-to-retry-loading-an-image-without-appending-query-string
    this.setState(prevState => ({
      status: 'LOADING',
      retries: prevState.retries + 1,
    }));

    const img = new Image();
    img.onload = this.handleImageLoad;
    img.onerror = this.handleImageError;
    img.src = src;
  };

  render() {
    const {
      classes, src, alt, notLoadedHeight, ...otherProps
    } = this.props;
    const { status, retries } = this.state;

    const imgStyles = {
      width: '100%',
      // Only show when image is loaded
      display: status === 'LOADED' ? 'block' : 'none',
    };

    return (
      <React.Fragment>
        {src && (
          <img
            {...otherProps}
            onLoad={this.handleImageLoad}
            onError={this.handleImageError}
            src={src}
            alt={alt}
            style={imgStyles}
            key={`${src}-${retries}`}
          />
        )}

        {status === 'LOADING' && (
          <div className={classes.verticallyCenter} style={{ height: notLoadedHeight }}>
            <CenteredLoading />
          </div>
        )}
        {status === 'FAILED' && (
          <div className={classes.verticallyCenter} style={{ height: notLoadedHeight }}>
            <Button variant="contained" onClick={this.handleRetryClick}>
              <Icon>refresh</Icon>
              Retry
            </Button>
          </div>
        )}
      </React.Fragment>
    );
  }
}

export default withStyles(styles)(ImageWithLoader);
