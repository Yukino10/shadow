import { message, notification } from 'antd';
import { queryBaseServiceList } from '@/services/devices';

const Model = {
  namespace: 'baseService',
  state: {
    servicelist: [],
  },
  effects: {
    // 获取基础服务列表
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryBaseServiceList);
      if (response.msgCode === 0) {
        yield put({
          type: 'queryList',
          payload: response.data,
        });
      } else {
        notification.error({
          message: '获取列表信息失败',
          description: response.msg,
        });
      }
    },
  },
  reducers: {
    queryList(state, action) {
      return {
        ...state,
        servicelist: action.payload,
      };
    },
  },
};
export default Model;
