// @flow
import * as React from 'react';
import { withRouter } from 'react-router-dom';

// https://reacttraining.com/react-router/web/guides/scroll-restoration

// TODO: do I need to also reset scroll on componentDidMount()?

type Props = {
  location: Object, // props injected by react-router
  children?: React.Node,
};

class ScrollToTop extends React.Component<Props> {
  static defaultProps = { children: null };

  componentDidUpdate(prevProps) {
    if (this.props.location !== prevProps.location) {
      window.scrollTo(0, 0);
    }
  }

  render() {
    // You can use this to wrap children if you want.
    // Refer to the link at the top for reference.
    return this.props.children;
  }
}

export default withRouter(ScrollToTop);
