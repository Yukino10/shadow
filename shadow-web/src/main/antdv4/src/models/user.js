import { notification } from 'antd';
import { queryCurrent, query as queryUsers, findAuthorities } from '../services/user';
import { getAuthority } from '@/utils/authority';

const UserModel = {
  namespace: 'user',
  state: {
    currentUser: {},
    authorities: [],
  },
  effects: {
    // 获取当前用户信息
    *fetchCurrent({ callback }, { call, put }) {
      const response = yield call(queryCurrent);
      if (response.msgCode === 0) {
        const { data } = response;
        yield put({
          type: 'saveCurrentUser',
          payload: data,
        });
        if (callback) {
          callback(data);
        }
      } else {
        notification.error({
          message: '获取当前用户基础信息失败',
          description: response.msg,
        });
      }
    },
    // 获取用户权限列表
    *fetchAuthorities({ callback }, { call, put }) {
      const authority = getAuthority();
      const response = yield call(findAuthorities, { username: authority });
      if (response.msgCode == 0) {
        yield put({
          type: 'saveAuthorities',
          payload: response.data,
        });
        if (callback) callback();
      } else {
        notification.error({
          message: '获取用户权限失败',
          description: response.msg,
        });
      }
    },
    *fetch(_, { call, put }) {
      const response = yield call(queryUsers);
      yield put({
        type: 'save',
        payload: response,
      });
    },
  },
  reducers: {
    saveCurrentUser(state, action) {
      console.log('当前用户信息', action.payload);
      return {
        ...state,
        currentUser: action.payload || {},
      };
    },
    saveAuthorities(state, action) {
      return {
        ...state,
        authorities: action.payload,
      };
    },
    changeNotifyCount(
      state = {
        currentUser: {},
      },
      action,
    ) {
      return {
        ...state,
        currentUser: {
          ...state.currentUser,
          notifyCount: action.payload.totalCount,
          unreadCount: action.payload.unreadCount,
        },
      };
    },
  },
};
export default UserModel;
