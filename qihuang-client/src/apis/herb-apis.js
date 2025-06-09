import request from '@/utils/http'

export function getHerbCard(id) {
  return request({
    url: `/card/get`,
    method: 'GET'
  })
}
