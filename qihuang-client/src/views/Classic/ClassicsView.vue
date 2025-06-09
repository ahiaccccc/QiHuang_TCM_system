<template>
  <div class="classics">
    <Navi />
    <div class="hero-section">
      <div class="main-area">
        <div class="main-area-header">
          <div class="frame">
            <div class="text-wrapper">{{ book.name }}</div>
          </div>

          <div class="frame-wrapper-right">
            <button
              @click="$router.push('/books')"
              class="text-wrapper-3"
            >返回</button>
          </div>
        </div>

        <div class="div-wrapper">
          <div class="frame-2">
            <div
              v-for="classic in classics"
              :key="classic.id"
              class="classic-item"
              @click="goToDetail(classic.id)"
            >
              <div class="classic-info">
                <div class="text-wrapper-6">{{ classic.title }}</div>
                <!-- <div class="text-wrapper-8">{{ classic.summary }}</div> -->
              </div>
            </div>
          </div>
          <!-- 修改后的分页组件 -->
          <div class="pagination-list">
            <div class="pagination-page">
              <div
                class="text-wrapper-9"
                @click="prevPage"
                :disabled="page === 1"
              >‹</div>
            </div>
            <div class="element-wrapper">
              <div class="element">{{ page }}</div>
            </div>
            <div class="text-wrapper-9">/</div>
            <div class="element-wrapper">
              <div class="element">{{ classicStore.totalPages }}</div>
            </div>
            <div class="pagination-page">
              <div
                class="text-wrapper-9"
                @click="nextPage"
                :disabled="page === classicStore.totalPages"
              >›</div>
            </div>
            <div class="pagination-input">
              <input
                type="number"
                v-model.number="inputPage"
                min="1"
                :max="classicStore.totalPages"
                @keyup.enter="goToPage"
                placeholder="页码"
              />
              <button @click="goToPage">跳转</button>
            </div>
          </div>

        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import '@/assets/classic.css'
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useClassicStore } from '@/stores/classic'
import { useBookStore } from '@/stores/book'
import Navi from '../components/NaviHomeView.vue'

const router = useRouter()
const route = useRoute()
const classicStore = useClassicStore()
const bookStore = useBookStore()

const book = ref({})
const page = ref(1)
const pageSize = 21
const bookId = parseInt(route.params.bookId)
const inputPage = ref(1) // 新增：用于输入跳转的页码

const loadData = async () => {
  try {
    await bookStore.getBookById(route.params.bookId)
    await classicStore.loadClassics(route.params.bookId, {
      page: page.value - 1,
      size: pageSize,
    })
    // 加载后更新输入框值为当前页
    inputPage.value = page.value
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(loadData)
watch(() => route.params.bookId, loadData)

const classics = computed(() => classicStore.classics)

const goToDetail = (classicId) => {
  router.push({
    name: 'ClassicDetail',
    params: {
      bookId: route.params.bookId, // 传递真实的bookId
      classicId: classicId,
    },
  })
}

const nextPage = () => {
  if (page.value < classicStore.totalPages) {
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
  let targetPage = Math.max(1, Math.min(inputPage.value, classicStore.totalPages))
  page.value = targetPage
  inputPage.value = targetPage // 修正输入值
}

const loadBook = async () => {
  book.value = await bookStore.getBookById(bookId)
}

const loadClassics = async () => {
  await classicStore.loadClassics(bookId, {
    page: page.value - 1,
    size: pageSize,
  })
}

onMounted(() => {
  loadBook()
  loadClassics()
})

watch(page, loadClassics)
</script>

<style scoped>
.classic-item {
  background-color: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e0e6ed;
  display: flex;
  overflow: hidden;
  position: relative;
  min-width: 25%;
}

.classic-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-color: #a0b8e0;
}

.classic-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 6px;
  background: linear-gradient(to bottom, #4a69bd, #6a89cc);
  border-radius: 6px 0 0 6px;
}

.classic-info {
  flex: 1;
  padding-left: 15px;
}

.text-wrapper-6 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.text-wrapper-8 {
  font-size: 15px;
  line-height: 1.5;
  color: #7f8c8d;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.frame-2 {
  display: flex;
  flex-direction: row;
  gap: 20px;
}

.pagination-list {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin: 0 auto 20px;
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