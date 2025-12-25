import request from '@/utils/request'

// 查询我的收藏列表
export function listFavorites(query) {
  return request({
    url: '/campus/favorites/list',
    method: 'get',
    params: query
  })
}

// 查询我的收藏详细
export function getFavorites(id) {
  return request({
    url: '/campus/favorites/' + id,
    method: 'get'
  })
}

// 新增我的收藏
export function addFavorites(data) {
  return request({
    url: '/campus/favorites',
    method: 'post',
    data: data
  })
}

// 修改我的收藏
export function updateFavorites(data) {
  return request({
    url: '/campus/favorites',
    method: 'put',
    data: data
  })
}

// 删除我的收藏
export function delFavorites(id) {
  return request({
    url: '/campus/favorites/' + id,
    method: 'delete'
  })
}
