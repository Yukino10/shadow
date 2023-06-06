import { routerRedux } from 'dva/router';
import { stringify } from 'querystring';
import { notification } from 'antd';
import router from 'umi/router';
import { preLogin, login, logout } from '../services/login';
import {
  setAuthority,
  setToken,
  setRefreshAfterLogin,
  getRefreshAfterLogin,
} from '../utils/authority';
import { getPageQuery } from '@/utils/utils';
import { reloadAuthorized } from '../utils/Authorized';

const Model = {
  namespace: 'login',
  state: {
    status: undefined,
    type: null,
    phone: null,
  },
  effects: {
    // 登录前向服务器申请公钥
    *pre({ callback }, { call, put }) {
      const preResp = yield call(preLogin);
      if (preResp.msgCode !== 0) {
        notification.error({
          message: '获取验证信息失败',
          description: preResp,
        });
        return;
      }
      if (callback) {
        callback(preResp.data.encoded);
      }
    },
    // 登陆
    *login({ payload }, { call, put }) {
      const response = yield call(login, payload);
      const info = {};
      info.status = response.msgCode;
      info.type = payload.type;
      info.currentAuthority = response.data.currentAuthority;
      info.token = response.data.token;
      yield put({
        type: 'changeLoginStatus',
        payload: info,
      });
      // Login successfully
      if (response.msgCode === 0) {
        const urlParams = new URL(window.location.href);
        const params = getPageQuery();
        let { redirect } = params;
        if (redirect) {
          const redirectUrlParams = new URL(redirect);
          if (redirectUrlParams.origin === urlParams.origin) {
            redirect = redirect.substr(urlParams.origin.length);
            if (redirect.match(/^\/.*#/)) {
              redirect = redirect.substr(redirect.indexOf('#') + 1);
            }
          } else {
            window.location.href = '/admin';
            return;
          }
        }
        yield put(routerRedux.replace(redirect || '/admin'));
        // yield put(routerRedux.push('/'));
        // token超时或者被其他人登录导致token失效之后，重新登录强制刷新页面
        if (getRefreshAfterLogin()) {
          setRefreshAfterLogin(false);
          window.location.reload(true);
        }
      } else {
        notification.error({
          message: '登录失败',
          description: response.msg,
        });
      }
    },

    /*   *getCaptcha({ payload }, { call }) {
      yield call(getFakeCaptcha, payload);
    }, */

    *logout({ payload }, { call, put, select }) {
      const { redirect } = getPageQuery();
      let needLogout = false;
      if (payload && payload.logoutfailed) {
        needLogout = true;
      } else {
        const response = yield call(logout);
        if (response.msgCode == 0) {
          needLogout = true;
        } else {
          notification.error({
            message: '登出失败',
            description: response.msg,
          });
        }
      }
      const status = yield select(state => state.status);
      console.log('status-->', status);
      if (needLogout && status != false) {
        if (payload && payload.Unauthorized) {
          notification.error({
            message: '登出系统',
            description: '用户登录超时或者有其他人登录了该用户',
          });
        }
        try {
          // get location pathname
          // const urlParams = new URL(window.location.href);
          // const pathname = yield select(state => state.routing.location.pathname);
          // add the parameters in the url
          // urlParams.searchParams.set('redirect', pathname);
          window.history.replaceState(null, '/usr/login');
        } finally {
          yield put({
            type: 'changeLoginStatus',
            payload: {
              status: false,
              currentAuthority: 'guest',
            },
          });
          setRefreshAfterLogin(true);
          reloadAuthorized();
          // yield put(routerRedux.push('/user/login'));
          if (window.location.pathname !== '/user/login' && !redirect) {
            router.replace({
              pathname: '/user/login',
              search: stringify({
                redirect: window.location.href,
              }),
            });
          }
        }
      }
    },
  },
  reducers: {
    changeLoginStatus(state, { payload }) {
      setAuthority(payload.currentAuthority);
      setToken(payload.token);
      return {
        ...state,
        status: payload.status,
        type: payload.type,
      };
    },
  },
};
export default Model;
