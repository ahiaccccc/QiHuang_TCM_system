import request from '@/utils/http'

export const loginAPI = ({ username, password }) => {
  return request({
      url: '/login',
      method: 'POST',
      headers: {
        username,
        password,
          // "Access-Control-Allow-Origin": "*",
      },
      // data: {
      //     username,
      //     password
      // }
  })
}