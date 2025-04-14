<template>
  <div class="login-container">
    <h2>用户登录</h2>
    <form @submit.prevent="handleLogin" class="login-form">
      <div class="form-group">
        <label>邮箱</label>
        <input
          v-model="loginForm.email"
          type="email"
          placeholder="请输入邮箱"
          required
        />
      </div>

      <div class="form-group">
        <label>密码</label>
        <input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          required
        />
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </form>
  </div>
</template>

<script>
import { loginAPI } from '@/apis/user'
import { setToken } from '@/utils/user'

export default {
  data() {
    return {
      loginForm: {
        email: '786825156@qq.com',
        password: '123456'
      },
      loading: false,
      errorMessage: ''
    }
  },
  methods: {
    async handleLogin() {
      this.loading = true
      this.errorMessage = ''

      try {
        const response = await loginAPI(this.loginForm)
        console.log('回答:', response);
        if (response.success) {
          setToken(response.data.token) // 存储Token
          console.log("token:", response.data.token)
          this.$router.push('/profile')
        } else {
          this.errorMessage = response.message || '登录失败'
        }
      } catch (error) {
        this.errorMessage = '网络错误，请稍后重试'
        console.error('登录出错:', error)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
}

.error-message {
  color: red;
  margin-top: 10px;
}
</style>
