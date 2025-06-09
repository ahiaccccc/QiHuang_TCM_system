import axios from 'axios'

export function getCollectedStatus(userId, classicId) {
  return axios.get('/api/collected/status', {
    params: { userId, classicId }
  })
}

export function toggleCollected(userId, classicId, title) {
  return axios.post('/api/collected/toggle', null, {
    params: { userId, classicId, title }
  })
}
export function getUserCollected(userId) {
  return axios.get('/api/collected/list', {
    params: { userId }
  })
}
