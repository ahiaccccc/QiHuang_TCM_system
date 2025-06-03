// 存储Token
export const setToken = (token) => {

  console.log('Setting token:', token) // 打印token以便调试
  localStorage.setItem('token', token)
}

// 获取Token
export const getToken = () => {
  return localStorage.getItem('token')
}

// 移除Token
export const removeToken = () => {
  localStorage.removeItem('token')
}
