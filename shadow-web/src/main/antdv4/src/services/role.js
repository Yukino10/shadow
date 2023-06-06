import requestTest from '@/utils/requestTest';

export async function queryRole(params) {
  return requestTest('/admin/role/list', {
    method: 'POST',
    body: params,
  });
}

export async function validRoleName(params) {
  return requestTest(`/admin/role/name/${params}`, {
    method: 'GET',
  });
}

export async function addRole(params) {
  return requestTest('/admin/role', {
    method: 'PUT',
    body: params,
  });
}

export async function removeRole(params) {
  return requestTest(`/admin/role/${params}`, {
    method: 'DELETE',
  });
}

export async function updateRole(params) {
  return requestTest('/admin/role', {
    method: 'PATCH',
    body: params,
  });
}

export async function queryUserRoles(params) {
  return requestTest(`/admin/role/userRole/${params}`, {
    method: 'POST',
  });
}

export async function changeUpdate(params) {
  return requestTest('/admin/role/changeMember', {
    method: 'PATCH',
    body: params,
  });
}

export async function queryPermissions() {
  return requestTest('/admin/transfer/rolePermission');
}

export async function queryRoleFunctions(params) {
  return requestTest(`/admin/rolePermission/function/${params}`, {
    method: 'POST',
  });
}

export async function addRolePermission(params) {
  return requestTest('/admin/rolePermission', {
    method: 'PUT',
    body: params,
  });
}
