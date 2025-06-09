import request from '@/utils/http'

export const fetchClassics = async (bookId, params) => {
  const res = await request({
    url: `/books/${bookId}/classics`,
    method: 'GET',
    params
  })
  return res
}

export const saveClassic = async (bookId, classic) => {
  try {
    return classic.id 
      ? await request({
          url: `/books/${bookId}/classics/${classic.id}`,
          method: 'PUT',
          data: classic 
        })
      : await request({
          url: `/books/${bookId}/classics`,
          method: 'POST',
          data:  classic 
        })
  } catch (error) {
    console.error("保存典籍失败:", error)
    throw error
  }
}

export const deleteClassic = async (bookId, classicId) => {
  try {
    return await request({
      url: `/books/${bookId}/classics/${classicId}`,
      method: 'DELETE'
    })
  } catch (error) {
    console.error("删除典籍失败:", error)
    throw error
  }
}