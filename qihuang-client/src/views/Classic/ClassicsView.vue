<template>
  <div id="app" class="classics">
    <div class="header">
      <h2>中医典籍</h2>
      <div class="tabs">
        <button v-for="tab in tabs" :key="tab" @click="selectedTab = tab" :class="{ active: selectedTab === tab }">
          {{ tab }}
        </button>
        <input v-model="searchQuery" placeholder="搜索标题或作者" class="search-input" />
        <button class="admin-btn" @click="showAdmin = !showAdmin">后台管理</button>
      </div>
    </div>

    <div v-if="showAdmin" class="admin-panel">
      <h3>后台管理</h3>
      <form @submit.prevent="saveBook">
        <input v-model="editBook.title" placeholder="标题" required />
        <input v-model="editBook.author" placeholder="作者" />
        <input v-model="editBook.dynasty" placeholder="朝代" />
        <input v-model="editBook.coverUrl" placeholder="封面URL" />
        <textarea v-model="editBook.originalText" placeholder="原文"></textarea>
        <textarea v-model="editBook.vernacularText" placeholder="白话文"></textarea>
        <textarea v-model="editBook.translateText" placeholder="翻译"></textarea>
        <select v-model="editBook.category">
          <option disabled value="">请选择分类</option>
            <option v-for="tab in tabs.slice(1)" :key="tab" :value="tab">
              {{ tab }}
            </option>
        </select>
        <textarea v-model="editBook.summary" placeholder="简介"></textarea>
        <button type="submit">保存</button>
      </form>
      <div class="book-admin-list">
        <div class="book-card" v-for="book in books" :key="book.id">
          <h4>{{ book.title }}</h4>
          <button @click="edit(book)">编辑</button>
          <button @click="del(book.id)">删除</button>
        </div>
      </div>
    </div>

    <div class="book-list" v-else>
      <div class="book-card" v-for="book in filteredBooks" :key="book.id">
        <img :src="book.coverUrl" alt="cover" class="cover" />
        <div class="info">
          <h3>{{ book.title }}</h3>
          <p>{{ book.author }}</p>
          <p class="summary">{{ book.summary }}</p>
          <button @click="goToDetail(book.id)">学习</button>
        </div>
      </div>
    </div>

    <div class="pagination">
      <button @click="prevPage" :disabled="page === 1">&lt;</button>
      <span>{{ page }}</span>
      <button @click="nextPage">&gt;</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const tabs = ['所有类型', '基础理论', '临床各科', '中药方剂', '针灸推拿']
const selectedTab = ref('所有类型')
const books = ref([])
const page = ref(1)
const pageSize = 12
const showAdmin = ref(false)
const editBook = ref({})
const searchQuery = ref('')
const totalPages = ref(1)

const fetchBooks = async () => {
  const res = await axios.get(`/api/classics`, {
    params: {
      page: page.value - 1,
      size: pageSize,
      category: selectedTab.value === '所有类型' ? null : selectedTab.value,
      keyword: searchQuery.value || null
    }
  })
  books.value = res.data.content
  totalPages.value = res.data.totalPages
}

const goToDetail = (bookId) => {
  router.push({ path: '/classic-detail', query: { book_id: bookId } })
}

const edit = (book) => {
  editBook.value = { ...book }
}

const saveBook = async () => {
  if (editBook.value.id) {
    await axios.put(`/api/classics/${editBook.value.id}`, editBook.value)
  } else {
    await axios.post(`/api/classics`, editBook.value)
  }
  fetchBooks()
  editBook.value = {}
}

const del = async (id) => {
  if (confirm('确认删除？')) {
    await axios.delete(`/api/classics/${id}`)
    fetchBooks()
  }
}

onMounted(fetchBooks)
watch([selectedTab, searchQuery, page], fetchBooks)

const filteredBooks = computed(() => books.value)

const nextPage = () => {
  page.value++
}
const prevPage = () => {
  if (page.value > 1) page.value--
}
</script>

<style scoped>
.books-page { padding: 2rem; font-family: sans-serif; }
.header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem; }
.tabs { display: flex; flex-wrap: wrap; gap: 0.5rem; align-items: center; }
.tabs button { padding: 0.4rem 0.8rem; border: none; background: #eee; border-radius: 0.5rem; }
.tabs .active { background: #42b983; color: white; }
.search-input { padding: 0.4rem 0.8rem; border-radius: 0.5rem; border: 1px solid #ccc; }
.book-list, .book-admin-list { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-top: 1rem; }
.book-card { background: white; padding: 1rem; border-radius: 0.75rem; box-shadow: 0 2px 8px rgba(0,0,0,0.1); text-align: center; }
.cover { width: 100%; height: 180px; object-fit: cover; border-radius: 0.5rem; }
.summary { font-size: 0.85rem; color: #666; margin: 0.5rem 0; }
.learn-btn, .admin-btn, form button { background-color: #42b983; color: white; padding: 0.4rem 0.8rem; border: none; border-radius: 0.5rem; cursor: pointer; }
.pagination { margin-top: 1rem; display: flex; justify-content: center; align-items: center; gap: 1rem; }
.admin-panel form { display: flex; flex-direction: column; gap: 0.5rem; max-width: 600px; margin-top: 1rem; }
.admin-panel textarea, .admin-panel input, .admin-panel select { padding: 0.4rem; border: 1px solid #ccc; border-radius: 0.5rem; }
</style>