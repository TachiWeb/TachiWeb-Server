// @flow
import * as React from 'react';
import MangaInfoHeader from 'components/mangaInfo/MangaInfoHeader';
import MangaInfoDetails from 'components/mangaInfo/MangaInfoDetails';
import SortFilterChaptersHOC from 'components/mangaInfo/SortFilterChaptersHOC';
import MangaInfoChapters from 'components/mangaInfo/MangaInfoChapters';
import type { MangaType, ChapterType } from 'types';
import FullScreenLoading from 'components/loading/FullScreenLoading';
import FavoriteFABContainer from 'containers/FavoriteFABContainer';
import ContinueReadingButton from 'components/mangaInfo/ContinueReadingButton';
import CenteredHOC from 'components/CenteredHOC';

type Props = {
  mangaInfo: ?MangaType,
  chapters: Array<ChapterType>,
  initialTabValue: number,
  onBackClick: string | Function,
  onRefreshClick: Function,
  isLoading: boolean,
  setFlag: Function,

  chapterUrl: Function,
};

type State = {
  tabValue: number,
};

// FEATURES TODO:
// mark as read
// mark as unread
// download
// delete

class MangaInfo extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      tabValue: props.initialTabValue,
    };
  }

  handleChangeTab = (event: SyntheticEvent<>, newValue: number) => {
    this.setState({ tabValue: newValue });
  };

  tabContent = (): React.Node => {
    const { tabValue } = this.state;
    const { mangaInfo, chapters, chapterUrl } = this.props;

    const numChapters: number = chapters ? chapters.length : 0;

    if (mangaInfo) {
      if (tabValue === 0) {
        return (
          <MangaInfoDetails mangaInfo={mangaInfo} numChapters={numChapters}>
            <FavoriteFABContainer mangaId={mangaInfo.id} />
          </MangaInfoDetails>
        );
      } else if (tabValue === 1) {
        const CenteredContinueReadingButton = CenteredHOC(ContinueReadingButton);
        const SortFilterMangaInfoChapters = SortFilterChaptersHOC(MangaInfoChapters);

        return (
          <React.Fragment>
            <CenteredContinueReadingButton
              chapters={chapters}
              mangaId={mangaInfo.id}
              chapterUrl={chapterUrl}
              style={{ marginBottom: 24 }}
            />

            <SortFilterMangaInfoChapters
              mangaInfo={mangaInfo}
              chapters={chapters}
              chapterUrl={chapterUrl}
            />
          </React.Fragment>
        );
      }
    }
    return null;
  };

  render() {
    const { tabValue } = this.state;
    const {
      mangaInfo, onBackClick, onRefreshClick, isLoading, setFlag,
    } = this.props;

    return (
      <React.Fragment>
        <MangaInfoHeader
          mangaInfo={mangaInfo}
          tabValue={tabValue}
          handleChangeTab={this.handleChangeTab}
          onBackClick={onBackClick}
          onRefreshClick={onRefreshClick}
          setFlag={setFlag}
        />
        {this.tabContent()}

        {isLoading && <FullScreenLoading />}
      </React.Fragment>
    );
  }
}

export default MangaInfo;
