import { notification } from 'antd';
import { fakeRegister } from '@/services/login';
import { setAuthority } from '@/utils/authority';
import { reloadAuthorized } from '@/utils/Authorized';

const Register = {
  namespace: 'register',

  state: {
    status: undefined,
  },

  effects: {
    *submit({ payload }, { call, put }) {
      const response = yield call(fakeRegister, payload);
      console.log('payload', response);
      // register result
      if (response.msgCode === 0) {
        yield put({
          type: 'registerHandle',
          payload: {
            status: 'ok',
          },
        });
      } else {
        notification.error({
          message: '登录失败',
          description: response.msg,
        });
      }
    },
  },

  reducers: {
    registerHandle(state, { payload }) {
      // setAuthority('user');
      // reloadAuthorized();
      return {
        ...state,
        status: payload.status,
      };
    },
  },
};

export default Register;
