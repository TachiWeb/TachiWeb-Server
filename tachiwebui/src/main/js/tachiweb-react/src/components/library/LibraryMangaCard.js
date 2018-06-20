// @flow
import React from 'react';
import Grid from '@material-ui/core/Grid';
import { withStyles } from '@material-ui/core/styles';
import Badge from '@material-ui/core/Badge';
import ButtonBase from '@material-ui/core/ButtonBase';
import MangaCard from 'components/MangaCard';
import Link from 'components/Link';
import { Server, Client } from 'api';
import type { MangaType } from 'types';
import classNames from 'classnames';

// TODO: Currently passing in the entire unread object, not just the corresponding number
//       ^ Would have to rework the component tree a big to make that happen.

const styles = {
  fullWidth: {
    // While the grid item is full width, it's children aren't.
    // Need to apply width 100% multiple levels down to make things stretch correctly.
    width: '100%',
  },
  invisibleBadge: {
    // Hide the badge via CSS when there's no unread manga
    visibility: 'hidden',
  },
  badge: {
    top: -8,
    right: -8, // Fixes badge overflowing on the x-axis
  },
};

type Props = {
  classes: Object,
  manga: MangaType,
  unread: { [mangaId: number]: number },
};

const LibraryMangaCard = ({ classes, manga, unread }: Props) => {
  const badgeClass = classNames(
    classes.badge,
    // make badge invisible when this manga's unread count is 0 (or == null)
    { [classes.invisibleBadge]: !unread[manga.id] },
  );

  return (
    <Grid item xs={6} sm={3}>
      <Badge
        badgeContent={unread[manga.id] || 0}
        color="primary"
        className={classes.fullWidth}
        classes={{ badge: badgeClass }}
      >
        <ButtonBase className={classes.fullWidth} component={Link} to={Client.manga(manga.id)}>
          <MangaCard title={manga.title} coverUrl={Server.cover(manga.id)} />
        </ButtonBase>
      </Badge>
    </Grid>
  );
};

export default withStyles(styles)(LibraryMangaCard);
