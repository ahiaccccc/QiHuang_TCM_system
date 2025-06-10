<template>
  <div class="classics">
       <Navi
    :avatar="getAvatarUrl(profile.avatar) || defaultAvatar"
    :nickname="profile.username"
  />
    <div class="hero-section">
      <div class="main-area">
        <div class="main-area-header">
          <div class="frame">
            <div class="text-wrapper">{{ book.name }}</div>
          </div>

          <div class="frame-wrapper-right">
            <button @click="$router.push('/books')" class="text-wrapper-3">返回</button>
          </div>
        </div>


        <div class="div-wrapper">
          <div class="frame-2">
             <div v-for="classic in classics" :key="classic.id" 
       class="classic-item" 
       @click="goToDetail(classic.id)">
              <div class="classic-info">
                <div class="text-wrapper-6">{{ classic.title }}</div>
                <!-- <div class="text-wrapper-8">{{ classic.summary }}</div> -->
              </div>
            </div>
          </div>
          <!-- 修改后的分页组件 -->
          <div class="pagination-list">
            <div class="pagination-page">
              <div class="text-wrapper-9" @click="prevPage" :disabled="page === 1">‹</div>
            </div>
            <div class="element-wrapper">
              <div class="element">{{ page }}</div>
            </div>
            <div class="text-wrapper-9">/</div>
            <div class="element-wrapper">
              <div class="element">{{ classicStore.totalPages }}</div>
            </div>
            <div class="pagination-page">
              <div class="text-wrapper-9" @click="nextPage" :disabled="page === classicStore.totalPages">›</div>
            </div>
            <div class="pagination-input">
              <input type="number" v-model.number="inputPage" min="1" :max="classicStore.totalPages"
                @keyup.enter="goToPage" placeholder="页码" />
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
import Navi from '../components/NaviHomeView2.vue'
import { getProfileAPI } from '@/apis/user'

const router = useRouter()
const route = useRoute()
const classicStore = useClassicStore()
const bookStore = useBookStore()

const book = ref({})
const page = ref(1)
const pageSize = 18
const bookId = parseInt(route.params.bookId)
const inputPage = ref(1) // 新增：用于输入跳转的页码

const profile = ref({
  username: '',
  userId: '',
  email: '',
  avatar: '',
})

  const loadProfile = async () => {
  try {
    const response = await getProfileAPI()
    if (response.code === 200) {
      profile.value = response.data
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}
const defaultAvatar = ref('@/assets/images/logo.png')
const getAvatarUrl = (avatar) => {
  return avatar ? `http://localhost:8080${avatar}` : null
}

const loadData = async () => {
  try {
    await bookStore.getBookById(route.params.bookId)
    await classicStore.loadClassics(route.params.bookId, {
      page: page.value - 1,
      size: pageSize
    })
    // 加载后更新输入框值为当前页
    inputPage.value = page.value
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(loadData)
onMounted(() => {
    profile.value = {
    username: '',
    userId: '',
    email: '',
    avatar: '',
  }
  loadProfile()
})
watch(() => route.params.bookId, loadData)

 const classics = computed(() => classicStore.classics)


const goToDetail = (classicId) => {
  router.push({ 
    name: 'ClassicDetail', 
    params: { 
      bookId: route.params.bookId,  // 传递真实的bookId
      classicId: classicId
    }
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
    size: pageSize
  })
}

onMounted(() => {
  loadBook()
  loadClassics()
})

watch(page, loadClassics)
</script>

<style scoped>.classic-item {
  position: relative;
  background-color: #f9faf1; /* 宣纸底色 */
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e0e6ed;
  display: flex;
  overflow: visible;
  min-width: 25%;
}

/* 远山 - 增强层次和对比度 */
.classic-item::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 20% 30%, #b6d2c9 0%, transparent 60%),
    radial-gradient(circle at 75% 40%, #a8cbbf 0%, transparent 55%),
    radial-gradient(circle at 45% 65%, #9bb9ae 0%, transparent 70%);
  opacity: 0.35;
  pointer-events: none;
  z-index: 1;
}

/* 近水 - 增强水流效果 */
.classic-item::after {
  content: "";
  position: absolute;
  right: 15px;
  bottom: 15px;
  width: 100px;
  height: 50px;
  background: 
    linear-gradient(120deg, #d8e8e3 0%, #e6f2ee 50%, #d8e8e3 100%);
  border-radius: 70% 30% 60% 40%;
  opacity: 0.65;
  transform: rotate(8deg);
  pointer-events: none;
  z-index: 2;
  box-shadow: 
    inset 0 3px 5px rgba(255, 255, 255, 0.9),
    inset 0 -3px 5px rgba(210, 225, 220, 0.8),
    0 2px 5px rgba(220, 235, 230, 0.5);
}

/* 新增流动波纹效果 */
.classic-item::after {
  content: "";
  position: absolute;
  right: 15px;
  bottom: 15px;
  width: 100px;
  height: 50px;
  background: 
    linear-gradient(120deg, #d8e8e3 0%, #e6f2ee 50%, #d8e8e3 100%);
  border-radius: 70% 30% 60% 40%;
  opacity: 0.65;
  transform: rotate(8deg);
  pointer-events: none;
  z-index: 2;
  box-shadow: 
    inset 0 3px 5px rgba(255, 255, 255, 0.9),
    inset 0 -3px 5px rgba(210, 225, 220, 0.8),
    0 2px 5px rgba(220, 235, 230, 0.5);
}

/* 新增溪流效果 */
.classic-item::before {
  content: "";
  position: absolute;
  left: 20px;
  bottom: 25px;
  width: 40px;
  height: 15px;
  background: 
    linear-gradient(90deg, transparent, #e0f0ea 40%, #e0f0ea 60%, transparent);
  border-radius: 50%;
  opacity: 0.6;
  transform: rotate(-5deg);
  pointer-events: none;
  z-index: 2;
}

/* 新增云雾效果 - 调整位置使其更突出 */
.classic-item::before {
  content: "";
  position: absolute;
  left: -10px;
  top: -10px;
  width: calc(100% + 20px);
  height: calc(100% + 20px);
  background: 
    radial-gradient(circle at 25% 15%, rgba(255, 255, 255, 0.7) 0%, transparent 45%),
    radial-gradient(circle at 75% 85%, rgba(255, 255, 255, 0.6) 0%, transparent 40%);
  opacity: 0.35;
  pointer-events: none;
  z-index: 0;
}

/* 悬停效果 - 增强山水动效 */
.classic-item:hover::before {
  opacity: 0.45;
  background: 
    radial-gradient(circle at 20% 30%, #b6d2c9 0%, transparent 62%),
    radial-gradient(circle at 75% 40%, #a8cbbf 0%, transparent 57%),
    radial-gradient(circle at 45% 65%, #9bb9ae 0%, transparent 72%);
}

.classic-item:hover::after {
  opacity: 0.75;
  transform: rotate(10deg) scale(1.02);
}
.classic-item {
  position: relative;
  background-color: #f9faf2; /* 宣纸底色 */
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e0e6ed;
  display: flex;
  overflow: visible;
  min-width: 25%;
}

/* 远山 - 更淡的青绿色调 */
.classic-item::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 25% 30%, #d8e6e3 0%, transparent 55%),
    radial-gradient(circle at 70% 45%, #d4e8e0 0%, transparent 60%),
    radial-gradient(circle at 45% 70%, #d2e6de 0%, transparent 75%);
  opacity: 0.25;
  pointer-events: none;
  z-index: 1;
}

/* 近水 - 超淡的波纹 */
.classic-item::after {
  content: "";
  position: absolute;
  right: 15px;
  bottom: 15px;
  width: 90px;
  height: 45px;
  background: 
    linear-gradient(135deg, rgba(235, 245, 243, 0.8), rgba(240, 248, 246, 0.9));
  border-radius: 60% 30% 55% 35%;
  opacity: 0.5;
  transform: rotate(5deg);
  pointer-events: none;
  z-index: 2;
  box-shadow: 
    inset 0 2px 3px rgba(255, 255, 255, 0.8),
    inset 0 -2px 3px rgba(220, 235, 230, 0.7);
}

/* 增添云雾效果 */
.classic-item::before {
  content: "";
  position: absolute;
  left: -10px;
  top: -10px;
  width: calc(100% + 20px);
  height: calc(100% + 20px);
  background: 
    radial-gradient(circle at 30% 10%, rgba(255, 255, 255, 0.6) 0%, transparent 40%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.5) 0%, transparent 30%);
  opacity: 0.3;
  pointer-events: none;
  z-index: 0;
}

/* 悬停效果增强 */
.classic-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(200, 215, 210, 0.2);
  background-color: #fbfcf7;
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
  margin:0 auto 20px;
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