import request from '@/utils/request'

// 查询浏览商品列表
export function listProducts(query) {
  return request({
    url: '/campus/products/list',
    method: 'get',
    params: query
  })
}

// 查询浏览商品详细
export function getProducts(id) {
  return request({
    url: '/campus/products/' + id,
    method: 'get'
  })
}

// 新增浏览商品
export function addProducts(data) {
  return request({
    url: '/campus/products',
    method: 'post',
    data: data
  })
}

// 修改浏览商品
export function updateProducts(data) {
  return request({
    url: '/campus/products',
    method: 'put',
    data: data
  })
}

// 删除浏览商品
export function delProducts(id) {
  return request({
    url: '/campus/products/' + id,
    method: 'delete'
  })
}
