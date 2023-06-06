import requestTest from '../utils/requestTest';

export async function queryPerson(params) {
  return requestTest('/admin/user/searchUsers', {
    method: 'POST',
    body: params,
  });
}

export async function queryCompanyList() {
  return requestTest('/admin/company/getCoSimpleList', {
    method: 'GET',
  });
}

export async function queryRoleList() {
  return requestTest('/admin/role/getRoleSelectList', {
    method: 'GET',
  });
}

export async function validName(params) {
  return requestTest(`/admin/user/valid/${params}`, {
    method: 'GET',
  });
}

export async function resetPass(params) {
  return requestTest(`/admin/user/resetPwd/${params}`, {
    method: 'POST',
  });
}

export async function addPerson(params) {
  return requestTest('/admin/user', {
    method: 'POST',
    body: params,
  });
}

export async function deleteUser(params) {
  return requestTest('/admin/user/deleteUser', {
    method: 'POST',
    body: params,
  });
}

export async function getSubordinateList() {
  return requestTest('/admin/user/getSubordinate', {
    method: 'GET',
  });
}
