// @flow
import { connect } from 'react-redux';
import ErrorNotifications from 'components/ErrorNotifications';
import { allErrorsSelector } from 'redux-ducks/error';

const mapStateToProps = state => ({
  errorMessage: allErrorsSelector(state),
});

export default connect(mapStateToProps)(ErrorNotifications);
