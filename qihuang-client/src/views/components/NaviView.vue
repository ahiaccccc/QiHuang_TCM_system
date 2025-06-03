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
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.navigation {
  display: flex;
  align-items: center;
  background: linear-gradient(
    to right,
    rgba(85, 100, 119, 0.4),
    rgba(133, 157, 189, 0.2) 75%,
    rgba(133, 157, 189, 1)
  );
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  height: 50px;
  padding: 0 20px;
  position: relative;
}

.left-column {
  position: absolute;
  left: 15%; /* 距离父元素左侧 15% */
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
