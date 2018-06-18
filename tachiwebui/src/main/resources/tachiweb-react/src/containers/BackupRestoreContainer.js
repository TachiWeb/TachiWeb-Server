// @flow
import { connect } from 'react-redux';
import { uploadRestoreData, UPLOAD_RESTORE } from 'redux-ducks/library';
import BackupRestore from 'pages/BackupRestore';
import { createLoadingSelector } from 'redux-ducks/loading';

const uploadRestoreIsLoading = createLoadingSelector([UPLOAD_RESTORE]);

type StateToProps = { uploadRestoreIsLoading: boolean };

const mapStateToProps = state =>
  ({ uploadRestoreIsLoading: uploadRestoreIsLoading(state) }: StateToProps);

type DispatchToProps = { uploadRestoreData: Function };

const mapDispatchToProps = (dispatch): DispatchToProps => ({
  uploadRestoreData: file => dispatch(uploadRestoreData(file)),
});

export type BackupRestoreContainerProps = StateToProps & DispatchToProps;
export default connect(mapStateToProps, mapDispatchToProps)(BackupRestore);
