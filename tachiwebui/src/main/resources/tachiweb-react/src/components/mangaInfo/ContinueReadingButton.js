// @flow
import React from 'react';
import type { ChapterType } from 'types';
import Button from '@material-ui/core/Button';
import Icon from '@material-ui/core/Icon';
import Link from 'components/Link';
import { chapterNumPrettyPrint } from 'components/utils';

// The chapters list passed into this component should NOT be sorted or filtered.
// It iterates through the chapters array based on the array's natural order.
// Expecting chapters to be in ascending order.

// TODO: make sure that chapter_number is displayed correctly (rounding)
//      ^ Might want to create a utility function, and use it in the chapter list and reader too.

// TODO: add some spacing between the play icon and text

type Props = {
  chapters: Array<ChapterType>,
  mangaId: number,
  chapterUrl: Function,
}; // otherProps passed to Button

const ContinueReadingButton = ({
  chapters, mangaId, chapterUrl, ...otherProps
}: Props) => {
  if (!chapters.length) return null;

  const firstUnreadChapter = findFirstUnreadChapter(chapters);

  if (firstUnreadChapter) {
    const pageUrl = chapterUrl(mangaId, firstUnreadChapter.id, firstUnreadChapter.last_page_read);
    return (
      <Button variant="contained" color="primary" component={Link} to={pageUrl} {...otherProps}>
        <Icon>play_arrow</Icon>
        Continue Reading Ch. {chapterNumPrettyPrint(firstUnreadChapter.chapter_number)}
      </Button>
    );
  }
  return (
    <Button variant="contained" disabled {...otherProps}>
      All Chapters Read
    </Button>
  );
};

// Helper functions
function findFirstUnreadChapter(chapters): ?ChapterType {
  let firstUnreadChapter = null;

  // using Array.some() for a short-circuit-able iterator
  chapters.some((chapter) => {
    if (!chapter.read) {
      firstUnreadChapter = chapter;
      return true; // escape
    }
    return false; // continue
  });

  return firstUnreadChapter;
}

export default ContinueReadingButton;
