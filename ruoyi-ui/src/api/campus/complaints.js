import request from '@/utils/request'

// 查询投诉商品列表
export function listComplaints(query) {
  return request({
    url: '/campus/complaints/list',
    method: 'get',
    params: query
  })
}

// 查询投诉商品详细
export function getComplaints(id) {
  return request({
    url: '/campus/complaints/' + id,
    method: 'get'
  })
}

// 新增投诉商品
export function addComplaints(data) {
  return request({
    url: '/campus/complaints',
    method: 'post',
    data: data
  })
}

// 修改投诉商品
export function updateComplaints(data) {
  return request({
    url: '/campus/complaints',
    method: 'put',
    data: data
  })
}

// 删除投诉商品
export function delComplaints(id) {
  return request({
    url: '/campus/complaints/' + id,
    method: 'delete'
  })
}
