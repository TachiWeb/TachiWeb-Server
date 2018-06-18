// @flow
import React from 'react';
import ListItem from '@material-ui/core/ListItem';
import Moment from 'moment';
import Grid from '@material-ui/core/Grid';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import classNames from 'classnames';
import Link from 'components/Link';
import type { ChapterType, MangaType } from 'types';
import { chapterNumPrettyPrint } from 'components/utils';

// TODO: add additional actions such as mark as read/unread.
// TODO: align the bottom row text? It's a little off horizontally right now.

const styles = () => ({
  read: {
    color: '#AAA',
  },
});

type Props = {
  classes: Object,
  mangaInfo: MangaType,
  chapter: ChapterType,
  chapterUrl: Function,
};

const ChapterListItem = ({
  classes, mangaInfo, chapter, chapterUrl,
}: Props) => {
  const dimIfRead: Function = (read: boolean): String => classNames({ [classes.read]: read });
  const goToPage: number = chapter.read ? 0 : chapter.last_page_read;
  const chapterName: string =
    mangaInfo.flags.DISPLAY_MODE === 'NAME'
      ? chapter.name
      : `Chapter ${chapterNumPrettyPrint(chapter.chapter_number)}`;

  return (
    <ListItem button divider component={Link} to={chapterUrl(mangaInfo.id, chapter.id, goToPage)}>
      <Grid container>
        <Grid item xs={12}>
          <Typography variant="subheading" className={dimIfRead(chapter.read)}>
            {chapterName}
          </Typography>
        </Grid>
        <Grid item style={{ flex: 1 }}>
          <Typography variant="caption" className={dimIfRead(chapter.read)}>
            {Moment(chapter.date).format('L')}
          </Typography>
        </Grid>
        <Grid item>
          <Typography>{chapterText(chapter.read, chapter.last_page_read)}</Typography>
        </Grid>
      </Grid>
    </ListItem>
  );
};

// Helper Functions
/* eslint-disable camelcase */
function chapterText(read: boolean, last_page_read: number) {
  let text: string = '';
  if (!read && last_page_read > 0) {
    text = `Page ${last_page_read + 1}`;
  }
  return text;
}
/* eslint-enable camelcase */

export default withStyles(styles)(ChapterListItem);
