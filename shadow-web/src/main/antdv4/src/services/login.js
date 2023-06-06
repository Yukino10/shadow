// import request from '@/utils/request';
import requestTest from '../utils/requestTest';
// 登陆前检验权限
export async function preLogin() {
   return requestTest('/auth/pre', { method: 'POST' });
}
// 登陆
export async function login(params) {
  return requestTest('/auth/login', {
    method: 'POST',
    body: params,
  });
}
// 登出
export async function logout() {
  return requestTest('/auth/logout', {
    method: 'POST',
  });
}
// 注册
export async function fakeRegister(params) {
  return requestTest('/auth/register', {
    method: 'POST',
    body: params,
  });
}

export async function changePwd(params) {
  return requestTest('/auth/changePwd', {
    method: 'POST',
    body: params,
  });
}

export async function checkoutPhoneIsEmpty(params) {
  return requestTest('/admin/user/phoneLogin/checkoutPhoneIsEmpty', {
    method: 'POST',
    body: params,
  });
}

export async function sendAuthCode(params) {
  return requestTest('/admin/user/phoneLogin/sendAuthCode', {
    method: 'POST',
    body: params,
  });
}

export async function phoneLogin(params) {
  return requestTest('/admin/user/phoneLogin', {
    method: 'POST',
    body: params,
  });
}

export async function sendAuthCodeForgotPassword(params) {
  return requestTest('/admin/user/forgotPassword/sendAuthCodeForgotPassword', {
    method: 'POST',
    body: params,
  });
}

export async function checkoutPhoneNext(params) {
  return requestTest('/admin/user/forgotPassword/checkoutPhoneNext', {
    method: 'POST',
    body: params,
  });
}

export async function forgotPasswordByPhone(params) {
  return requestTest('/admin/user/forgotPassword/forgotPasswordByPhone', {
    method: 'POST',
    body: params,
  });
}

export async function forgotPasswordByMail(params) {
  return requestTest('/admin/user/forgotPassword/forgotPasswordByMail', {
    method: 'POST',
    body: params,
  });
}
