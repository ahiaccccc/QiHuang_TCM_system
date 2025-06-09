import axios from 'axios'

export const fetchClassics = async (bookId, params) => {
  const res = await axios.get(`/api/books/${bookId}/classics`, { params })
  return res.data
}

// export const saveClassic = async (bookId, classic) => {
//   return classic.id 
//     ? axios.put(`/api/books/${bookId}/classics/${classic.id}`, classic)
//     : axios.post(`/api/books/${bookId}/classics`, classic)
// }

// export const deleteClassic = async (bookId, classicId) => {
//   return axios.delete(`/api/books/${bookId}/classics/${classicId}`)
// }

export const saveClassic = async (bookId, classic) => {
  try {
    return classic.id 
      ? await axios.put(`/api/books/${bookId}/classics/${classic.id}`, classic)
      : await axios.post(`/api/books/${bookId}/classics`, classic)
  } catch (error) {
    console.error("保存典籍失败:", error)
    throw error
  }
}

export const deleteClassic = async (bookId, classicId) => {
  try {
    return await axios.delete(`/api/books/${bookId}/classics/${classicId}`)
  } catch (error) {
    console.error("删除典籍失败:", error)
    throw error
  }
}