<template>
  <div class="admin-container">
    <!-- 书籍管理模块 -->
    <div class="admin-header">
      <h2>{{ selectedBook ? `${selectedBook.name} 典籍管理` : '书籍管理' }}</h2>
      <div>
        <button v-if="selectedBook" @click="selectedBook = null">返回书籍列表</button>
        <button @click="$router.push('/')">返回前台</button>
      </div>
    </div>

    <!-- 书籍列表 -->
    <div v-if="!selectedBook" class="book-management">
      <form @submit.prevent="handleBookSubmit" class="admin-form">
        <div class="form-row">
          <input v-model="bookForm.name" placeholder="书名" required />
          <input v-model="bookForm.author" placeholder="作者" />
        </div>
        <div class="form-row">
          <input v-model="bookForm.dynasty" placeholder="朝代" />
          <input v-model="bookForm.year" placeholder="年份" />
        </div>
        <div class="form-actions">
          <button type="submit">{{ editingBook ? '更新' : '创建' }}书籍</button>
          <button type="button" @click="resetBookForm">重置</button>
        </div>
      </form>

      <div class="book-admin-list">
        <div v-for="book in books" :key="book.id" class="admin-book-card">
          <div class="book-info">
            <h4>{{ book.name }}</h4>
            <p class="meta-info">{{ book.author }} · {{ book.dynasty }}</p>
          </div>
          <div class="book-actions">
            <button @click="selectBook(book)">管理典籍</button>
            <button @click="editBook(book)">编辑</button>
            <button @click="deleteBook(book.id)" class="delete-btn">删除</button>
          </div>
        </div>
      </div>

      <!-- 书籍分页 -->
      <div class="pagination-list">
        <div class="pagination-page">
          <button @click="prevBookPage" :disabled="bookPage === 1">‹</button>
        </div>
        <div class="element-wrapper">
          <div class="element">{{ bookPage }}</div>
        </div>
        <div class="text-wrapper-9">/</div>
        <div class="element-wrapper">
          <div class="element">{{ bookStore.totalPages }}</div>
        </div>
        <div class="pagination-page">
          <button @click="nextBookPage" :disabled="bookPage === bookStore.totalPages">›</button>
        </div>
        <div class="pagination-input">
          <input 
            type="number" 
            v-model.number="bookInputPage" 
            min="1" 
            :max="bookStore.totalPages" 
            @keyup.enter="goToBookPage"
            placeholder="页码"
          />
          <button @click="goToBookPage">跳转</button>
        </div>
      </div>
    </div>

    <!-- 典籍管理模块 -->
    <div v-else class="classic-management">
      <form @submit.prevent="handleClassicSubmit" class="admin-form">
        <div class="form-row">
          <input v-model="classicForm.title" placeholder="章节标题" required />
        </div>
        
        <div class="form-column">
          <textarea v-model="classicForm.originalText" placeholder="原文" rows="4"></textarea>
          <textarea v-model="classicForm.vernacularText" placeholder="白话文" rows="4"></textarea>
          <textarea v-model="classicForm.translateText" placeholder="翻译" rows="4"></textarea>
        </div>

        <div class="form-actions">
          <button type="submit">{{ editingClassic ? '更新' : '创建' }}典籍</button>
          <button type="button" @click="resetClassicForm">重置</button>
        </div>
      </form>

      <div class="classic-admin-list">
        <div v-for="classic in classics" :key="classic.id" class="admin-classic-card">
          <div class="classic-info">
            <h4>{{ classic.title }}</h4>
          </div>
          <div class="classic-actions">
            <button @click="editClassic(classic)">编辑</button>
            <button @click="deleteClassic(classic.id)" class="delete-btn">删除</button>
          </div>
        </div>
      </div>

      <!-- 典籍分页 -->
      <div class="pagination-list">
        <div class="pagination-page">
          <button @click="prevClassicPage" :disabled="classicPage === 1">‹</button>
        </div>
        <div class="element-wrapper">
          <div class="element">{{ classicPage }}</div>
        </div>
        <div class="text-wrapper-9">/</div>
        <div class="element-wrapper">
          <div class="element">{{ classicStore.totalPages }}</div>
        </div>
        <div class="pagination-page">
          <button @click="nextClassicPage" :disabled="classicPage === classicStore.totalPages">›</button>
        </div>
        <div class="pagination-input">
          <input 
            type="number" 
            v-model.number="classicInputPage" 
            min="1" 
            :max="classicStore.totalPages" 
            @keyup.enter="goToClassicPage"
            placeholder="页码"
          />
          <button @click="goToClassicPage">跳转</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBookStore } from '@/stores/book'
import { useClassicStore } from '@/stores/classic'
import { storeToRefs } from 'pinia'

// 书籍管理逻辑
const bookStore = useBookStore()
const { totalPages: bookTotalPages } = storeToRefs(bookStore)
const books = ref([])
const selectedBook = ref(null)
const bookForm = ref({})
const editingBook = ref(null)

// 书籍分页相关
const bookPage = ref(1)
const bookPageSize = ref(20)
const bookInputPage = ref(1)

// 典籍管理逻辑
const classicStore = useClassicStore()
const { totalPages: classicTotalPages } = storeToRefs(classicStore)
const classics = ref([]) 
const classicForm = ref({})
const editingClassic = ref(null)

// 典籍分页相关
const classicPage = ref(1)
const classicPageSize = ref(20)
const classicInputPage = ref(1)

onMounted(async () => {
  await loadBooks()
})

// 书籍操作
const loadBooks = async () => {
  await bookStore.loadBooks({ 
    page: bookPage.value - 1, 
    size: bookPageSize.value 
  })
  books.value = bookStore.books
  bookInputPage.value = bookPage.value // 同步输入框
}

// 书籍分页方法
const nextBookPage = () => {
  if (bookPage.value < bookTotalPages.value) {
    bookPage.value++
    loadBooks()
  }
}

const prevBookPage = () => {
  if (bookPage.value > 1) {
    bookPage.value--
    loadBooks()
  }
}

const goToBookPage = () => {
  const targetPage = Math.max(1, Math.min(bookInputPage.value, bookTotalPages.value))
  bookPage.value = targetPage
  loadBooks()
}

const handleBookSubmit = async () => {
  if (editingBook.value) {
    await bookStore.updateBook(editingBook.value.id, bookForm.value)
  } else {
    await bookStore.createBook(bookForm.value)
  }
  await loadBooks()
  resetBookForm()
}

const editBook = (book) => {
  editingBook.value = book.id
  bookForm.value = { ...book }
}

const deleteBook = async (id) => {
  if (!confirm('确认删除该书籍及其所有典籍？')) return
  await bookStore.deleteBook(id)
  await loadBooks()
}

const resetBookForm = () => {
  bookForm.value = {}
  editingBook.value = null
}

// 典籍操作
const selectBook = async (book) => {
  selectedBook.value = book
  classicPage.value = 1 // 重置典籍页码
  await loadClassics()
}

const loadClassics = async () => {
  await classicStore.loadClassics(selectedBook.value.id, { 
    page: classicPage.value - 1, 
    size: classicPageSize.value 
  })
  classics.value = classicStore.classics
  classicInputPage.value = classicPage.value // 同步输入框
}

// 典籍分页方法
const nextClassicPage = () => {
  if (classicPage.value < classicTotalPages.value) {
    classicPage.value++
    loadClassics()
  }
}

const prevClassicPage = () => {
  if (classicPage.value > 1) {
    classicPage.value--
    loadClassics()
  }
}

const goToClassicPage = () => {
  const targetPage = Math.max(1, Math.min(classicInputPage.value, classicTotalPages.value))
  classicPage.value = targetPage
  loadClassics()
}

const handleClassicSubmit = async () => {
  if (editingClassic.value) {
    // 直接通过 store 实例调用方法
    await classicStore.updateClassic(
      selectedBook.value.id, 
      editingClassic.value, 
      classicForm.value
    )
  } else {
    // 直接通过 store 实例调用方法
    await classicStore.createClassic(
      selectedBook.value.id, 
      classicForm.value
    )
  }
  await loadClassics()
  resetClassicForm()
}

const editClassic = (classic) => {
  editingClassic.value = classic.id
  classicForm.value = { ...classic }
}

const deleteClassic = async (id) => {
  if (!confirm('确认删除该典籍？')) return
  await classicStore.deleteClassic(selectedBook.value.id, id)
  await loadClassics()
}

const resetClassicForm = () => {
  classicForm.value = {}
  editingClassic.value = null
}
</script>

<style scoped>
.book-management,
.classic-management {
  margin-top: 2rem;
}

.meta-info {
  color: #666;
  font-size: 0.9em;
}

.admin-classic-card {
  background: rgba(255, 255, 255, 0.9);
  padding: 1rem;
  margin: 0.5rem 0;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.classic-info h4 {
  margin: 0 0 0.5rem 0;
}
.admin-container {
  position: relative;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 150vh;
  z-index: 1;
}

.admin-container::before {
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("https://c.animaapp.com/m9pqi0c3GNaMeT/img/back.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.3; /* 调整透明度 */
  z-index: -1;
}
.admin-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.admin-form {
  background: rgba(248, 249, 250, 0.9); /* 增加透明度 */
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-column {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1rem;
}

input, textarea, select {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 0.5rem;
  font-size: 1rem;
}

textarea {
  resize: vertical;
  min-height: 100px;
}

select {
  background: white;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.book-admin-list {
  display: grid;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.admin-book-card {
  background: rgba(255, 255, 255, 0.9); /* 增加透明度 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.book-info h4 {
  margin: 0;
  color: #333;
}

.category-tag {
  display: inline-block;
  background: #e8f5e9;
  color: #2e7d32;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.8rem;
}

.book-actions {
  display: flex;
  gap: 0.5rem;
}

.delete-btn {
  background-color: #ff4444;
}

.admin-header,
.admin-form,
.book-admin-list {
  position: relative;
  z-index: 1;
}

/* 分页样式 */
.pagination-list {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 30px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
}

.pagination-page button {
  background: #4a6baf;
  color: white;
  border: none;
  border-radius: 4px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.pagination-page button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.element-wrapper {
  min-width: 24px;
  text-align: center;
}

.pagination-input {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.pagination-input input {
  width: 60px;
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
}

.pagination-input button {
  margin-left: 8px;
  padding: 6px 12px;
  background-color: #1da1a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.pagination-input button:hover {
  background-color: #169772;
}
</style>