<template>
  <div class="header-container">
    <div class="navigation">
      <div class="left-column">
        <div
          class="logo"
          @click="goToHome"
        >
          <img
            class="img"
            alt="Logo"
            :src="logo"
          />
          <div class="title">岐黄慧问</div>
        </div>
      </div>

      <div class="middle-column">
        <div
          class="nav-item"
          :class="{ active: currentRoute === 'ChatTest' }"
          @click="navigateTo('ChatTest')"
        >
          AI问答
        </div>
        <div
          class="nav-item"
          :class="{ active: currentRoute === 'classics' }"
          @click="navigateTo('classics')"
        >
          典籍解读
        </div>
        <div
          class="nav-item"
          :class="{ active: currentRoute === 'quiz' }"
          @click="navigateTo('quiz')"
        >
          答题挑战
        </div>
        <div
          v-if="$props.nickname !== 'admin'"
          class="nav-item"
          :class="{ active: currentRoute === 'recomand' }"
          @click="navigateTo('recomand')"
        >
          养生专题
        </div>
      </div>

      <div class="right-column">
        <div class="user-profile">
          <div class="avatar">
            <img
              class="avatar-img"
              :src="avatar"
              alt="用户头像"
            />
          </div>
          <div class="nickname">{{ $props.nickname }}</div>
          <div class="dropdown-menu">
            <div
              class="dropdown-item"
              @click.stop="goToProfile"
            >个人主页</div>
            <div
              class="dropdown-item"
              @click.stop="showDailyCard"
            >每日卡片</div>

            <div
              class="dropdown-item"
              @click.stop="logout"
            >退出</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载遮罩 -->
    <n-spin :show="loading">
      <div
        v-if="loading"
        class="global-mask"
      ></div>
    </n-spin>

    <!-- 中药卡片弹窗 -->
    <n-modal
      v-model:show="showCardModal"
      @clickoutside="closeModal"
    >
      <n-card
        style="width: 600px; max-width: 90vw;"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
      >
        <div class="herb-card">
          <img
            class="herb-image"
            :src="herbCardData.img"
            alt="中药图片"
          />
          <div class="herb-title">
            <n-icon
              size="24"
              class="icon"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z" />
                <path d="M11 7h2v5h-2zm0 6h2v2h-2z" />
              </svg>
            </n-icon>
            <span>每日中药材推荐</span>
          </div>

          <div class="herb-content">
            <h3>{{ herbCardData.name }}</h3>
            <p class="herb-describe">{{ herbCardData.describe }}</p>
            <p class="herb-word">——{{ herbCardData.word }}</p>
          </div>
        </div>
      </n-card>
    </n-modal>

    <div class="header-background">
      <img
        :src="back"
        alt="背景图"
        class="background-image"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NSpin, NModal, NCard } from 'naive-ui'
import logo from '../../assets/images/logo.png'
import back from '../../assets/images/back.png'
import { removeToken } from '@/utils/auth'
import { getHerbCard } from '@/apis/herb-apis'

const props = defineProps({
  avatar: {
    type: String,
    required: true,
  },
  nickname: {
    type: String,
    default: '用户名',
  },
})

const router = useRouter()
const showDropdown = ref(false)
const currentRoute = ref('')
const loading = ref(false)
const showCardModal = ref(false)

// ---------------------中药卡片-------------
const herbCardData = ref(null) // 存储预先获取的卡片数据
const isFetching = ref(false) // 标记是否正在获取数据
const herbData = ref({
  img: '',
  name: '',
  describe: '',
  word: '',
})

const showDailyCard = async () => {
  showDropdown.value = false

  // 如果数据已经存在，直接显示
  if (herbCardData.value) {
    console.log('已存在卡片数据，直接显示', herbCardData.value)
    showCardModal.value = true
    return
  }

  // 如果数据不存在且正在获取中，等待获取完成
  if (isFetching.value) {
    loading.value = true
    // 创建一个轮询检查数据是否已获取
    const checkInterval = setInterval(() => {
      if (herbCardData.value) {
        clearInterval(checkInterval)
        loading.value = false
        showCardModal.value = true
      }
    }, 300)
    return
  }

  // 如果数据不存在且没有在获取中，重新获取
  loading.value = true
  try {
    const response = await getHerbCard()
    herbCardData.value = {
      img: response.img || 'https://sys01.lib.hkbu.edu.hk/cmed/mpid/images/D00483.jpg',
      name: response.name || '宁夏枸杞，中宁枸杞',
      describe: response.describe || '对免疫功能有影响作用...',
      word: response.word || 'what can i say!',
    }
    showCardModal.value = true
  } catch (error) {
    console.error('获取每日卡片失败:', error)
  } finally {
    loading.value = false
  }
}
// 在组件挂载时获取卡片数据
onMounted(async () => {
  if (!herbCardData.value && !isFetching.value) {
    isFetching.value = true
    try {
      const response = await getHerbCard()
      herbCardData.value = {
        img: response.img || 'https://sys01.lib.hkbu.edu.hk/cmed/mpid/images/D00483.jpg',
        name: response.name || '宁夏枸杞，中宁枸杞',
        describe: response.describe || '对免疫功能有影响作用...',
        word: response.word || 'what can i say!',
      }
    } catch (error) {
      console.error('预加载每日卡片失败:', error)
      // 即使失败也设置为false，避免阻塞后续请求
      isFetching.value = false
    } finally {
      isFetching.value = false
    }
  }
})

const closeModal = () => {
  showCardModal.value = false
}

//------------------------------------------------

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const navigateTo = (route) => {
  currentRoute.value = route

  if (props.nickname === 'admin') {
    switch (route) {
      case 'classics':
        router.push('/admin-classics') // 管理员典籍管理页面
        break
      case 'ChatTest':
        router.push('/ChatAdmin') // 管理员AI问答管理页面
        break
      case 'quiz':
        router.push('/quiz-select-admin') // 管理员答题管理页面
        break
    }
  } else {
    switch (route) {
      case 'classics':
        router.push('/books')
        break
      case 'ChatTest':
        router.push('/ChatTest')
        break
      case 'quiz':
        router.push('/quiz-select')
        break
      case 'recomand':
        router.push('/recomand')
        break
    }
  }
}

const goToProfile = () => {
  router.push('/profile')
  showDropdown.value = false
}

const goToHome = () => {
  router.push('/')
  showDropdown.value = false
}

const logout = () => {
  removeToken()
  router.push('/login')
  showDropdown.value = false
}
</script>

<style scoped>
/* 新增样式 */
.herb-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}
.global-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 9998;
}

.herb-card {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.herb-image {
  width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 8px;
}

.herb-content {
  padding: 0 10px;
}

.herb-content h3 {
  color: #333;
  margin-bottom: 15px;
  text-align: center;
}

.herb-describe {
  color: #555;
  line-height: 1.6;
  margin-bottom: 15px;
}

.herb-word {
  color: #888;
  font-style: italic;
  text-align: right;
}

@media (max-width: 768px) {
  .herb-card {
    flex-direction: column;
  }

  .herb-image {
    max-height: 200px;
  }
}

/* --------------------- */
.header-container {
  position: relative;
  width: 100%;
  padding: 0;
  margin: 0;
}

.navigation {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.75);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  height: 50px;
  padding: 0 15%;
  position: relative;
  z-index: 10;
}

.header-background {
  width: 100%;
  height: 200px;
  position: relative;
  z-index: 1;
  margin-top: -50px;
}

.background-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.left-column {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: flex-start;
  min-width: 200px;
}

.middle-column {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 2;
  justify-content: center;
}

.right-column {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: flex-end;
  min-width: 200px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.img {
  height: 28px;
  width: 28px;
  object-fit: cover;
}

.title {
  font-family: 'SimSun';
  font-size: 22px;
  font-weight: 1000;
  line-height: 30px;
}

.nav-item {
  padding: 6px 12px;
  font-size: 16px;
  color: #666666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background-color: rgba(157, 218, 227);
  color: #333333;
}

.nav-item.active {
  background-color: #f5f5f5;
  color: #03a6ba;
  font-weight: 500;
}

.user-profile {
  display: flex;
  align-items: center;
  background-color: rgb(171, 178, 186);
  border-radius: 20px;
  padding: 5px 15px 5px 5px;
  gap: 16px;
  position: relative;
  cursor: pointer;
}

.user-profile:hover .dropdown-menu,
.dropdown-menu:hover {
  display: block;
  opacity: 1;
  visibility: visible;
}

.nickname {
  margin-right: 10px;
  font-size: 16px;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dropdown-menu::before {
  content: '';
  position: absolute;
  bottom: 100%;
  left: 0;
  width: 100%;
  height: 10px;
  background: transparent;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  min-width: 120px;
  display: none;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
}

.dropdown-item {
  padding: 8px 16px;
  color: #333;
  text-align: left;
  transition: background-color 0.2s ease;
}

.dropdown-item:hover {
  background-color: rgba(157, 218, 227);
  color: #333333;
}

@media (max-width: 768px) {
  .navigation {
    padding: 0 15px;
  }
}

@media (max-width: 480px) {
  .navigation {
    height: 50px;
  }
}
</style>
