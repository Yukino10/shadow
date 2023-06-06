import { message, notification } from 'antd/lib/index';
import {
  queryRole,
  addRole,
  validRoleName,
  queryUserRoles,
  queryRoleFunctions,
  updateRole,
  removeRole,
  changeUpdate,
  addRolePermission,
} from '@/services/role';

const Role = {
  namespace: 'role',

  state: {
    data: {
      list: [],
      pagination: {},
    },
    createModalVisible: false,
    updateModalVisible: false,
    changeModalVisible: false,
    authModalVisible: false,
    updateRole: {},
    formValues: {},
    selectedRows: [],
    versionList: [{ id: 0, name: '版本A' }, { id: 1, name: '版本B' }, { id: 2, name: '版本C' }],
  },

  effects: {
    *fetch({ payload, callback }, { call, put }) {
      const response = yield call(queryRole, payload);
      if (response.msgCode === 0) {
        yield put({
          type: 'save',
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

    *fetchUserRoles({ payload, callback }, { call }) {
      const response = yield call(queryUserRoles, payload);
      if (response.msgCode === 0) {
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '获取已选人员失败',
          description: response.msg,
        });
      }
    },

    *fetchRoleFunctions({ payload, callback }, { call }) {
      const response = yield call(queryRoleFunctions, payload);
      if (response.msgCode === 0) {
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '获取已选权限信息失败',
          description: response.msg,
        });
      }
    },

    *add({ payload, callback }, { call }) {
      const response = yield call(addRole, payload);
      if (response.msgCode === 0) {
        message.success('添加成功');
        if (callback) callback(true);
      } else if (response.msgCode === 2) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '新增角色失败',
          description: response.msg,
        });
      }
    },

    *update({ payload, callback }, { call, put }) {
      const response = yield call(updateRole, payload);
      if (response.msgCode === 0) {
        message.success('更新成功');
        if (callback) callback(true);
      } else if (response.msgCode === 2) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '更新失败',
          description: response.msg,
        });
      }
    },

    *addAuth({ payload, callback }, { call }) {
      const response = yield call(addRolePermission, payload);
      if (response.msgCode === 0) {
        message.success('添加成功');
        if (callback) callback(true);
      } else if (response.msgCode === 2) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '新增角色失败',
          description: response.msg,
        });
      }
    },

    *remove({ payload, callback }, { call, put }) {
      const response = yield call(removeRole, payload);
      if (response.msgCode === 0) {
        message.success('删除成功');
        yield put({
          type: 'saveSelectedRows',
          payload: [],
        });
        if (callback) callback();
      } else if (response.msgCode === 2) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '删除失败',
          description: response.msg,
        });
      }
    },

    *validRoleName({ payload, callback }, { call }) {
      const response = yield call(validRoleName, payload);
      if (response.msgCode === 0) {
        if (callback) callback();
      } else if (callback) callback(response.msg);
    },

    *changeUpdate({ payload, callback }, { call }) {
      const response = yield call(changeUpdate, payload);
      if (response.msgCode === 0) {
        message.success('完成');
        if (callback) callback(true);
      } else if (response.msgCode === 2) {
        message.error(response.msg);
      } else {
        notification.error({
          message: '设置人员失败',
          description: response.msg,
        });
      }
    },

    *getCompanyList({ callback, payload }, { call, put }) {
      const response = yield call(queryCompanyList);
      if (response.msgCode === 0) {
        yield put({
          type: 'saveCompanyList',
          payload: response.data,
        });
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '获取公司列表失败',
          description: response.msg,
        });
      }
    },
  },

  reducers: {
    save(state, action) {
      return {
        ...state,
        data: action.payload,
      };
    },

    setCreateModalVisible(state, action) {
      return {
        ...state,
        createModalVisible: action.payload,
      };
    },

    setUpdateModalVisible(state, action) {
      return {
        ...state,
        updateModalVisible: action.payload,
      };
    },

    setChangeModalVisible(state, action) {
      return {
        ...state,
        changeModalVisible: action.payload,
      };
    },

    setAuthModalVisible(state, action) {
      return {
        ...state,
        authModalVisible: action.payload,
      };
    },

    saveSelectedRows(state, action) {
      return {
        ...state,
        selectedRows: action.payload,
      };
    },

    setFormValues(state, action) {
      return {
        ...state,
        formValues: action.payload,
      };
    },

    saveUpdateRole(state, action) {
      return {
        ...state,
        updateRole: action.payload,
      };
    },

    saveVersionList(state, action) {
      return {
        ...state,
        versionList: action.payload,
      };
    },
  },
};

export default Role;
