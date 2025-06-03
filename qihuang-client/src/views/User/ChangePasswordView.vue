<template>
  <div class="register-container">
    <Navi />

    <div class="register-content">
      <div class="register-form-section">
        <div class="register-form-container">
          <h2 class="register-title">修改密码</h2>

          <form @submit.prevent="handleRegister" class="register-form">
            <div class="register-form-group">
              <label class="register-form-label">旧密码</label>
              <input
                v-model="registerForm.oldPassword"
                type="password"
                class="register-form-input"
                placeholder="请输入旧密码"
              />
            </div>

            <div class="register-form-group">
              <label class="register-form-label">新密码</label>
              <input
                v-model="registerForm.newPassword"
                type="password"
                class="register-form-input"
                placeholder="请输入新密码"
              />
            </div>

            <div class="register-form-group">
              <label class="register-form-label">确认新密码</label>
              <input
                v-model="registerForm.confirmPassword"
                type="password"
                class="register-form-input"
                placeholder="请再次输入新密码"
              />
            </div>

            <div class="regiter-form-group">
              <router-link to="/profile" class="line-button form-actions-start">
                返回主页
              </router-link>
            </div>

            <button type="submit" class="register-submit-btn" :disabled="loading">
              {{ loading ? '修改密码中...' : '继续 >' }}
            </button>
          </form>
        </div>
      </div>

      <div class="register-image-section">
        <img class="register-image" :src="back" alt="Register" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { changePasswordAPI } from '@/apis/user'
import Navi from '../components/NaviRegisterView.vue'
import back from '../../assets/images/rightBack.png'

const router = useRouter()
const message = useMessage()

const registerForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const loading = ref(false)

const validateForm = () => {
  const errors = []
  if (!registerForm.value.oldPassword) {
    errors.push('旧密码不能为空')
  } else if (
    registerForm.value.oldPassword.length < 6 ||
    registerForm.value.oldPassword.length > 20
  ) {
    errors.push('旧密码长度必须在6到20个字符之间')
  }

  if (!registerForm.value.newPassword) {
    errors.push('新密码不能为空')
  } else if (
    registerForm.value.newPassword.length < 6 ||
    registerForm.value.newPassword.length > 20
  ) {
    errors.push('新密码长度必须在6到20个字符之间')
  }

  if (!registerForm.value.confirmPassword) {
    errors.push('请确认新密码')
  } else if (registerForm.value.newPassword !== registerForm.value.confirmPassword) {
    errors.push('两次输入的新密码不一致')
  }

  return errors
}

const handleRegister = async () => {
  loading.value = true
  try {
    const errors = validateForm()
    if (errors.length > 0) {
      message.error(errors[0])
      return
    }

    const response = await changePasswordAPI(registerForm.value)
    if (response.code === 200) {
      message.success('修改密码成功！即将跳转至登录页')
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      console.log(response.msg)
      message.error(response.msg || '注册失败，请检查信息')
    }
  } catch (error) {
    if (error.response) {
      if (error.response.status === 405) {
        message.error(error.response.data.msg || '修改失败，请检查信息')
      } else {
        message.error('服务器错误，请稍后重试')
      }
    } else if (error.request) {
      message.error('网络错误，请检查连接')
    } else {
      message.error('发生未知错误')
    }

    console.error('修改密码出错:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.logo {
  display: flex;
  align-items: center;
  gap: 5px;
}

.logo-img {
  height: 25px;
  width: 27px;
  object-fit: cover;
}

.logo-text {
  color: var(--black-1);
  font-family: 'STZhongsong-Regular', Helvetica;
  font-size: 20px;
  font-weight: 400;
  line-height: 30px;
}

.register-content {
  display: flex;
  height: calc(100vh - 60px);
}

.register-form-section {
  width: 55%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fffef8;
}

.register-form-container {
  width: 427px;
  padding: 40px;
}

.register-title {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 40px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.register-form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.register-form-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.register-form-input {
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.register-form-input:focus {
  outline: none;
  border-color: #03a6ba;
}

.register-submit-btn {
  padding: 12px;
  background-color: #34565a;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 16px;
}

.register-submit-btn:hover {
  background-color: #03a6ba;
}

.register-submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.register-image-section {
  width: 45%;
  height: 100%;
}

.register-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
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

@media (max-width: 768px) {
  .register-content {
    flex-direction: column;
  }

  .register-form-section,
  .register-image-section {
    width: 100%;
    height: auto;
  }

  .register-form-container {
    width: 100%;
    padding: 20px;
  }
}
</style>
