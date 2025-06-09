<template>
  <Navi />
  <div class="classic-container">
    
    <!-- 典籍内容展示区域 -->
     <div 
      class="content-area" 
      @mouseup="onSelect"
      :style="{ width: leftWidth + 'px' }"
    >
      <div class="book-header">
        <h1>{{ classic==null?'':classic.title }}</h1>
            <button @click="goBackToClassics" class="back-button">
      <i class="fas fa-book"></i> 返回目录
    </button>
        <button @click="toggleCollected" class="star-button">
  <span :class="{ collected: isCollected }">★</span>
</button>
      </div>
      <div class="text-section">
        <h3>原文</h3>
        <div class="original-text">{{ classic==null?'':classic.originalText }}</div>
        <h3>白话文</h3>
        <div class="translated-text">{{ classic==null?'':classic.vernacularText }}</div>
        <h3>英文翻译</h3>
        <div class="notes-text">{{ classic==null?'':classic.translateText }}</div>
      </div>
    </div>

    <div 
      class="resize-handle"
      @mousedown="startResize"
    ></div>
    <!-- 主聊天区域 -->
    <div 
      class="main-chat-container"
      :style="{ width: `calc(100% - ${leftWidth + 5}px)` }"
    >
      <!-- 功能按钮栏 -->
      <div class="function-bar">
        <button class="function-button" @click="showHistory = !showHistory">
          <i class="fas fa-history"></i> 历史对话
        </button>
        <button class="new-chat-button" @click="qastore.startNewSession()">
          <i class="fas fa-comment-plus"></i> 新建对话
        </button>
      </div>

      <!-- 聊天区域 -->
      <div class="chat-area">
        <div
  v-for="msg in currentMessages"
  :key="msg.id"
  :class="[
    'message', 
    msg.role, 
    { 
      streaming: msg.streaming,
      error: msg.error
    }
  ]"
>
          <div class="message-header">
             <span class="role-tag">
      {{ msg.role === 'user' ? '你' : 'AI' }}
      <span v-if="msg.streaming" class="streaming-indicator"></span>
    </span>
            <div class="message-actions" v-if="msg.role === 'user'">
              <button class="copy-btn" @click="copyMessage(msg.content)">⎘</button>
              <button class="edit-btn" @click="startEdit(msg.id, msg.content)">✎</button>
              <button class="delete-btn" @click="deleteMsg(msg.id)">×</button>
            </div>
          </div>
          <div class="message-content">
            <template v-if="editingStates[msg.id]?.editing">
      <div class="edit-wrapper">
        <textarea
          ref="editInput"
          v-model="editingStates[msg.id].editedContent"
          @keydown.enter.exact.prevent="handleEditEnter(msg.id)"
          @keyup.esc="cancelEdit(msg.id)"
          class="edit-textarea"
          :style="{ height: editingTextareaHeight + 'px' }"
        ></textarea>
        
        <div class="edit-controls">
          <button 
            @click="saveEdit(msg.id)" 
            :disabled="isSavingEdit"
            class="btn-save"
          >
            <span v-if="isSavingEdit">
              <i class="loading-icon"></i> 生成中...
            </span>
            <span v-else>保存并重新生成 🔄</span>
          </button>
          <button 
            @click="cancelEdit(msg.id)"
            class="btn-cancel"
          >
            取消 ✖
          </button>
        </div>
      </div>
    </template>
<template v-else>
    <!-- 解析think内容 -->
 <div v-if="hasThinkContent(msg.content)" class="think-container">
      <div 
        class="think-toggle"
        @click="toggleThink(msg.id)"
      >
        {{ showThink[msg.id] ? '▲ 隐藏思考过程' : '▼ 显示AI思考过程' }}
      </div>
      <div 
        v-if="showThink[msg.id]"
        class="think-content"
        v-html="formatThinkContent(msg.content)"
      ></div>
    </div>
    
    <!-- 显示正式回答 -->
    <div class="ai-answer">
      {{ extractFinalAnswer(msg.content) }}
    </div>
  </template>
          </div>
          <div v-if="msg.role === 'assistant'" class="message-footer">
            <button @click="copyMessage(msg.content)">⎘ 复制</button>
            <button @click="rate(msg.id, 'good')">👍</button>
            <button @click="rate(msg.id, 'bad')">👎</button>
            <button 
  @click="regenerate(msg.id)" 
  :disabled="loadingMessage[msg.id] || msg.streaming">
  重新生成
</button>
          </div>
        </div>
      </div>

   <div class="input-area">
        <textarea
          v-model="newMessage"
          @keyup.enter.prevent="sendMessage(classicId)"
          :placeholder="quote || '请输入问题...'"
        ></textarea>
        <button @click="sendMessage(classicId)" :disabled="sending">发送</button>
      </div>
    </div>

   <!-- 历史对话弹窗 -->
<div v-if="showHistory" class="history-dialog">
  <div class="history-header">
    <h3>聊天历史 ({{ qastore.sessions.length }})</h3>
    <button @click="showHistory = false">×</button>
  </div>
  <div class="history-tabs">
    <span :class="{ active: historyTab === 'all' }" @click="historyTab = 'all'">全部</span>
  </div>
  <div class="history-search">
    <input type="text" placeholder="搜索" v-model="searchQuery">
  </div>
  <div class="history-list">
    <div
      v-for="session in filteredSessions"
      :key="session.id"
      class="history-item"
    >
    <div class="session-header">
  <div class="session-content" @click="selectSession(session)">
    <h4>{{ session.title || '未命名对话' }}</h4>
  </div>
  <div class="session-actions">
    <button class="rename-btn" @click.stop="qastore.startRename(session)" title="重命名">✎</button>
    <button class="delete-btn" @click.stop="deleteSession(session.id)" title="删除">×</button>
  </div>
</div>
  </div>
  </div>

  <!-- 重命名模态框 -->
  <div v-if="qastore.renamingSession" class="rename-modal">
  <div class="modal-content">
    <h3>重命名对话</h3>
    <input 
      type="text" 
      v-model="qastore.renamingTitle" 
      @keyup.enter="qastore.saveRename"
      placeholder="输入新标题"
      ref="renameInput"
    >
    <div class="modal-actions">
      <button @click="qastore.saveRename">保存</button>
      <button @click="qastore.cancelRename">取消</button>
    </div>
  </div>
</div>
</div>

    <!-- 选中文本提问弹窗 -->
   <SelectionIcon 
      v-if="selectionIconVisible" 
      :style="selectionIconStyle" 
      @click="showPopup"
    />

    <!-- 选中文本提问弹窗 -->
    <SelectionPopup 
      v-if="popupVisible" 
      :style="popupStyle" 
      :selectedText="selectedText"
      @ask="onAskSelection"
      @cancel="popupVisible = false"
    />
  </div>
</template>

<script setup>
import '@/assets/qa.css' 
import { ref, computed, nextTick, onMounted ,onUnmounted} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQAClassicStore } from '@/stores/qa-store'
import { storeToRefs } from 'pinia'
import { getSelectedText, getSelectionPosition } from '@/utils/selection'
import SelectionPopup from '@/views/Classic/SelectionPopup.vue'
import { useCollectedStore } from '@/stores/classic'
import SelectionIcon from '@/views/Classic/SelectionIcon.vue' 
import Navi from '../components/NaviHomeView.vue'

const route = useRoute()
const router = useRouter()
const qastore = useQAClassicStore()
const { classic, currentMessages, newMessage, quote, sending, loadingMessage } = storeToRefs(qastore)

const userId = 1 // 默认用户
const collectedStore = useCollectedStore()

// 返回目录方法
const goBackToClassics = () => {
  router.push({ 
    name: 'Classics', 
    params: { 
      bookId: bookId.value 
    }
  })
}

// onBeforeMount(()=>{
//   console.log(qastore)
//   classic.value = qastore.classic
//   console.log(classic.value)
// })

onMounted(async() => {
  qastore.fetchClassic(classicIdNum.value)
  qastore.fetchSessions(classicIdNum.value)
  
  
  await collectedStore.fetchCollectedStatus(userId, classicIdNum.value)

  console.log('isCollected',collectedStore.isCollected)
})

const leftWidth = ref(700) // 初始宽度
let isResizing = false

// 拖拽处理逻辑
const startResize = () => {
  isResizing = true
  document.body.style.userSelect = 'none' // 禁用文本选择
}

// 全局事件监听
onMounted(() => {
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
})

const handleResize = (e) => {
  if (!isResizing) return
  
  const container = document.querySelector('.classic-container')
  const containerRect = container.getBoundingClientRect()
  const newWidth = e.clientX - containerRect.left
  
  // 限制最小宽度
  leftWidth.value = Math.max(400, Math.min(newWidth, containerRect.width - 400))
}

// 复制方法
const copyMessage = (content) => {
  const textToCopy = extractFinalAnswer(content)
  navigator.clipboard.writeText(textToCopy)
    .then(() => alert('已复制到剪贴板'))
    .catch(err => console.error('复制失败:', err))
}

const stopResize = () => {
  isResizing = false
  document.body.style.userSelect = ''
}

const toggleCollected = () => {
  const title = classic.value.title || '未知标题'
  collectedStore.toggle(userId, classicIdNum.value, title)
}

const isCollected = computed(() => collectedStore.isCollected)

const bookId = computed(() => route.params.bookId)
const classicId = computed(() => route.params.classicId)
const classicIdNum = computed(() => {
  const id = Number(classicId.value)
  return isNaN(id) ? null : id
})

// 编辑状态管理
const editInput = ref(null)
const isSavingEdit = ref(false)
const editingTextareaHeight = ref(0)
const editingStates = ref({}) 

// 选中文字弹窗
const popupVisible = ref(false)
const popupStyle = ref({})

// 历史对话控制
const showHistory = ref(false)
const historyTab = ref('all')
const searchQuery = ref('')

// 会话筛选
const filteredSessions = computed(() => {
  const query = searchQuery.value.toLowerCase()
  let sessions = [...qastore.sessions]
  
  if (historyTab.value === 'favorites') {
    sessions = sessions.filter(s => s.isFavorite)
  }
  
  if (query) {
    sessions = sessions.filter(s => 
      s.title?.toLowerCase().includes(query) || 
      s.preview?.toLowerCase().includes(query)
    )
  }
  
  return sessions
})


const selectionIconVisible = ref(false)
const selectionIconStyle = ref({})
const selectedText = ref('')

// 选中文本处理
const onSelect = () => {
  const text = getSelectedText()
  if (text) {
    selectedText.value = text
    const pos = getSelectionPosition()
    selectionIconStyle.value = { 
      top: `${pos.y}px`, 
      left: `${pos.x}px` 
    }
    selectionIconVisible.value = true
  } else {
    selectionIconVisible.value = false
  }
}

// 显示提问弹窗
const showPopup = () => {
  const pos = getSelectionPosition()
  popupStyle.value = { 
    top: `${pos.y + 25}px`, 
    left: `${pos.x}px` 
  }
  popupVisible.value = true
  selectionIconVisible.value = false
}

// 当前会话ID
const currentSessionId = computed(() => qastore.currentSessionId)

// 提问处理（修改为在当前对话提问）
const onAskSelection = (question) => {
  if (!currentSessionId.value) {
    qastore.startNewSession(question)
  } else {
    newMessage.value = question
    qastore.sendMessage(classicIdNum.value)
    newMessage.value = '' // 清空输入框
  }
  popupVisible.value = false
}


// 发送、重生、评分
const sendMessage = (id) => qastore.sendMessage(id)
const regenerate = (id) => qastore.regenerate(id)
const rate = (id, feedback) => qastore.rate(id, feedback)

// 删除消息
const deleteMsg = (id) => {
  if (confirm('确定删除该消息及其所有后续对话吗？')) {
    qastore.deleteMessage(id)
  }
}
//删除会话
const deleteSession = async (sessionId) => {
  if (confirm('确定要删除这个会话吗？删除后将无法恢复')) {
    try {
      await qastore.deleteSession(sessionId)
      // 如果删除的是当前会话，清空当前消息
      if (qastore.currentSessionId === sessionId) {
        qastore.currentSessionId = null
        qastore.currentMessages = []
      }
    } catch (error) {
      alert('删除失败: ' + error.message)
    }
  }
}

//think处理***************/
// 新增响应式状态
const showThink = ref({})

const hasThinkContent = (content) => {
  return /<think>/.test(content)
}

const extractFinalAnswer = (content) => {
  return content.replace(/<think>[\s\S]*<\/think>/, '').trim()
}

const formatThinkContent = (content) => {
  const match = content.match(/<think>([\s\S]*)<\/think>/)
  if (match) {
    return match[1]
      .replace(/\n/g, '<br>')
      .replace(/ {2}/g, '&nbsp;&nbsp;')
  }
  return ''
}

const toggleThink = (msgId) => {
  showThink.value[msgId] = !showThink.value[msgId]
}
/*********************** */

// 自动调整文本域高度
const calculateTextareaHeight = () => {
  nextTick(() => {
    if (editInput.value) {
      editingTextareaHeight.value = editInput.value.scrollHeight + 2
    }
  })
}

// 处理回车键（Shift+Enter换行）
const handleEditEnter = (msgId) => {
  if (event.shiftKey) {
    editingStates.value[msgId].editedContent += '\n'
    calculateTextareaHeight()
  } else {
    saveEdit(msgId)
  }
}

const startEdit = (msgId, content) => {
  editingStates.value[msgId] = {
    editing: true,
    editedContent: content,
    originalContent: content
  }
  calculateTextareaHeight()
}

const cancelEdit = (msgId) => {
  if (editingStates.value[msgId]) {
    editingStates.value[msgId].editing = false
    editingStates.value[msgId].editedContent = 
      editingStates.value[msgId].originalContent
  }
}

const saveEdit = async (msgId) => {
  if (isSavingEdit.value) return
  isSavingEdit.value = true
  try {
    await qastore.editMessage(
      msgId,
      editingStates.value[msgId].editedContent
    )
    editingStates.value[msgId].editing = false
  } finally {
    isSavingEdit.value = false
  }
}

// 会话选择
const selectSession = async (session) => {
  if (qastore.sending) return
  await qastore.loadSession(session.id)
  showHistory.value = false
}
</script>

<style scoped>
/* .classic-container::before {
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("https://c.animaapp.com/m9pqi0c3GNaMeT/img/back.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.3; 
  z-index: -1;
} */
.book-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

h1 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.star-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 24px;
  padding: 0;
}

.star-button span {
  color: #ccc; /* 默认灰色 */
  transition: color 0.3s ease;
}

.star-button .collected {
  color: #ffd700; /* 收藏状态时的金色 */
}
.text-section h3 {
  font-size: 18px;
  color: #555;
  margin: 30px 0 15px;
}

.original-text, .translated-text, .notes-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

/* 历史触发按钮 */
.history-trigger {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  background-color: white;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  cursor: pointer;
  z-index: 100;
}

.history-trigger i {
  font-size: 24px;
  color: #666;
}

/* 功能按钮栏 */
.function-bar {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;
  gap: 10px;
}

.function-button {
  padding: 8px 15px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.new-chat-button {
  background-color: #6b5deb;
  color: white;
  border: none;
  margin-left: auto;
  border-radius: 20px;
}

/* 聊天区域 */
.chat-area {
  flex: 1;
  
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 10px;
  position: relative;
}

.message.streaming {
  opacity: 0.9;
  background-color: rgba(255, 255, 255, 0.8);
}

.message.streaming .message-content::after {
  content: '...';
  animation: ellipsis 1.5s infinite;
}

@keyframes ellipsis {
  0% { content: '.'; }
  33% { content: '..'; }
  66% { content: '...'; }
}

.message.temp {
  opacity: 0.7;
}
.message.user {
  background-color: #e3f2fd;
  margin-left: 30%;
}

.message.assistant {
  background-color: #fff;
  margin-right: 30%;
  border: 1px solid #e0e0e0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.role-tag {
  font-size: 12px;
  color: #666;
}

.message-actions button {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 14px;
}

/* .message-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
} */

.message-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.message-footer button {
  margin-left: 10px;
  padding: 5px 10px;
  font-size: 12px;
  background: none;
  border: 1px solid #ddd;
  border-radius: 15px;
  cursor: pointer;
}

.input-area {
  padding: 15px;
  background-color: #fff;
  border-top: 1px solid #ddd;
}

.input-area textarea {
  width: 96%;
  height: 100px;
  padding: 2%;
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.6;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
}

.input-area button {
  padding: 8px 20px;
  font-size: 14px;
  background-color: #4a6baf;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

/* 历史对话弹窗 */
.history-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 700px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.2);
  z-index: 1000;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.history-header h3 {
  margin: 0;
  font-size: 18px;
}

.history-header button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.history-tabs {
  display: flex;
  padding: 10px 20px;
  border-bottom: 1px solid #eee;
}

.history-tabs span {
  padding: 5px 15px;
  cursor: pointer;
}

.history-tabs span.active {
  border-bottom: 2px solid #6b5deb;
  font-weight: bold;
}

.history-search {
  padding: 10px 20px;
}

.history-search input {
  width: 95%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
}

.history-list {
  max-height: 500px;
  overflow-y: auto;
  padding: 10px 0;
}

.history-item {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.history-item h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.history-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.message.pending .message-content {
  color: #666;
  font-style: italic;
}
/* 在style部分添加 */
.rename-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}

.modal-content h3 {
  margin-top: 0;
}

.modal-content input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.session-header {
  display: flex;
  justify-content: space-between;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.session-actions {
  display: flex;
  gap: 0;
  margin-left: 8px;
}

.rename-btn, .delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px 3px;
  margin: 0;
  font-size: 14px;
  color: #999;
}

.rename-btn:hover { color: #1890ff; }
.delete-btn:hover { color: #ff4d4f; }
.edit-wrapper {
  position: relative;
  margin: 8px 0;
  border-radius: 8px;
  background: rgba(245, 245, 245, 0.9);
}

.edit-textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s ease;
}

.edit-textarea:focus {
  border-color: #409eff;
  box-shadow: 0 0 8px rgba(64, 158, 255, 0.2);
}

.edit-controls {
  display: flex;
  gap: 8px;
  padding: 8px 12px 12px;
  background: linear-gradient(to bottom, rgba(255,255,255,0.9), #fff);
  border-radius: 0 0 8px 8px;
}

.btn-save, .btn-cancel {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 13px;
  transition: all 0.2s ease;
}

.btn-save {
  background: #409eff;
  color: white;
  border: 1px solid #409eff;
}

.btn-save:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.btn-cancel {
  background: #f4f4f5;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.loading-icon {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid #fff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 0.8s linear infinite;
}

.think-content {
  color: #999;
  font-size: 12px;
  background-color: #f5f5f5;
  padding: 2px 4px;
  border-radius: 4px;
  display: inline-block;
  margin: 2px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
/** 思考内容容器 */
.think-container {
  margin-bottom: 8px;
  border-radius: 4px;
  overflow: hidden;
}

.think-toggle {
  font-size: 12px;
  color: #666;
  background-color: #f5f5f7;
  padding: 4px 8px;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}

.think-toggle:hover {
  background-color: #ebebed;
}

.think-content {
  font-size: 12px;
  color: #666;
  background-color: #f8f9fa;
  padding: 8px 12px;
  line-height: 1.5;
  border-left: 3px solid #e0e0e0;
  white-space: pre-wrap;
}

.ai-answer {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
}
.classic-container {
  position: relative;
  display: flex;
  height: 100vh;
  background-color: #f1efe8;
}

.content-area {
  width: 700px; /* 初始宽度 */
  min-width: 400px; /* 最小宽度 */
  /* height: 100%; */
  padding: 4%;
  overflow-y: auto;
  border-right: 1px solid #eee;
  transition: none; /* 禁用过渡避免拖拽卡顿 */
}

.resize-handle {
  width: 5px;
  background: #ddd;
  cursor: col-resize;
  height: 100%;
  position: relative;
  z-index: 100;
}

.resize-handle:hover,
.resize-handle:active {
  background: #6b5deb;
}

.main-chat-container {
  flex: 1;
  min-width: 400px; /* 最小宽度 */
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background-color: white;
}

/* 添加复制按钮样式 */
.copy-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 14px;
  margin-right: 5px;
}
.copy-btn:hover {
  color: #2196F3;
}
/* 调整停止按钮样式 */
.input-area button[disabled] {
  background-color: #ff4444 !important;
  cursor: not-allowed;
}

/* 在 style 部分添加按钮样式 */
.book-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.back-button {
  padding: 8px 15px;
  background-color: #4a6baf;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  transition: background-color 0.3s;
}

.back-button:hover {
  background-color: #3a5a9f;
}

.back-button i {
  font-size: 14px;
}
</style>