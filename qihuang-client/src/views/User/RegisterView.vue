<template>
  <form @submit.prevent="handleSubmit" class="auth-form">
    <h2 class="form-title">用户注册</h2>

    <div class="form-group">
      <input
        v-model="form.username"
        placeholder="Username"
        class="form-input"
      />
    </div>

    <div class="form-group">
      <input
        v-model="form.email"
        placeholder="Email"
        class="form-input"
        type="email"
      />
    </div>

    <div class="form-group">
      <input
        v-model="form.password"
        type="password"
        placeholder="Password"
        class="form-input"
      />
    </div>

    <div class="form-group">
      <input
        v-model="form.confirmPassword"
        type="password"
        placeholder="Confirm Password"
        class="form-input"
      />
    </div>

    <button
      type="submit"
      :disabled="loading"
      class="submit-btn"
      :class="{ 'loading': loading }"
    >
      {{ loading ? '注册中...' : '注册' }}
    </button>

    <div v-if="error" class="error-message">
      {{ error }}
    </div>
  </form>
</template>


<script>
import { registerAPI } from '@/apis/user'

export default {
  data() {
    return {
      form: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
      },
      error: '',
      loading: false,
      errorMessage: ''
    }
  },
  methods: {
    async handleSubmit() {
      this.loading = true
      this.errorMessage = ''
      try {
        console.log('发送请求:', this.form)  // 确认数据格式
        const response = await registerAPI(this.form)
        console.log('注册成功:', response)
        this.$router.push('/login')
      } catch (err) {
          this.errorMessage ='注册失败'
        this.error = err.response?.data?.message || '请求失败，请检查控制台'
      } finally {
        this.loading = false
      }
    },
  },
}
</script>

<style scoped>
.auth-form {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  background: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-title {
  text-align: center;
  color: #333;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.2rem;
}

.form-input {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #4a90e2;
}

.submit-btn {
  width: 100%;
  padding: 0.8rem;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #3a7bc8;
}

.submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.submit-btn.loading {
  position: relative;
}

.error-message {
  margin-top: 1rem;
  padding: 0.8rem;
  background-color: #ffebee;
  color: #d32f2f;
  border-radius: 4px;
  text-align: center;
}
</style>
