import axios from 'axios'

export const fetchBooks = async (params) => {
  const res = await axios.get(`/api/books`, { params })
  return res.data
}

export const fetchBookById = async (id) => {
  const res = await axios.get(`/api/books/${id}`)
  return res.data
}

export const createBook = async (book) => {
  return axios.post('/api/books', book)
}

export const updateBook = async (id, book) => {
  return axios.put(`/api/books/${id}`, book)
}

export const deleteBook = async (id) => {
  return axios.delete(`/api/books/${id}`)
}