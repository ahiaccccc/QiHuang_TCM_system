<template>
  <div class="classics">
    <Navi />
    <div class="hero-section">
      <div class="main-area">
        <div class="main-area-header">
          <div class="frame">
            <div class="text-wrapper">中医典籍</div>
          </div>

          <!-- <div class="tabs" style="position: absolute; top: 20px; right: 150px;"> -->
          <div class="tabs">
            <input v-model="searchQuery" placeholder="搜索书名、作者或朝代" class="search-input text-wrapper-14" style="
              margin-right: 20px;
              padding: 8px 16px;
              border: 2px solid var(--huilan);
              border-radius: 24px;
              background-color: var(--grayswhite);
              transition: all 0.3s ease;
              width: 270px;
            " />
          </div>

          <!-- <button class="button-blue text-wrapper-15" @click="$router.push('/admin-classics')">
            后台管理
          </button> -->
        </div>

        <div class="div-wrapper">
          <div class="frame-2">
            <div v-for="book in books" :key="book.id" class="book-item" @click="goToBook(book.id)">
              <div class="book-info">
                <div class="text-wrapper-6">{{ book.name }}</div>
                <div class="text-wrapper-7">{{ book.author }}</div>
                <div class="text-wrapper-8">{{ book.dynasty }} {{ book.year }}</div>
              </div>
            </div>
          </div>
          <div class="pagination-list">
            <div class="pagination-page">
              <div class="text-wrapper-9" @click="prevPage" :disabled="page === 1">‹</div>
            </div>
            <div class="element-wrapper">
              <div class="element">{{ page }}</div>
            </div>
            <div class="text-wrapper-9">/</div>
            <div class="element-wrapper">
              <div class="element">{{ store.totalPages }}</div>
            </div>
            <div class="pagination-page">
              <div class="text-wrapper-9" @click="nextPage" :disabled="page === store.totalPages">›</div>
            </div>
            <div class="pagination-input">
              <input type="number" v-model.number="inputPage" min="1" :max="store.totalPages" @keyup.enter="goToPage"
                placeholder="页码" />
              <button @click="goToPage">跳转</button>
            </div>
          </div>
        </div>

        <!-- 修改后的分页组件 -->

      </div>
    </div>
  </div>
</template>

<script setup>
import '@/assets/book.css'
import Navi from '../components/NaviHomeView.vue'

import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useBookStore } from '@/stores/book'

const router = useRouter()
const store = useBookStore()

const page = ref(1)
const pageSize = 12
const searchQuery = ref('')
const inputPage = ref(1) // 新增：用于输入跳转的页码

const books = computed(() => store.books)

const goToBook = (bookId) => {
  router.push({ path: `/books/${bookId}/classics` })
}

const nextPage = () => {
  if (page.value < store.totalPages) {
    page.value++
    inputPage.value = page.value // 同步输入框值
  }
}

const prevPage = () => {
  if (page.value > 1) {
    page.value--
    inputPage.value = page.value // 同步输入框值
  }
}

// 新增：跳转到指定页面
const goToPage = () => {
  let targetPage = Math.max(1, Math.min(inputPage.value, store.totalPages))
  page.value = targetPage
  inputPage.value = targetPage // 修正输入值
}

const fetchBooks = async () => {
  try {
    // store.setLoading(true);
    await store.loadBooks({
      page: page.value - 1,
      size: pageSize,
      name: searchQuery.value,
      author: searchQuery.value,
      dynasty: searchQuery.value
    });
    // console.log("书籍数据加载成功", store.books);
    
    // 添加数据验证
    if (!Array.isArray(store.books)) {
      console.error("API返回数据格式错误", store.books);
      store.books = []; // 防止渲染错误
    }
    
    inputPage.value = page.value;
  } catch (error) {
    console.error("加载书籍失败:", error);
  } finally {
    // store.setLoading(false);
  }
}

onMounted(fetchBooks)
watch([searchQuery, page], fetchBooks)
</script>

<style scoped>
.book-item {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  transition: all 0.4s cubic-bezier(0.75, 0.885, 0.32, 1.275);
  cursor: pointer;
  position: relative;
  height: 130px;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.book-item:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
}

.book-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 10px;
  background: linear-gradient(90deg, #1da1a6b6, #e2fac4, #169772, #a1c4fd);
}

.book-info {
  padding: 25px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.text-wrapper-6 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 12px;
  line-height: 1;
}

.text-wrapper-7 {
  font-size: 1.1rem;
  color: hsl(0, 2%, 19%);
  margin-bottom: 100px;
  padding-top: 10px;
  font-weight: 500;
}

.text-wrapper-8 {
  font-size: 1rem;
  color: #7f8c8d;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px dashed #eee;
}

.frame-2 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  /* 核心：每行三列 */
  gap: 30px;
  /* 元素间距 */
  margin-top: 20px;
  padding-right: 20px;
}

.pagination-list {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  /* margin-top: 30px; */
  margin:0 auto;
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