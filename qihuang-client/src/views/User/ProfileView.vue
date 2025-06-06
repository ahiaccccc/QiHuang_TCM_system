<template>
  <div class="home-container">
    <Navi :avatar="getAvatarUrl(profile.avatar) || defaultAvatar" :nickname="profile.username" />
    <div class="home-content">
      <h2 class="page-title">个人主页</h2>
      <div class="profile-container">
        <div class="avatar-section">
          <img :src="getAvatarUrl(profile.avatar) || defaultAvatar" class="avatar" />
          <input
            type="file"
            ref="avatarInput"
            accept="image/*"
            @change="handleAvatarUpload"
            style="display: none"
          />
          <button @click="$refs.avatarInput.click()" class="avater-btn">更换头像</button>
          <span v-if="avatarUploading" class="uploading-text">上传中...</span>
        </div>
        <div class="info-section">
          <!-- 用户名 - 编辑时变为输入框 -->
          <div class="info-item">
            <label>用户名:</label>
            <input
              v-if="isEditing"
              v-model="editForm.username"
              type="text"
              class="editable-input"
            />
            <span v-else>{{ profile.username }}</span>
          </div>

          <div class="info-item">
            <label>用户ID:</label>
            <span>{{ profile.userId }}</span>
          </div>
          <div class="info-item">
            <label>邮箱:</label>
            <span>{{ profile.email }}</span>
          </div>

          <div class="action-buttons">
            <template v-if="!isEditing">
              <button class="edit-btn" @click="startEditing">编辑资料</button>
              <button class="reset-btn" @click="resetPassword">重置密码</button>
            </template>
            <template v-else>
              <button class="edit-btn" @click="handleSubmit">保存</button>
              <button class="reset-btn" @click="cancelEdit">取消</button>
            </template>
          </div>
        </div>
      </div>
      <div class="profile-container1">
        <userCollections />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import Navi from '../components/NaviHomeView.vue'
import defaultAvatar from '../../assets/images/defaultAvatar.png'
import userCollections from '@/views/Classic/userCollections.vue'

import { getProfileAPI, updateProfileAPI, uploadAvatarAPI } from '@/apis/user'

const router = useRouter()
const message = useMessage()

const profile = ref({
  username: '',
  userId: '',
  email: '',
  avatar: '',
})

const editForm = ref({
  username: '',
})

const isEditing = ref(false)
const avatarUploading = ref(false)

const loadProfile = async () => {
  try {
    const response = await getProfileAPI()
    if (response.code === 200) {
      profile.value = response.data
      editForm.value = {
        username: response.data.username,
        email: response.data.email,
      }
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
    message.error('加载个人信息失败')
  }
}

const getAvatarUrl = (avatar) => {
  return avatar ? `http://localhost:8080${avatar}` : null
}

const startEditing = () => {
  isEditing.value = true
  editForm.value = {
    username: profile.value.username,
    email: profile.value.email,
  }
}

const cancelEdit = () => {
  isEditing.value = false
}

const validateForm = () => {
  const errors = []

  if (!editForm.value.username) {
    errors.push('用户名不能为空')
  } else if (editForm.value.username.length < 3 || editForm.value.username.length > 50) {
    errors.push('用户名长度必须在3到50个字符之间')
  }
  return errors
}

const handleSubmit = async () => {
  const errors = validateForm()
  if (errors.length > 0) {
    message.error(errors[0])
    return
  }

  try {
    const response = await updateProfileAPI(editForm.value)
    if (response.code === 200) {
      message.success('资料更新成功')
      await loadProfile()
      isEditing.value = false
    } else {
      message.error(response.message || '更新失败')
    }
  } catch (error) {
    if (error.response) {
      if (error.response.status === 405) {
        message.error(error.response.data.msg || '用户名重复')
      } else {
        message.error('服务器错误，请稍后重试')
      }
    } else if (error.request) {
      message.error('网络错误，请检查连接')
    } else {
      message.error('发生未知错误')
    }
  }
}

const handleAvatarUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.match('image.*')) {
    message.error('请选择图片文件')
    return
  }

  avatarUploading.value = true
  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await uploadAvatarAPI(formData)
    if (response.code === 200) {
      message.success('头像更新成功')
      profile.value.avatar = response.data
    } else {
      message.error(response.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    message.error('头像上传失败，请稍后重试')
  } finally {
    avatarUploading.value = false
    e.target.value = ''
  }
}

const resetPassword = () => {
  router.push('/forgetPassword')
}

onMounted(async () => {
  await loadProfile()
})
</script>
<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  width: 80%;
}

.info-item label {
  min-width: 80px;
  font-weight: bold;
  margin-right: 10px;
}

.editable-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  flex-grow: 1;
  font-size: 1rem;
  width: 300px;
}

.info-item span {
  padding: 8px 0;
  flex-grow: 1;
}

.home-container {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.home-content {
  width: 100%;
  background-color: #e6f2f2;
  padding: 20px;
  border-top: none;
  align-items: center;
  gap: 20%;
}

.page-title {
  text-align: left;
  margin-bottom: 20px;
  margin-left: 10%;
  color: #333;
}

.profile-container {
  width: 80%;
  background-color: #ffffff;
  display: flex;
  margin: 0 auto;
  justify-content: center;
  padding: 2% 0;
}

.profile-container1 {
  width: 80%;
  background-color: #ffffff;
  margin: 5% auto;
  /* justify-content: center; */
  padding: 2% 0;
}
.avatar-section {
  margin: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  gap: 10px;
}

.avatar {
  width: 250px;
  height: 250px;
  object-fit: cover;
  border: 3px solid #f0f0f0;
  border-radius: 8px;
}

.avatar-btn {
  background-color: #5c7376;
  color: white;
  border: none;
  padding: 5px 15px;
  border-radius: 3px;
  margin-top: 10px;
  cursor: pointer;
}

.uploading-text {
  color: #666;
  margin-top: 5px;
  font-size: 0.9rem;
}

.info-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.username {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #333;
}

.user-id,
.email,
signature {
  font-size: 1rem;
  color: #444;
  margin: 5px 0;
}

.action-buttons {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
  margin-top: 10px;
}

.avater-btn {
  width: 200px;
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

.avater-btn:hover {
  background-color: #03a6ba;
}

.edit-btn {
  width: 120px;
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

.edit-btn:hover {
  background-color: #03a6ba;
}

.reset-btn {
  width: 120px;
  padding: 0.8rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 0.5rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.reset-btn:hover {
  background-color: #03a6ba;
}

.edit-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 3px;
  font-size: 1rem;
}

.form-input:focus {
  outline: none;
  border-color: #4285f4;
  box-shadow: 0 0 0 2px rgba(66, 133, 244, 0.2);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
</style>
