// @flow
import * as React from 'react';

// I have yet to really figure out how to type HOCs correctly
// HOC flow types based on this
// https://github.com/facebook/flow/issues/4759#issuecomment-327896001
// https://flow.org/en/docs/react/hoc/

// FIXME: Supporting defaultProps, seems like a pain
// https://flow.org/en/docs/react/hoc/#toc-supporting-defaultprops-with-react-elementconfig

type Props = {
  className: ?string, // optional
};

/* eslint-disable react/prefer-stateless-function */
// Having a named class allows it to show up in react dev tools
function CenteredHOC<OutputProps: {}, InputProps: OutputProps & Props>(WrappedComponent: React.ComponentType<OutputProps>): React.ComponentType<InputProps> {
  return class withCenteredHOC extends React.Component<InputProps> {
    static defaultProps = {
      className: null,
    };

    render() {
      const { className, ...otherProps } = this.props;

      const centerStyle = {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'flex-start', // children height auto
      };

      return (
        <div style={centerStyle} className={className}>
          <WrappedComponent {...otherProps} />
        </div>
      );
    }
  };
}

export default CenteredHOC;
