import request from '@/utils/request'

// 查询我的订单列表
export function listOrders(query) {
  return request({
    url: '/campus/orders/list',
    method: 'get',
    params: query
  })
}

// 查询我的订单详细
export function getOrders(id) {
  return request({
    url: '/campus/orders/' + id,
    method: 'get'
  })
}

// 新增我的订单
export function addOrders(data) {
  return request({
    url: '/campus/orders',
    method: 'post',
    data: data
  })
}

// 修改我的订单
export function updateOrders(data) {
  return request({
    url: '/campus/orders',
    method: 'put',
    data: data
  })
}

// 删除我的订单
export function delOrders(id) {
  return request({
    url: '/campus/orders/' + id,
    method: 'delete'
  })
}
