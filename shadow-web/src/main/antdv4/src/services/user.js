import request from '@/utils/request';
import requestTest from '@/utils/requestTest';

export async function query() {
  return request('/api/users');
}

export async function queryNotices() {
  return request('/api/notices');
}

// 获取当前用户信息
export async function queryCurrent() {
  return requestTest('/admin/user/info');
}
/* export async function queryCurrent() {
  return request('/api/currentUser');
} */
export async function queryUser() {
  return requestTest('/admin/user/transfer');
}

// 获取当前用户权限列表
export async function findAuthorities(params) {
  return requestTest('/admin/authorities', {
    method: 'POST',
    body: params,
  });
}
