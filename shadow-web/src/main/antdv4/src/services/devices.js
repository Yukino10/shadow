import requestTest from '../utils/requestTest';

// 获取物模型列表
export async function queryDevicesList() {
  return requestTest('/admin/device/list', {
    method: 'POST',
  });
}
// 删除物模型信息
export async function deleteDevice(params) {
  return requestTest('/admin/device/delete', {
    method: 'POST',
    body: params,
  });
}
// 新增物模型
export async function createDevice(params) {
  return requestTest('/admin/device/create', {
    method: 'POST',
    body: params,
  });
}
// 获取物模型属性列表
export async function queryDevicesPropList(params) {
  return requestTest('/admin/device/prop/list', {
    method: 'POST',
    body: params,
  });
}
// 删除物模型属性
export async function deleteDeviceProp(params) {
  return requestTest('/admin/device/prop/delete', {
    method: 'POST',
    body: params,
  });
}
// 新增物模型属性
export async function createDeviceProp(params) {
  return requestTest('/admin/device/prop/create', {
    method: 'POST',
    body: params,
  });
}
/* // 新增产品信息


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
} */

// 获取基础服务列表
export async function queryBaseServiceList() {
  return requestTest('/admin/base/list', {
    method: 'POST',
  });
}
