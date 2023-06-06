import { notification, message } from 'antd';
import {
  queryCompanyList,
  queryPerson,
  queryRoleList,
  resetPass,
  validName,
  addPerson,
  deleteUser,
  getSubordinateList,
} from '@/services/person';

const Person = {
  namespace: 'person',

  state: {
    // 分页数据项
    data: {
      list: [],
      pagination: {},
    },
    // 信息
    record: {},
    // 用户开户modal框显示状态
    modalVisible: false,
    // 是否展开查询条件
    expandSearchForm: false,
    // 选中行的内容
    selectedRows: [],
    // 搜索框内容
    formValues: {},

    companyList: [],
    roleList: [],

    // 下级员工
    subordinate: [],
  },

  effects: {
    *fetch({ callback, payload }, { call, put }) {
      const response = yield call(queryPerson, payload);
      if (response.msgCode === 0) {
        yield put({
          type: 'save',
          payload: response.data,
        });
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '获取人员列表失败',
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

    *getRoleList({ callback, payload }, { call, put }) {
      const response = yield call(queryRoleList, payload);
      if (response.msgCode === 0) {
        yield put({
          type: 'saveRoleList',
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

    *validPersonName({ payload, callback }, { call }) {
      const response = yield call(validName, payload);
      if (response.msgCode === 0) {
        if (callback) callback();
      } else if (callback) callback(response.msg);
    },

    *resetPassword({ payload, callback }, { call }) {
      const response = yield call(resetPass, payload);
      if (response.msgCode === 0) {
        if (callback) callback();
        message.info('成功');
      } else if (callback) callback(response.msg);
    },
    *add({ payload, callback }, { call }) {
      const response = yield call(addPerson, payload);
      if (response.msgCode === 0) {
        if (callback) {
          callback(true);
        }
      } else {
        notification.error({
          message: '新增人员失败',
          description: response.msg,
        });
      }
    },
    // 删除人员
    *remove({ callback, payload }, { call, put }) {
      const response = yield call(deleteUser, payload);
      if (response.msgCode === 0) {
        if (callback) {
          callback();
        }
      } else {
        notification.error({
          message: '删除水价失败',
          description: response.msg,
        });
      }
    },
    *getSubordinateList({ callback, payload }, { call, put }) {
      const response = yield call(getSubordinateList, payload);
      if (response.msgCode === 0) {
        yield put({
          type: 'saveSubordinate',
          payload: response.data,
        });
        if (callback) callback(response.data);
      } else {
        notification.error({
          message: '获取员工列表失败',
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
    // 改变modal框状态
    setModalVisible(state, action) {
      return {
        ...state,
        modalVisible: action.payload,
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
    // 保存搜索框内容
    setFormValues(state, action) {
      return {
        ...state,
        formValues: action.payload,
      };
    },
    // 保存信息到record中
    saveCustomer(state, action) {
      return {
        ...state,
        record: action.payload,
      };
    },
    // 设置搜索框是否展开
    setExpandSearchForm(state, action) {
      return {
        ...state,
        expandSearchForm: action.payload,
      };
    },
    saveCompanyList(state, action) {
      return {
        ...state,
        companyList: action.payload,
      };
    },
    saveRoleList(state, action) {
      return {
        ...state,
        roleList: action.payload,
      };
    },
    saveSubordinate(state, action) {
      return {
        ...state,
        subordinate: action.payload,
      };
    },
  },
};

export default Person;
