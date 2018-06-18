// @flow
import * as React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Link from 'components/Link';
import Icon from '@material-ui/core/Icon';
import IconButton from '@material-ui/core/IconButton';
import { withStyles } from '@material-ui/core/styles';
import PageSlider from 'components/reader/PageSlider';
import { chapterNumPrettyPrint } from 'components/utils';

// TODO: using two toolbars currently, but it might be too big. Consider changing/customizing later.

// NOTE: Material-UI v1 hasn't ported a slider component yet, so using an external library.
//       When it is added to Material-UI, consider using that instead.
//       https://github.com/mui-org/material-ui/issues/4793

const styles = {
  overlay: {
    // Overlay it above the image
    width: '100%',
    position: 'fixed',
    zIndex: 1,

    // Visible only on hover
    opacity: 0,
    transition: 'opacity .2s ease-in-out',
    '&:hover': {
      opacity: 1,
    },
  },
};

type Props = {
  classes: Object, // injected styles
  title: string,
  chapterNum: number,
  pageCount: number,
  page: number,
  backUrl: string,
  prevChapterUrl: ?string,
  nextChapterUrl: ?string,
  onJumpToPage: Function,
};

const ReaderOverlay = ({
  classes,
  title,
  chapterNum,
  pageCount,
  page,
  backUrl,
  prevChapterUrl,
  nextChapterUrl,
  onJumpToPage,
}: Props) => (
  <AppBar position="static" color="default" className={classes.overlay}>
    <Toolbar>
      <IconButton component={Link} to={backUrl}>
        <Icon>arrow_back</Icon>
      </IconButton>

      <Typography variant="title" style={{ flex: 1 }}>
        {title}
      </Typography>

      <Typography variant="subheading">Chapter {chapterNumPrettyPrint(chapterNum)}</Typography>
    </Toolbar>

    <Toolbar>
      <IconButton component={Link} to={prevChapterUrl} disabled={!prevChapterUrl}>
        <Icon>skip_previous</Icon>
      </IconButton>

      <PageSlider pageCount={pageCount} page={page} onJumpToPage={onJumpToPage} />

      <IconButton component={Link} to={nextChapterUrl} disabled={!nextChapterUrl}>
        <Icon>skip_next</Icon>
      </IconButton>
    </Toolbar>
  </AppBar>
);

export default withStyles(styles)(ReaderOverlay);
