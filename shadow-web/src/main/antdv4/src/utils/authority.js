// use localStorage to store the authority info, which might be sent from server in actual project.
import { reloadAuthorized } from './Authorized';

export function getAuthority(str) {
  // return localStorage.getItem('antd-pro-authority') || ['admin', 'user'];
  const authorityString =
    typeof str === 'undefined' ? localStorage.getItem('antd-pro-authority') : str;
  // authorityString could be admin, "admin", ["admin"]
  let authority;
  try {
    authority = JSON.parse(authorityString);
  } catch (e) {
    authority = authorityString;
  }
  if (typeof authority === 'string') {
    return [authority];
  }
  return authority || ['admin'];
}

export function setAuthority(authority) {
  const proAuthority = typeof authority === 'string' ? [authority] : authority;
  localStorage.setItem('antd-pro-authority', JSON.stringify(proAuthority));
  return reloadAuthorized();
}

export function getToken() {
  return sessionStorage.getItem('vendplat30-token');
}

export function setToken(token) {
  return sessionStorage.setItem('vendplat30-token', token);
}

export function setRefreshAfterLogin(flag) {
  return sessionStorage.setItem('vendplat30-refreshAfterLogin', flag);
}

export function getRefreshAfterLogin() {
  return sessionStorage.getItem('vendplat30-refreshAfterLogin');
}
