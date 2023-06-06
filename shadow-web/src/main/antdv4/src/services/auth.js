import requestTest from '../utils/requestTest';

export async function queryTreeNode() {
  return requestTest('/admin/auth/tree', {
    method: 'GET',
  });
}

export async function queryAuth(params) {
  return requestTest('/admin/adminmgt/permission', {
    method: 'POST',
    body: params,
  });
}
export async function addAuth(params) {
  return requestTest('/admin/adminmgt/permission', {
    method: 'PUT',
    body: params,
  });
}
export async function patchAuth(params) {
  return requestTest('/admin/adminmgt/permission', {
    method: 'PATCH',
    body: params,
  });
}
export async function removeAuth(params) {
  return requestTest(`/admin/adminmgt/permission/${params}`, {
    method: 'DELETE',
  });
}
export async function checkAuthName(params) {
  return requestTest(`/admin/adminmgt/permission/check_name/${params}`, {
    method: 'POST',
  });
}
