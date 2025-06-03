<template>
  <div class="navigation">
    <div class="left-column">
      <div class="logo" @click="goToHome">
        <img class="img" alt="Logo" :src="logo" />
        <div class="title">岐黄慧问</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import logo from '../../assets/images/logo.png'
import { useRouter } from 'vue-router'

const router = useRouter()
const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.navigation {
  display: flex;
  align-items: center;
  background: #dde7f1;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  height: 50px;
  width: 100%;
  padding: 0 20px;
  position: relative;
}

.left-column {
  position: absolute;
  left: 15%;
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

/* 响应式断点 */
@media (max-width: 768px) {
  .navigation {
    padding: 0 15px;
  }

  .title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .navigation {
    height: 50px;
  }

  .title {
    font-size: 16px;
  }
}
</style>
