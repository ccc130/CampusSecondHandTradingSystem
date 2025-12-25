import request from '@/utils/request'

// 查询商品类别列表
export function listCategories(query) {
  return request({
    url: '/campus/categories/list',
    method: 'get',
    params: query
  })
}

// 查询商品类别详细
export function getCategories(id) {
  return request({
    url: '/campus/categories/' + id,
    method: 'get'
  })
}

// 新增商品类别
export function addCategories(data) {
  return request({
    url: '/campus/categories',
    method: 'post',
    data: data
  })
}

// 修改商品类别
export function updateCategories(data) {
  return request({
    url: '/campus/categories',
    method: 'put',
    data: data
  })
}

// 删除商品类别
export function delCategories(id) {
  return request({
    url: '/campus/categories/' + id,
    method: 'delete'
  })
}
