<template>
  <div class="classic-container">
    <!-- 典籍内容区域 -->
    <div class="content-area" @mouseup="handleTextSelection">
      <h1>{{ classic.title }}</h1>
      <div class="text-section">
        <h3>原文</h3>
        <div class="original-text">{{ classic.originalText }}</div>
      </div>
      <!-- 其他文本部分... -->
    </div>

    <!-- 侧边栏 -->
    <div :class="['sidebar', { active: sidebarOpen }]">
      <div class="sidebar-header">
        <h3>问答对话</h3>
        <button @click="startNewSession">新建对话</button>
      </div>
      
      <!-- 对话列表 -->
      <div class="session-list">
        <div v-for="session in sessions" 
             :key="session.id"
             @click="loadSession(session.id)"
             class="session-item">
          {{ session.title }}
        </div>
      </div>

      <!-- 消息区域 -->
      <div class="chat-area">
        <div v-for="message in currentMessages" 
             :key="message.id"
             :class="['message', message.role]">
          <div class="message-content">{{ message.content }}</div>
          <div v-if="message.role === 'assistant'" class="message-actions">
            <button @click="regenerate(message.id)">重新生成</button>
            <div class="feedback-buttons">
              <button @click="rateResponse(message.id, 'good')">👍</button>
              <button @click="rateResponse(message.id, 'bad')">👎</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <textarea v-model="newMessage" @keyup.enter="sendMessage"></textarea>
        <button @click="sendMessage">发送</button>
      </div>
    </div>

    <!-- 选中文本弹窗 -->
    <div v-if="showSelectionPopup" 
         :style="selectionPopupStyle"
         class="selection-popup">
      <button @click="askAboutSelection">提问</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import axios from 'axios'

const props = defineProps(['classicId'])
const classic = ref({})
const sessions = ref([])
const currentMessages = ref([])
const sidebarOpen = ref(true)
const newMessage = ref('')
const currentSessionId = ref(null)
const selectedText = reactive({
  content: '',
  position: { x: 0, y: 0 }
})

// 获取典籍详情
const fetchClassic = async () => {
  const response = await axios.get(`/api/classics/${props.classicId}`)
  classic.value = response.data
}

// 获取历史对话
const fetchSessions = async () => {
  const response = await axios.get('/api/qa/sessions', {
    params: { classicId: props.classicId, userId: 1 } // 替换真实用户ID
  })
  sessions.value = response.data
}

// 创建新对话
const startNewSession = async () => {
  const response = await axios.post('/api/qa/sessions', null, {
    params: { classicId: props.classicId, userId: 1 }
  })
  sessions.value.push(response.data)
  currentSessionId.value = response.data.id
  currentMessages.value = []
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim()) return
  
  const response = await axios.post('/api/qa/messages', {
    sessionId: currentSessionId.value,
    content: newMessage.value,
    role: 'user'
  })
  
  currentMessages.value.push(response.data)
  newMessage.value = ''
  
  // 获取AI响应
  const aiResponse = await axios.post('/api/qa/messages', {
    sessionId: currentSessionId.value,
    content: `关于《${classic.value.title}》的问题：${newMessage.value}`,
    role: 'assistant',
    parentId: response.data.id
  })
  
  currentMessages.value.push(aiResponse.data)
  scrollToBottom()
}

// 文本选中处理
const handleTextSelection = () => {
  const selection = window.getSelection()
  if (selection.toString().trim()) {
    selectedText.content = selection.toString()
    const range = selection.getRangeAt(0)
    const rect = range.getBoundingClientRect()
    selectedText.position = { x: rect.left, y: rect.top }
  }
}

// 关于选中文本提问
const askAboutSelection = async () => {
  if (!currentSessionId.value) await startNewSession()
  
  const message = `关于选中的内容："${selectedText.content}"，我的问题是：`
  newMessage.value = message
  selectedText.content = ''
}

// 重新生成回答
const regenerate = async (messageId) => {
  const response = await axios.post('/api/qa/regenerate', null, {
    params: { messageId }
  })
  currentMessages.value.push(response.data)
  scrollToBottom()
}

// 评分
const rateResponse = async (messageId, feedback) => {
  await axios.post('/api/qa/feedback', null, {
    params: { messageId, feedback }
  })
}

onMounted(() => {
  fetchClassic()
  fetchSessions()
})

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    const chatArea = document.querySelector('.chat-area')
    chatArea.scrollTop = chatArea.scrollHeight
  })
}
</script>

<style scoped>
.classic-container {
  display: flex;
  height: 100vh;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.sidebar {
  width: 400px;
  background: #f5f5f5;
  border-left: 1px solid #ddd;
  transition: transform 0.3s;
  display: flex;
  flex-direction: column;
}

.chat-area {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.message {
  margin: 10px;
  padding: 10px;
  border-radius: 8px;
}

.message.user {
  background: #e3f2fd;
  margin-left: 20%;
}

.message.assistant {
  background: #fff;
  margin-right: 20%;
}

.selection-popup {
  position: fixed;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 5px;
  border-radius: 4px;
}
</style>