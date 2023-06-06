import { message, notification } from 'antd';
import {
  createDevice,
  createDeviceProp,
  deleteDevice,
  deleteDeviceProp,
  queryDevicesList,
  queryDevicesPropList,
} from '@/services/devices';

function isEmpty(obj) {
  return obj === undefined || obj === '';
}

const Model = {
  namespace: 'devices',
  state: {
    data: {
      list: [],
      pagination: {},
    },
    propList: [],
    propChildList: [],
    deviceData: {
      id: null,
      deviceName: null,
      deviceTag: null,
      description: '',
    },
    deviceCreateVisible: false,
    createModalVisible: false,
    editModalVisible: false,
    propCreateVisible: false,
    structElement: 'base',
    modalState: 'create',
    viewRecord: undefined,
  },
  effects: {
    // 获取产品列表
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryDevicesList);
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
    *delete({ payload, callback }, { call }) {
      const response = yield call(deleteDevice, payload);
      if (response.msgCode === 0) {
        message.success('删除成功');
        if (callback) callback();
      } else {
        notification.error({
          message: '删除产品信息失败',
          description: response.msg,
        });
      }
    },
    *create({ payload, callback }, { call }) {
      const response = yield call(createDevice, payload);
      if (response.msgCode === 0) {
        message.success('新增产品信息成功');
        if (callback) callback();
      } else if (response.msgCode === 1) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '新增物模型信息失败',
          description: response.msg,
        });
      }
    },
    *fetchProp({ payload }, { call, put }) {
      const response = yield call(queryDevicesPropList, payload);
      if (response.msgCode === 0) {
        yield put({
          type: 'queryPropList',
          payload: response.data,
        });
      } else {
        notification.error({
          message: '获取列表信息失败',
          description: response.msg,
        });
      }
    },
    *deleteProp({ payload, callback }, { call }) {
      const response = yield call(deleteDeviceProp, payload);
      if (response.msgCode === 0) {
        message.success('删除成功');
        if (callback) callback();
      } else {
        notification.error({
          message: '删除产品信息失败',
          description: response.msg,
        });
      }
    },
    *createDeviceProp({ payload, callback }, { call }) {
      const response = yield call(createDeviceProp, payload);
      if (response.msgCode === 0) {
        message.success('新增产品信息成功');
        if (callback) callback();
      } else if (response.msgCode === 1) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '新增物模型信息失败',
          description: response.msg,
        });
      }
    },
  },
  reducers: {
    queryList(state, action) {
      return {
        ...state,
        data: action.payload,
      };
    },
    changeCreateModal(state, { payload }) {
      return {
        ...state,
        createModalVisible: payload,
      };
    },
    changePropCreateModal(state, { payload }) {
      return {
        ...state,
        propCreateVisible: payload,
      };
    },
    changeStructElement(state, { payload }) {
      return {
        ...state,
        structElement: payload,
      };
    },
    changeDeviceCreateModal(state, { payload }) {
      return {
        ...state,
        deviceCreateVisible: payload,
      };
    },
    setDeviceDetail(state, { payload }) {
      return {
        ...state,
        deviceData: payload,
      };
    },
    queryPropList(state, { payload }) {
      return {
        ...state,
        propList: payload,
      };
    },
    queryPropChildList(state, { payload }) {
      return {
        ...state,
        propChildList: payload,
      };
    },
    handleModal(state, { payload }) {
      return {
        ...state,
        modalState: payload,
      };
    },
    handleRecord(state, { payload }) {
      return {
        ...state,
        viewRecord: payload,
      };
    },
  },
};
export default Model;
