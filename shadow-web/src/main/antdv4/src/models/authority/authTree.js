import { notification } from 'antd';
import { queryTreeNode } from '@/services/auth';

const AuthTree = {
  namespace: 'authTree',

  state: {
    expandedKeys: [],
    checkedKeys: [],
    autoExpandParent: true,
    treeData: [],
  },

  effects: {
    *fetch({ callback }, { call, put }) {
      const response = yield call(queryTreeNode);
      if (response.msgCode === 0) {
        yield put({
          type: 'treeData',
          payload: response.data,
        });
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '获取角色列表失败',
          description: response.msg,
        });
      }
    },
  },

  reducers: {
    expandedKeys(state, action) {
      return {
        ...state,
        expandedKeys: action.payload,
      };
    },
    checkedKeys(state, action) {
      return {
        ...state,
        checkedKeys: action.payload,
      };
    },
    autoExpandParent(state, action) {
      return {
        ...state,
        autoExpandParent: action.payload,
      };
    },
    treeData(state, action) {
      return {
        ...state,
        treeData: action.payload,
      };
    },
  },
};

export default AuthTree;
