// @flow
import * as React from 'react';
import ResponsiveGrid from 'components/ResponsiveGrid';
import type { MangaType } from 'types';

type Props = {
  mangaLibrary: Array<MangaType>,
  cardComponent: React.Element<any>, // single node only
};

// NOTE: You must pass a cardComponent, which is what will be rendered.
//       As of writing this, there is LibraryMangaCard and CatalogueMangaCard
// e.g. <MangaGrid mangaLibrary={mangaLibrary} cardComponent={<LibraryMangaCard />} />

// https://reactjs.org/docs/composition-vs-inheritance.html
// https://stackoverflow.com/questions/32370994/how-to-pass-props-to-this-props-children

const MangaGrid = ({ mangaLibrary, cardComponent }: Props) => (
  <ResponsiveGrid>
    {mangaLibrary.map(manga => React.cloneElement(cardComponent, { key: manga.id, manga }))}
  </ResponsiveGrid>
);

export default MangaGrid;
