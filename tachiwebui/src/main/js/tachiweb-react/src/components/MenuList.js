// @flow
import React from 'react';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Link from 'components/Link';
import { Client } from 'api';

// Set the width of the menu
const styles = {
  list: {
    width: 250,
  },
};

type Props = { classes: Object };

const MenuList = ({ classes }: Props) => (
  <div className={classes.list}>
    <List>
      <ListItem>
        <Typography variant="title" gutterBottom>
          TachiWeb
        </Typography>
      </ListItem>
      <ListItem button component={Link} to={Client.library()}>
        <ListItemText primary="Library" />
      </ListItem>
      <ListItem button component={Link} to={Client.catalogue()}>
        <ListItemText primary="Catalogue" />
      </ListItem>
      <ListItem button>
        <ListItemText primary="Downloads" />
      </ListItem>
      <ListItem button>
        <ListItemText primary="Settings" />
      </ListItem>
      <ListItem button component={Link} to={Client.backupRestore()}>
        <ListItemText primary="Backup / Restore" />
      </ListItem>
    </List>
  </div>
);

export default withStyles(styles)(MenuList);
