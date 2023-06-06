import { notification, message } from 'antd';
import {
  create,
  queryProductList,
  queryProductTableList,
  deleteProduct,
  deleteProductList,
  packUp,
  updateProduct,
  getProductVersion,
  deleteProductVersion,
  isUpdated,
  packageValidate,
} from '@/services/product';

function isEmpty(obj) {
  return obj === undefined || obj === '';
}
const ProductModel = {
  namespace: 'product',
  state: {
    createModalVisible: false,
    editModalVisible: false,
    versionModalVisible: false,
    versionNoModalVisible: false,
    record: {},
    versionList: [],
    data: {
      list: [],
      pagination: {},
    },
    selectedRows: [],
    formValues: {},
  },
  effects: {
    // 获取产品表格列表
    *fetchList({ payload }, { call, put }) {
      const response = yield call(queryProductTableList);
      if (response.msgCode === 0) {
        yield put({
          type: 'queryTableList',
          payload: response.data,
        });
      } else {
        notification.error({
          message: '获取产品列表信息失败',
          description: response.msg,
        });
      }
    },
    // 新增产品信息
    *create({ payload, callback }, { call }) {
      const response = yield call(create, payload);
      console.log('新增产品信息：', response);
      if (response.msgCode === 0) {
        message.success('新增产品信息成功');
        if (callback) callback();
      } else if (response.msgCode === 1) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '新增产品信息失败',
          description: response.msg,
        });
      }
    },
    *delete({ payload, callback }, { call }) {
      const response = yield call(deleteProduct, payload);
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
    *remove({ callback, payload }, { call, put }) {
      const response = yield call(deleteProductList, payload);
      if (response.msgCode === 0) {
        if (callback) {
          callback();
        }
      } else {
        notification.error({
          message: '删除产品失败',
          description: response.msg,
        });
      }
    },
    *update({ payload, callback }, { call }) {
      const response = yield call(updateProduct, payload);
      console.log('更新产品信息：', response);
      if (response.msgCode === 0) {
        message.success('修改成功');
        if (callback) callback();
      } else {
        notification.error({
          message: '修改产品信息失败',
          description: response.msg,
        });
      }
    },
    *pack({ payload, callback }, { call }) {
      const response = yield call(packUp, payload);
      console.log('打包：', response);
      if (response.msgCode === 0) {
        message.success('成功');
        if (callback) callback();
      } else {
        notification.error({
          message: '失败',
          description: response.msg,
        });
      }
    },
    *getVersion({ payload, callback }, { call, put }) {
      const response = yield call(getProductVersion, payload);
      if (response.msgCode === 0) {
        yield put({
          type: 'saveVersionList',
          payload: response.data,
        });
      } else {
        notification.error({
          message: '获取产品版本信息失败',
          description: response.msg,
        });
      }
    },
    *deleteProductVersion({ payload, callback }, { call }) {
      const response = yield call(deleteProductVersion, payload);
      if (response.msgCode === 0) {
        message.success('删除成功');
        if (callback) callback();
      } else {
        notification.error({
          message: '删除版本信息失败',
          description: response.msg,
        });
      }
    },
    *isUpdated({ payload, callback }, { call }) {
      const response = yield call(isUpdated, payload);
      if (response.msgCode === 0) {
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '产品打包失败',
          description: response.msg,
        });
      }
    },
    *packageValidate({ payload, callback }, { call }) {
      const response = yield call(packageValidate, payload);
      if (response.msgCode === 0) {
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '产品打包失败',
          description: response.msg,
        });
      }
    },
  },
  reducers: {
    queryList(state, action) {
      return {
        ...state,
        list: action.payload,
      };
    },
    queryTableList(state, action) {
      return {
        ...state,
        data: action.payload,
      };
    },
    setFormValues(state, action) {
      return {
        ...state,
        formValues: action.payload,
      };
    },
    changeCreateModal(state, { payload }) {
      return {
        ...state,
        createModalVisible: payload,
      };
    },
    changeEditModalVisible(state, { payload }) {
      return {
        ...state,
        editModalVisible: payload,
      };
    },
    changeVersionModalVisible(state, { payload }) {
      return {
        ...state,
        versionModalVisible: payload,
      };
    },
    changeVersionNoModalVisible(state, { payload }) {
      return {
        ...state,
        versionNoModalVisible: payload,
      };
    },
    saveRecord(state, action) {
      return {
        ...state,
        record: action.payload,
      };
    },
    saveVersionList(state, action) {
      return {
        ...state,
        versionList: action.payload,
      };
    },
    // 保存选中行信息
    setSelectRows(state, action) {
      const idList = [];
      for (let i = 0; i < action.payload.length; i++) {
        idList.push(action.payload[i].id);
      }
      return {
        ...state,
        selectedRows: idList,
      };
    },
  },
};
export default ProductModel;
