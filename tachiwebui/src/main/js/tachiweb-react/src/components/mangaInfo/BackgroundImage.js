// @flow
import * as React from 'react';

type Props = {
  coverUrl: string,
  children: React.Node, // require children to be passed
};

// * backgroundImage
// linear-gradient hack, put a white filter over the background image
const BackgroundImage = ({ coverUrl, children, ...otherProps }: Props) => (
  <div
    {...otherProps}
    style={{
      backgroundImage: `linear-gradient(rgba(255, 255, 255, 0.75), rgba(255, 255, 255, 0.75)), url(${coverUrl ||
        ''})`,
      backgroundRepeat: 'no-repeat',
      backgroundPosition: 'center center',
      backgroundSize: 'cover',
    }}
  >
    {children}
  </div>
);

export default BackgroundImage;
