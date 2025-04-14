<!-- <template>
  <div class="profile-container">
    <div v-if="loading" class="loading">加载中...</div>
    <template v-else>
      <h2>个人信息</h2>
      <div class="profile-card">
        <img :src="profile.avatar || defaultAvatar" class="avatar">
        <div class="details">
          <div><strong>用户ID:</strong> {{ profile.userId }}</div>
          <div><strong>用户名:</strong> {{ profile.username }}</div>
          <div><strong>邮箱:</strong> {{ profile.email }}</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { getProfileAPI } from '@/apis/user'

export default {
  data() {
    return {
      loading: true,
      profile: {},
      defaultAvatar: '/assets/images/defaultAvatar.png'
    }
  },
  async created() {
    await this.loadProfile();
  },
  methods: {
    async loadProfile() {
      try {
        const response = await getProfileAPI();
        if (response.success) {
          this.profile = response.data;
        } else {
          console.error('获取失败:', response.message);
        }
      } catch (error) {
        console.error('请求出错:', error);
        if (error.response?.status === 401) {
          this.$router.push('/login');
        }
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.profile-card {
  display: flex;
  gap: 2rem;
  align-items: center;
  margin-top: 1.5rem;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #f0f0f0;
}

.details div {
  margin: 0.8rem 0;
  font-size: 1.1rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}
</style> -->
<template>
  <div class="profile-container">
    <!-- 头像上传 -->
    <div class="avatar-section">
      <img :src="getAvatarUrl(profile.avatar) || defaultAvatar" class="avatar">
      <input
        type="file"
        ref="avatarInput"
        accept="image/*"
        @change="handleAvatarUpload"
        style="display: none"
      >
      <button @click="$refs.avatarInput.click()">更换头像</button>
      <span v-if="avatarUploading" class="uploading-text">上传中...</span>
    </div>

    <!-- 编辑表单 -->
    <form @submit.prevent="handleSubmit" v-if="isEditing">
      <div class="form-group">
        <label>用户id</label>
        <input v-model="profile.userId" readonly> <!-- 用户 ID 设置为只读 -->
      </div>

      <div class="form-group">
        <label>用户名</label>
        <input v-model="editForm.username" required>
      </div>

      <div class="form-group">
        <label>邮箱</label>
        <input v-model="editForm.email" type="email" required>
      </div>

      <div class="form-actions">
        <button type="submit" :disabled="updating">保存</button>
        <button type="button" @click="cancelEdit">取消</button>
      </div>
    </form>

    <!-- 展示模式 -->
    <div v-else class="profile-info">
      <div><strong>用户id:</strong> {{ profile.userId }}</div>
      <div><strong>用户名:</strong> {{ profile.username }}</div>
      <div><strong>邮箱:</strong> {{ profile.email }}</div>
      <button @click="startEditing">编辑资料</button>
    </div>

    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script>
import { getProfileAPI, updateProfileAPI, uploadAvatarAPI } from '@/apis/user'
import defaultAvatar from '/assets/images/defaultAvatar.png'

export default {
  data() {
    return {
      profile: {
        userId: '',
        username: '',
        email: '',
        avatar: ''
      },
      isEditing: false,
      editForm: {
        username: '',
        email: ''
      },
      defaultAvatar,
      updating: false,
      avatarUploading: false,
      message: '',
      messageType: ''
    }
  },
  async created() {
    await this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        const response = await getProfileAPI()
        if (response.success) {
          this.profile = response.data
          // 初始化编辑表单
          this.editForm = {
            username: response.data.username,
            email: response.data.email
          }
        }
      } catch (error) {
        this.showMessage('加载失败', 'error')
        console.error('加载失败:', error)
      }
    },
    startEditing() {
      this.isEditing = true
    },
    cancelEdit() {
      this.isEditing = false
      // 重置表单为原始值
      this.editForm = {
        username: this.profile.username,
        email: this.profile.email
      }
    },
    getAvatarUrl(avatar) {
      return `http://localhost:8080${avatar}`;
    },
    async handleSubmit() {
      this.updating = true
      try {
        const response = await updateProfileAPI(this.editForm)
        if (response.success) {
          this.showMessage('更新成功', 'success')
          await this.loadProfile() // 重新加载数据
          this.isEditing = false
        } else {
          this.showMessage(response.message, 'error')
        }
      } catch (error) {
        this.showMessage('更新失败', 'error')
         console.error('更新失败:', error)
      } finally {
        this.updating = false
      }
    },
    async handleAvatarUpload(e) {
      const file = e.target.files[0]
      if (!file) return

      this.avatarUploading = true
      const formData = new FormData()
      formData.append('file', file)

      try {
        const response = await uploadAvatarAPI(formData)
        if (response.success) {
          this.showMessage('头像更新成功', 'success')
          this.profile.avatar = response.data // 更新头像显示
        }
      } catch (error) {
        this.showMessage('头像上传失败', 'error')
         console.error('头像上传失败:', error)
      } finally {
        this.avatarUploading = false
        e.target.value = '' // 重置input
      }
    },
    showMessage(msg, type) {
      this.message = msg
      this.messageType = type
      setTimeout(() => this.message = '', 3000)
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #f0f0f0;
  display: block;
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

button {
  padding: 0.5rem 1rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
}

.message {
  margin-top: 1rem;
  padding: 0.5rem;
  border-radius: 4px;
}

.message.success {
  background-color: #dff0d8;
  color: #3c763d;
}

.message.error {
  background-color: #f2dede;
  color: #a94442;
}

.uploading-text {
  color: #666;
  margin-left: 1rem;
  font-size: 0.9rem;
}
</style>
