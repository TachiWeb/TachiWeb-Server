// @flow
import * as React from 'react';
import { Link as ReactRouterLink } from 'react-router-dom';

// React-Router Link requires a non-null prop 'to'.
// However, there are times when you dynamically create Links that might not exist
// e.g. link to next chapter might not exist

// React-Router decided they won't change this behavior ¯\_(ツ)_/¯
// https://github.com/ReactTraining/react-router/issues/2319

// Fall back on <button>, assuming you're using it in conjunction with a Material-UI Button
//
// If you need a fallback other than <button> in the future, consider an optional
// prop to allow selecting which fallback element to use.

type Props = {
  to: ?string,
}; // all props will be passed to the <Link> or <a>

const Link = (props: Props) => {
  const Component = props.to ? ReactRouterLink : 'button';
  return <Component {...props} />;
};

export default Link;
