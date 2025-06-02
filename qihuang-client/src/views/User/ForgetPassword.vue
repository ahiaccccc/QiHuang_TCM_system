<template>
  <div class="reset-password-container">
    <img class="back-image" :src="back" alt="Background" />

    <Navi />

    <div class="reset-password-content">
      <div class="reset-password-card">
        <h2 class="reset-password-title">重置密码</h2>

        <form @submit.prevent="handleSubmit" class="reset-form">
          <div class="form-group">
            <label class="input-label">注册邮箱</label>
            <input
              id="email"
              v-model="resetForm.email"
              type="email"
              class="form-input"
              placeholder="请输入您的注册邮箱"
              :class="{ 'is-invalid': errors.email }"
              @input="clearError('email')"
            />
            <div v-if="errors.email" class="error-message">{{ errors.email }}</div>
          </div>

          <div class="form-actions">
            <router-link to="/login" class="line-button"> 返回登录 </router-link>
          </div>

          <button type="submit" :disabled="isSubmitting" class="submit-btn">
            {{ isSubmitting ? '发送中...' : '发送邮件' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { emailResetPasswordAPI } from '@/apis/user'
import Navi from '../components/NaviView.vue'
import back from '../../assets/images/back.png'
import { useMessage } from 'naive-ui'

export default {
  components: {
    Navi,
  },
  setup() {
    const message = useMessage()
    return { message }
  },
  data() {
    return {
      resetForm: {
        email: '',
      },
      errors: {
        email: '',
      },
      isSubmitting: false,
      back,
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

      try {
        const response = await emailResetPasswordAPI({
          email: this.resetForm.email,
        })

        if (response.code === 200) {
          this.message.success('密码重置邮件已发送，请查收您的邮箱')
          // 3秒后跳转到登录页
          setTimeout(() => {
            this.$router.push('/login')
          }, 3000)
        } else {
          this.errors.email = response.message || '发送失败，请稍后重试'
          this.message.error(response.message || '发送失败，请稍后重试')
        }
      } catch (error) {
        console.error('重置密码出错:', error)
        this.errors.email = '请求失败，请检查网络连接'
        this.message.error('请求失败，请检查网络连接')
      } finally {
        this.isSubmitting = false
      }
    },
  },
}
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.reset-password-container {
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

.reset-password-content {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
  padding: 2rem;
}

.reset-password-card {
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 1.5rem;
  padding: 2.5rem;
  width: 100%;
  max-width: 28rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.reset-password-title {
  font-size: 2.3rem;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 2rem;
}

.reset-form {
  display: flex;
  flex-direction: column;
  gap: 3.6rem;
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
  padding: 0.6rem 0.8rem;
  border: 1px solid #ddd;
  border-radius: 0.5rem;
  font-size: 1rem;
  transition: border-color 0.3s;
  width: 100%;
}

.form-input:focus {
  outline: none;
  border-color: #03a6ba;
}

.form-input.is-invalid {
  border-color: #ff4444;
}

.error-message {
  color: #ff4444;
  font-size: 0.875rem;
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

.form-actions {
  display: flex;
  justify-content: flex-start;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .reset-password-card {
    padding: 1.5rem;
    max-width: 100%;
  }

  .reset-password-title {
    font-size: 1.75rem;
    margin-bottom: 1.5rem;
  }

  .reset-form {
    gap: 1.25rem;
  }
}
</style>
