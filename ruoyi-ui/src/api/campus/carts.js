import request from '@/utils/request'

// 查询购物车列表
export function listCarts(query) {
  return request({
    url: '/campus/carts/list',
    method: 'get',
    params: query
  })
}

// 查询购物车详细
export function getCarts(id) {
  return request({
    url: '/campus/carts/' + id,
    method: 'get'
  })
}

// 新增购物车
export function addCarts(data) {
  return request({
    url: '/campus/carts',
    method: 'post',
    data: data
  })
}

// 修改购物车
export function updateCarts(data) {
  return request({
    url: '/campus/carts',
    method: 'put',
    data: data
  })
}

// 删除购物车
export function delCarts(id) {
  return request({
    url: '/campus/carts/' + id,
    method: 'delete'
  })
}
