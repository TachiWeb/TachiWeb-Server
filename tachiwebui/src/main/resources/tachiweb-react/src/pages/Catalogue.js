// @flow
import React, { Component } from 'react';
import debounce from 'lodash/debounce';
import MangaGrid from 'components/MangaGrid';
import CatalogueMangaCard from 'components/catalogue/CatalogueMangaCard';
import Waypoint from 'react-waypoint';
import DynamicSourceFilters from 'components/filters/DynamicSourceFilters';
import ResponsiveGrid from 'components/ResponsiveGrid';
import CatalogueHeader from 'components/catalogue/CatalogueHeader';
import CenteredLoading from 'components/loading/CenteredLoading';
import FullScreenLoading from 'components/loading/FullScreenLoading';
import type { FilterAnyType } from 'types/filters';
import type { CatalogueContainerProps } from 'containers/CatalogueContainer';
import Typography from '@material-ui/core/Typography';
import { withStyles } from '@material-ui/core/styles';
import { Helmet } from 'react-helmet';

// TODO: keep previous scroll position when going back from MangaInfo -> Catalogue

// FIXME: If you type something into the search bar,
//        then delete everything, searching breaks (no results)

// TODO: If you update search, then change it back to it's original value, don't search again?

const styles = {
  loading: {
    marginTop: 24,
    marginBottom: 40,
  },
  noMoreResults: {
    marginTop: 40,
    marginBottom: 60,
  },
};

class Catalogue extends Component<CatalogueContainerProps & { classes: Object }> {
  componentDidMount() {
    const {
      sources, sourceId, fetchSources, fetchCatalogue, fetchFilters, changeSourceId,
    } = this.props;

    // https://github.com/babel/babel/issues/2141
    // this is undefined in the promise, so manually bind this
    const that = this;

    // Only reload on component mount if it's missing data
    if (!sources.length || sourceId == null) {
      fetchSources().then(() => {
        changeSourceId(that.props.sources[0].id); // use the first available source
        fetchCatalogue();
        fetchFilters();
      });
    }

    // https://stackoverflow.com/questions/23123138/perform-debounce-in-react-js
    // Debouncing the search text
    this.delayedSearch = debounce(() => {
      fetchCatalogue();
    }, 500);
  }

  componentWillUnmount() {
    // Clean up debouncing function
    this.delayedSearch.cancel();
  }

  delayedSearch = () => null; // Placeholder, this gets replaced in componentDidMount()

  handleSourceChange = (event: SyntheticEvent<HTMLLIElement>) => {
    // NOTE: Using LIElement because that's how my HTML is structured.
    //       Doubt it'll cause problems, but change this or the actual component if needed.
    const {
      sources, changeSourceId, resetCatalogue, fetchFilters, fetchCatalogue,
    } = this.props;

    const newSourceIndex = parseInt(event.currentTarget.dataset.value, 10);
    const newSourceId = sources[newSourceIndex].id;

    resetCatalogue();
    changeSourceId(newSourceId);
    fetchFilters(); // call before fetchCatalogue so filters don't get used between sources
    fetchCatalogue();
  };

  handleSearchChange = (event: SyntheticEvent<HTMLInputElement>) => {
    // https://stackoverflow.com/questions/23123138/perform-debounce-in-react-js
    this.props.updateSearchQuery(event.currentTarget.value);
    this.delayedSearch();
  };

  handleLoadNextPage = () => {
    const {
      hasNextPage, fetchNextCataloguePage, catalogueIsLoading,
    } = this.props;

    if (hasNextPage && !catalogueIsLoading) {
      fetchNextCataloguePage();
    }
  };

  handleResetFilters = () => {
    this.props.resetFilters();
  };

  handleFilterChange = (newFilters: Array<FilterAnyType>) => {
    this.props.updateCurrentFilters(newFilters);
  };

  handleSearchFilters = () => {
    const { fetchCatalogue, updateLastUsedFilters } = this.props;

    updateLastUsedFilters(); // Must come before fetchCatalogue. This is a synchronous function.
    fetchCatalogue();
  };

  render() {
    const {
      classes,
      mangaLibrary,
      sources,
      sourcesAreLoading,
      catalogueIsLoading,
      currentFilters,
      searchQuery,
      sourceId,
      hasNextPage,
    } = this.props;

    const noMoreResults = !catalogueIsLoading && !sourcesAreLoading && !hasNextPage;

    return (
      <React.Fragment>
        <Helmet>
          <title>Catalogue - TachiWeb</title>
        </Helmet>

        <CatalogueHeader
          sourceId={sourceId}
          sources={sources}
          searchQuery={searchQuery}
          onSourceChange={this.handleSourceChange}
          onSearchChange={this.handleSearchChange}
        />

        <ResponsiveGrid>
          <DynamicSourceFilters
            filters={currentFilters}
            onResetClick={this.handleResetFilters}
            onSearchClick={this.handleSearchFilters}
            onFilterChange={this.handleFilterChange}
          />
        </ResponsiveGrid>

        <MangaGrid
          mangaLibrary={mangaLibrary}
          cardComponent={<CatalogueMangaCard />}
        />
        {mangaLibrary.length > 0 && (
          <Waypoint onEnter={this.handleLoadNextPage} bottomOffset={-300} />
        )}

        {catalogueIsLoading && <CenteredLoading className={classes.loading} />}
        {sourcesAreLoading && <FullScreenLoading />}
        {noMoreResults &&
          <Typography variant="caption" align="center" className={classes.noMoreResults}>
            No more results
          </Typography>
        }
      </React.Fragment>
    );
  }
}

export default withStyles(styles)(Catalogue);
