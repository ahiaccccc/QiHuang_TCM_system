import request from '@/utils/http'

export function getCollectedStatus(userId, classicId) {
  const response = request({
    url: '/collected/status',
    method: 'GET',
    params: { userId, classicId }
  })
  return response
}

export function toggleCollected(userId, classicId, title) {
  return request({
    url: '/collected/toggle',
    method: 'POST',
    params: { userId, classicId, title }
  })
}


export function getUserCollected(userId) {
  return request({
    url: '/collected/list',
    method: 'GET',
    params: { userId }
  })
}