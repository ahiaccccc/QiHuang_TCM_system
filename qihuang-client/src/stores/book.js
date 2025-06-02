import { defineStore } from 'pinia'
import { ref } from 'vue'
import { 
  fetchBooks, 
  fetchBookById, 
  createBook as apiCreateBook,
  updateBook as apiUpdateBook,
  deleteBook as apiDeleteBook
} from '@/apis/book-apis'

export const useBookStore = defineStore('book', () => {
  const books = ref([])
  const totalPages = ref(1)
  const currentBook = ref(null)

  const loadBooks = async (params = { page: 1, size: 10 }) => {
    const data = await fetchBooks(params)
    books.value = data?.content || []
    totalPages.value = data?.totalPages || 1
  }

  const getBookById = async (id) => {
    const data = await fetchBookById(id)
    currentBook.value = data
    return data
  }

  const createBook = async (book) => {
    await apiCreateBook(book)
    await loadBooks()
  }

  const updateBook = async (id, book) => {
    await apiUpdateBook(id, book)
    await loadBooks()
  }

  const deleteBook = async (id) => {
    await apiDeleteBook(id)
    await loadBooks()
  }

  return { books, totalPages, currentBook, loadBooks, getBookById, createBook, updateBook, deleteBook }
})