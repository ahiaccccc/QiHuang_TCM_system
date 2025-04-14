<template>
  <div class="reset-password-container">
    <h2>重置密码</h2>
    <form @submit.prevent="handleSubmit" class="reset-form">
      <div class="form-group">
        <label for="email">注册邮箱</label>
        <input
          id="email"
          v-model="resetForm.email"
          type="email"
          placeholder="请输入您的注册邮箱"
          :class="{ 'is-invalid': errors.email }"
          @input="clearError('email')"
        />
        <div v-if="errors.email" class="error-message">{{ errors.email }}</div>
      </div>

      <button type="submit" :disabled="isSubmitting" class="submit-btn">
        {{ isSubmitting ? '发送中...' : '发送重置邮件' }}
      </button>

      <div v-if="successMessage" class="success-message">
        {{ successMessage }}
      </div>
    </form>
  </div>
</template>

<script>
import { emailResetPasswordAPI } from '@/apis/user'

export default {
  data() {
    return {
      resetForm: {
        email: ''
      },
      errors: {
        email: ''
      },
      isSubmitting: false,
      successMessage: ''
    }
  },
  methods: {
    validateForm() {
      let isValid = true
      this.errors = { email: '' }

      // 邮箱验证
      if (!this.resetForm.email) {
        this.errors.email = '请输入邮箱地址'
        isValid = false
      } else if (!/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(this.resetForm.email)) {
        this.errors.email = '请输入有效的邮箱地址'
        isValid = false
      }

      return isValid
    },
    clearError(field) {
      this.errors[field] = ''
    },
    async handleSubmit() {
      if (!this.validateForm()) return

      this.isSubmitting = true
      this.successMessage = ''

      try {
        const response = await emailResetPasswordAPI({
          email: this.resetForm.email
        })

        if (response.success) {
          this.successMessage = '密码重置邮件已发送，请查收您的邮箱'
          // 3秒后跳转到登录页
          setTimeout(() => {
            this.$router.push('/login')
          }, 3000)
        } else {
          this.errors.email = response.message || '发送失败，请稍后重试'
        }
      } catch (error) {
        console.error('重置密码出错:', error)
        this.errors.email = '请求失败，请检查网络连接'
      } finally {
        this.isSubmitting = false
      }
    }
  }
}
</script>

<style scoped>
.reset-password-container {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.reset-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-weight: 500;
}

input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

input.is-invalid {
  border-color: #ff4444;
}

.error-message {
  color: #ff4444;
  font-size: 0.875rem;
}

.success-message {
  color: #00C851;
  padding: 0.75rem;
  background-color: rgba(0, 200, 81, 0.1);
  border-radius: 4px;
}

.submit-btn {
  padding: 0.75rem;
  background-color: #4285f4;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background-color: #3367d6;
}

.submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>
