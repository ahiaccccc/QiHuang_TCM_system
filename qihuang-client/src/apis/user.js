import request from '@/utils/http'

export const registerAPI = (registerRequest) => {
  return request({
    url: '/user/register',  // 与后端接口保持一致
    method: 'POST',
    data: registerRequest
  })
}

export const loginAPI = (loginRequest) => {
  return request({
    url: '/user/login',
    method: 'POST',
    data: loginRequest
  })
}

export const emailResetPasswordAPI = (resetPasswordRequest) => {
  return request({
    url: '/user/email-reset-password',
    method: 'POST',
    data: resetPasswordRequest
  })
}

export const getProfileAPI = () => {
  return request({
    url: '/user/profile',
    method: 'GET'
  })
}

export const updateProfileAPI = (data) => {
  return request({
    url: '/user/update-profile',
    method: 'POST',
    data
  })
}

export const uploadAvatarAPI = (formData) => {
  return request({
    url: '/user/upload-avatar',
    method: 'POST',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: formData
  })
}
