import request from '@/utils/http'

export const fetchBooks = async (params) => {
  const res = await request({
    url: '/books',
    method: 'GET',
    params
  })
  return res
}

export const fetchBookById = async (id) => {
  const res = await request({
    url: `/books/${id}`,
    method: 'GET'
  })
  return res
}

export const createBook = async (book) => {
  return request({
    url: '/books',
    method: 'POST',
    data: book
  })
}

export const updateBook = async (id, book) => {
  return request({
    url: `/books/${id}`,
    method: 'PUT',
    data: book
  })
}

export const deleteBook = async (id) => {
  return request({
    url: `/books/${id}`,
    method: 'DELETE'
  })
}