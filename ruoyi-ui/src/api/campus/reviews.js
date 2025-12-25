import request from '@/utils/request'

// 查询商品评价列表
export function listReviews(query) {
  return request({
    url: '/campus/reviews/list',
    method: 'get',
    params: query
  })
}

// 查询商品评价详细
export function getReviews(id) {
  return request({
    url: '/campus/reviews/' + id,
    method: 'get'
  })
}

// 新增商品评价
export function addReviews(data) {
  return request({
    url: '/campus/reviews',
    method: 'post',
    data: data
  })
}

// 修改商品评价
export function updateReviews(data) {
  return request({
    url: '/campus/reviews',
    method: 'put',
    data: data
  })
}

// 删除商品评价
export function delReviews(id) {
  return request({
    url: '/campus/reviews/' + id,
    method: 'delete'
  })
}
