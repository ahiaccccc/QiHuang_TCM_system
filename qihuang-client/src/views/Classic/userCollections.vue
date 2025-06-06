<template>
    <div class="collected-books-container">
    <div class="header">
      <h1><i class="fas fa-bookmark"></i> 我的收藏</h1>
    </div>

    <div v-if="loading" class="loading-container">
      <i class="fas fa-spinner fa-spin"></i> 加载中...
    </div>

    <div v-else-if="collectedBooks.length === 0" class="empty-container">
      <i class="fas fa-book-open"></i>
      <p>您还没有收藏任何典籍</p>
      <router-link to="/books" class="browse-link">
        去浏览典籍
      </router-link>
    </div>

    <div v-else class="books-grid">
      <div 
        v-for="book in collectedBooks" 
        :key="book.collectedId" 
        class="book-card"
        @click="goToClassicDetail(book.bookId, book.classicId)"
      >
        <div class="card-header">
          <h3>{{ book.title }}</h3>
          <button 
            class="uncollect-btn"
            @click.stop="handleToggleCollected(book.userId, book.classicId, book.title)"
          >
            <i class="fas fa-bookmark"></i>
          </button>
        </div>
        
        <div class="card-body">
          <p class="book-name">所属典籍: {{ book.bookName }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserCollected } from '@/apis/collected'
import { toggleCollected as toggleCollectedAPI } from '@/apis/collected'
import { useCollectedStore } from '@/stores/classic'
import { getProfileAPI } from '@/apis/user'

const router = useRouter()
const collectedStore = useCollectedStore()

const collectedBooks = ref([])
const loading = ref(true)
const profile = ref({
  userId: null
})

// 加载用户信息和收藏列表
const loadData = async () => {
  try {
    // 获取用户信息
    const userRes = await getProfileAPI()
    if (userRes.code === 200) {
      profile.value = userRes.data
      
      // 获取收藏列表
      const collectedRes = await getUserCollected(profile.value.userId)
      console.log('收藏列表:', collectedRes)
      collectedBooks.value = collectedRes || []
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    collectedBooks.value = []
  } finally {
    loading.value = false
  }
}


// 跳转到典籍详情
const goToClassicDetail = (bookId, classicId) => {
  router.push({
    name: 'ClassicDetail',
    params: { bookId, classicId }
  })
}

// 取消收藏
const handleToggleCollected = async (userId, classicId, title) => {
  try {
    await toggleCollectedAPI(userId, classicId, title)
    // 刷新收藏列表
    const collectedRes = await getUserCollected(userId)
    collectedBooks.value = collectedRes.data || []
    
    // 更新收藏状态
    collectedStore.isCollected = false
  } catch (error) {
    console.error('操作收藏失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.collected-books-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: left;
  margin: 0 0;
  color: #2c3e50;
}

.header h1 {
  font-size: 1.6rem;
  margin-bottom: 10px;
}

.header i {
  color: #e74c3c;
  margin-right: 10px;
}

.loading-container {
  text-align: center;
  padding: 50px;
  font-size: 1.2rem;
  color: #7f8c8d;
}

.loading-container i {
  font-size: 2rem;
  margin-right: 10px;
}

.empty-container {
  text-align: center;
  padding: 50px;
  color: #7f8c8d;
}

.empty-container i {
  font-size: 4rem;
  color: #bdc3c7;
  margin-bottom: 20px;
}

.empty-container p {
  font-size: 1.2rem;
  margin-bottom: 20px;
}

.browse-link {
  display: inline-block;
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.3s;
}

.browse-link:hover {
  background-color: #2980b9;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
  margin-top: 20px;
}

.book-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 7px solid #64bfaa;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.card-header h3 {
  margin: 0;
  font-size: 1.4rem;
  color: #2c3e50;
  flex: 1;
}

.uncollect-btn {
  background: none;
  border: none;
  color: #e74c3c;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 5px;
}

.uncollect-btn:hover {
  color: #c0392b;
}

.card-body p {
  margin: 8px 0;
  font-size: 0.95rem;
  color: #555;
}

.book-name {
  font-weight: bold;
  color: #3498db !important;
}

.collect-time {
  font-size: 0.85rem !important;
  color: #95a5a6 !important;
}
</style>