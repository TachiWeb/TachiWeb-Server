import React from 'react';
import ReactDOM from 'react-dom';
import { createStore, applyMiddleware } from 'redux';
import { Provider } from 'react-redux';
import thunk from 'redux-thunk';
import logger from 'redux-logger';
import { composeWithDevTools } from 'redux-devtools-extension';
import CssBaseline from '@material-ui/core/CssBaseline';
import Router from './routes';
import './index.css';
import registerServiceWorker from './registerServiceWorker';
import rootReducer from './redux-ducks';

// Redux
const store = createStore(rootReducer, composeWithDevTools(applyMiddleware(thunk, logger)));
// Redux hot reloading
if (process.env.NODE_ENV !== 'production') {
  if (module.hot) {
    module.hot.accept('./redux-ducks', () => {
      store.replaceReducer(rootReducer);
    });
  }
}

ReactDOM.render(
  <React.Fragment>
    <CssBaseline />
    <Provider store={store}>
      <Router />
    </Provider>
  </React.Fragment>,
  document.getElementById('root'),
);
registerServiceWorker();

// React hot reloading
if (module.hot) {
  module.hot.accept('./routes', () => {
    ReactDOM.render(
      <React.Fragment>
        <CssBaseline />
        <Provider store={store}>
          <Router />
        </Provider>
      </React.Fragment>,
      document.getElementById('root'),
    );
  });
}

// NOTE: this type of hot reload does not preserve state.
// Since I've enabled redux hot reloading, I'm not sure how much of a problem this is.
// https://daveceddia.com/hot-reloading-create-react-app/
// https://github.com/facebook/create-react-app/issues/2317
