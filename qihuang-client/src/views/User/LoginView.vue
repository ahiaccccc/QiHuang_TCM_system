<template>
  <div class="login-container">
    <img
      class="back-image"
      :src="back"
      alt="Background"
    />
    <Navi />

    <div class="login-content">
      <div class="login-card">
        <h2 class="login-title">登录</h2>

        <form
          @submit.prevent="handleLogin"
          class="login-form"
        >
          <div class="form-group">
            <label class="input-label">邮箱</label>
            <input
              v-model="loginForm.email"
              type="email"
              class="form-input"
              placeholder="请输入邮箱"
              required
            />
          </div>

          <div class="form-group">
            <label class="input-label">密码</label>
            <input
              v-model="loginForm.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              required
            />
          </div>

          <div class="form-actions-container">
            <router-link
              to="/register"
              class="line-button form-actions-start"
            >
              前往注册
            </router-link>
            <router-link
              to="/forgetPassword"
              class="line-button form-actions-end"
            >
              忘记密码？
            </router-link>
          </div>

          <button
            type="submit"
            class="submit-btn"
            :disabled="loading"
          >
            {{ loading ? '登录中...' : '确定' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import Navi from '../components/NaviView.vue'
import back from '../../assets/images/back.png'
import { loginAPI } from '@/apis/user'
import { setToken } from '@/utils/auth'

const router = useRouter()
const message = useMessage()

const loginForm = ref({
  email: '',
  password: '',
})

const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  console.log('登录中')

  try {
    const response = await loginAPI(loginForm.value)
    console.log('登录响应:', response)
    if (response.code === 200) {
      console.log('登录成功:', response.data)
      setToken(response.data.token)
      router.push('/profile')
    } else {
      message.error(response.msg || '登录失败')
      console.error(response.msg)
    }
  } catch (error) {
    if (error.response) {
      if (error.response.status === 405) {
        message.error(error.response.data.msg || '用户名或密码错误')
      } else {
        message.error('服务器错误，请稍后重试')
      }
    } else if (error.request) {
      message.error('网络错误，请检查连接')
    } else {
      message.error('发生未知错误')
    }

    console.error('登录出错:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.login-container {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.back-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: -1;
}

.login-content {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  padding: 2rem;
}

.login-card {
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 1.5rem;
  padding: 2.5rem;
  width: 100%;
  max-width: 28rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.login-title {
  font-size: 2.3rem;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 2rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 3.3rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-label {
  font-size: 1rem;
  font-weight: 500;
  color: #444;
}

.form-input {
  padding: 0.8rem 1rem;
  border: 1px solid #ddd;
  border-radius: 0.5rem;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #03a6ba;
}

.form-actions-container {
  display: flex;
  justify-content: space-between;
  margin-right: 5%;
  margin-left: 5%;
}

.line-button:hover {
  color: #03a6ba;
  text-decoration: underline;
}

.line-button {
  color: #666;
  font-size: 1rem;
  text-decoration: none;
  transition: color 0.3s;
}

.submit-btn {
  padding: 0.8rem;
  background-color: #35565a;
  color: white;
  border: none;
  border-radius: 0.5rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #03a6ba;
}

.submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #f56c6c;
  font-size: 0.875rem;
  text-align: center;
  margin-top: 1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    padding: 1.5rem;
    max-width: 100%;
  }

  .login-title {
    font-size: 1.75rem;
    margin-bottom: 1.5rem;
  }

  .login-form {
    gap: 1.25rem;
  }
}
</style>
