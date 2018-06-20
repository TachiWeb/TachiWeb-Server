// @flow
import { connect } from 'react-redux';
import { updateReadingStatus } from 'redux-ducks/chapters';
import ReadingStatusUpdater from 'components/reader/ReadingStatusUpdater';
import { withRouter } from 'react-router-dom';

type StateToProps = {
  mangaId: number,
  chapterId: number,
  page: number,
};

const mapStateToProps = (state, ownProps): StateToProps => {
  const { mangaId, chapterId, page } = ownProps.match.params;

  return {
    mangaId: parseInt(mangaId, 10),
    chapterId: parseInt(chapterId, 10),
    page: parseInt(page, 10),
  };
};

type DispatchToProps = {
  updateReadingStatus: Function,
};

const mapDispatchToProps = (dispatch): DispatchToProps => ({
  updateReadingStatus: (mangaId, chapterId, readPage) =>
    dispatch(updateReadingStatus(mangaId, chapterId, readPage)),
});

export type ReadingStatusUpdaterContainerProps = StateToProps & DispatchToProps;
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ReadingStatusUpdater));
