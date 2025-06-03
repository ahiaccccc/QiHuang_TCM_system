<template>
  <div class="header-container">
    <div class="navigation">
      <div class="left-column">
        <div class="logo" @click="goToHome">
          <img class="img" alt="Logo" :src="logo" />
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
          class="nav-item"
          :class="{ active: currentRoute === 'ChatAdmin' }"
          @click="navigateTo('ChatAdmin')"
        >
          AI问答管理员端
        </div>
      </div>

      <!-- <div class="right-column">
        <div class="user-profile">
          <div class="avatar">
            <img class="avatar-img" :src="avatar" alt="用户头像" />
          </div>
          <div class="nickname">{{ $props.nickname }}</div>
          <div class="dropdown-menu">
            <div class="dropdown-item" @click.stop="goToProfile">个人主页</div>
            <div class="dropdown-item" @click.stop="logout">退出</div>
          </div>
        </div>
      </div>-->
    </div>

    <div class="header-background">
      <img :src="back" alt="背景图" class="background-image" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import logo from '../../assets/images/logo.png'
import back from '../../assets/images/back.png'
// import { removeToken } from '@/utils/auth'

defineProps({
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

const navigateTo = (route) => {
  currentRoute.value = route
  switch (route) {
    case 'classics':
      router.push('/classics')
      break
    case 'ChatTest':
      router.push('/ChatTest')
      break
      case 'ChatAdmin':
      router.push('/ChatAdmin')
      break
  }
}

// const goToProfile = () => {
//   router.push('/profile')
//   showDropdown.value = false
// }

const goToHome = () => {
  router.push('/')
  showDropdown.value = false
}

// const logout = () => {
//   removeToken()
//   router.push('/login')
//   showDropdown.value = false
// }
</script>

<style scoped>
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
