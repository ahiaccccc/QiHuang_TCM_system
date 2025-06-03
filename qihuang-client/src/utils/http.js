import axios from 'axios'
import { getToken } from '@/utils/auth'

const httpInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})

httpInstance.interceptors.request.use(
  (config) => {
    // 从本地存储获取token
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}` // Bearer方案
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

httpInstance.interceptors.response.use(
  (res) => res.data,
  (e) => {
    if (e.response?.status === 401) {
      // 处理 401 错误
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(e)
  },
)

// 导出 httpInstance
export default httpInstance
