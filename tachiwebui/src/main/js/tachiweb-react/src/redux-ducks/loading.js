// @flow

// Based on this great article
// https://medium.com/stashaway-engineering/react-redux-tips-better-way-to-handle-loading-flags-in-your-reducers-afda42a804c6
// weird array explaination
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Destructuring_assignment#Unpacking_values_from_a_regular_expression_match

// NOTE: the general naming you should follow when referencing this state is [thing]IsLoading
//       e.g. catalogueIsLoading

import some from 'lodash/some';
import get from 'lodash/get';

type State = { +[action: string]: boolean };

export default function loadingReducer(state: State = {}, action = {}) {
  const { type } = action;
  const matches = /(.*)_(REQUEST|SUCCESS|FAILURE)/.exec(type);

  // not a *_REQUEST / *_SUCCESS /  *_FAILURE actions, so we ignore them
  if (!matches) return state;

  const [, requestName, requestState] = matches;
  return {
    ...state,
    // Store whether a request is happening at the moment or not
    // e.g. will be true when receiving GET_TODOS_REQUEST
    //      and false when receiving GET_TODOS_SUCCESS / GET_TODOS_FAILURE
    [requestName]: requestState === 'REQUEST',
  };
}

// 'actions' should be an array of strings. Strings should be action prefixes.
// e.g. ['GET_TODOS'] corresponds to GET_TODOS_REQUEST, _SUCCESS, _FAILURE
export const createLoadingSelector = (actions: Array<string>) => (state: Object): boolean =>
  // returns true only when all actions is not loading
  some(actions, action => get(state, `loading.${action}`));
