// @flow
import { connect } from 'react-redux';
import { uploadRestoreFile, UPLOAD_RESTORE } from 'redux-ducks/library';
import RestoreCard from 'components/backup-restore/RestoreCard';
import { createLoadingSelector } from 'redux-ducks/loading';
import { createErrorMessageSelector } from 'redux-ducks/error';

const restoreIsLoading = createLoadingSelector([UPLOAD_RESTORE]);
const restoreErrorMessage = createErrorMessageSelector([UPLOAD_RESTORE]);

type StateToProps = {
  restoreIsLoading: boolean,
  restoreFailed: boolean,
};

const mapStateToProps = (state): StateToProps => ({
  restoreIsLoading: restoreIsLoading(state),
  restoreFailed: !!restoreErrorMessage(state),
});

type DispatchToProps = { uploadRestoreFile: Function };

const mapDispatchToProps = (dispatch): DispatchToProps => ({
  uploadRestoreFile: file => dispatch(uploadRestoreFile(file)),
});

export type RestoreCardContainerProps = StateToProps & DispatchToProps;
export default connect(mapStateToProps, mapDispatchToProps)(RestoreCard);
