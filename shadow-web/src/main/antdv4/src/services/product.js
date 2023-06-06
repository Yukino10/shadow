import requestTest from '../utils/requestTest';

// 获取产品卡片列表
export async function queryProductList() {
  return requestTest('/admin/product/tableList', {
    method: 'POST',
  });
}
// 获取产品表格列表
export async function queryProductTableList() {
  return requestTest('/admin/product/tableList', {
    method: 'POST',
  });
}
// 新增产品信息
export async function create(params) {
  return requestTest('/admin/product/create', {
    method: 'POST',
    body: params,
  });
}
// 删除产品信息
export async function deleteProduct(params) {
  return requestTest('/admin/product/delete', {
    method: 'POST',
    body: params,
  });
}
// 删除产品信息
export async function deleteProductList(params) {
  return requestTest('/admin/product/deleteProduct', {
    method: 'POST',
    body: params,
  });
}
// 更新产品信息
export async function updateProduct(params) {
  return requestTest('/admin/product/update', {
    method: 'POST',
    body: params,
  });
}
// 打包
export async function packUp(params) {
  return requestTest('/admin/product/packUp', {
    method: 'POST',
    body: params,
  });
}

export async function getProductVersion(params) {
  return requestTest('/admin/product/version', {
    method: 'POST',
    body: params,
  });
}

export async function deleteProductVersion(params) {
  return requestTest('/admin/product/version/delete', {
    method: 'POST',
    body: params,
  });
}

export async function isUpdated(params) {
  return requestTest('/admin/product/isUpdated', {
    method: 'POST',
    body: params,
  });
}

export async function packageValidate(params) {
  return requestTest('/admin/product/package/validate', {
    method: 'POST',
    body: params,
  });
}
