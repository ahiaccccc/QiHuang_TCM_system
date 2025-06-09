
<template>
  <div class="admin-conversations">
    <div class="header">
      <h1>用户对话管理</h1>
      <button
        class="refresh-btn"
        @click="fetchConversations"
        :disabled="loading"
      >
        {{ loading ? '加载中...' : '刷新列表' }}
      </button>
    </div>
    <div class="search-bar">
      <input
        v-model="searchParams.convId"
        type="number"
        placeholder="对话ID"
        class="search-input"
      >
      <input
        v-model="searchParams.title"
        type="text"
        placeholder="对话标题"
        class="search-input"
      >
      <input
        v-model="searchParams.userId"
        type="number"
        placeholder="用户ID"
        class="search-input"
      >
      <button
        @click="handleSearch"
        class="search-btn"
      >
        搜索
      </button>
      <button
        @click="resetSearch"
        class="reset-btn"
      >
        重置
      </button>
    </div>

    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-if="conversations.length === 0 && !loading" class="empty-state">
      没有找到任何对话记录
    </div>

    <div v-else class="conversation-list">
      <div
        v-for="conv in conversations"
        :key="conv.id"
        class="conversation-card"
      >
        <div class="conversation-header">
          <span class="conv-id">#{{ conv.id }}</span>
          <span class="username">{{ conv.user.username }}</span>
          <span class="user-id">(ID: {{ conv.user.userId }})</span>
        </div>

        <div class="conversation-body">
          <h3 class="conv-title">{{ conv.title || '无标题' }}</h3>
          <div class="conv-meta">
            <span class="created-at">
              创建时间: {{ formatDate(conv.createdAt) }}
            </span>

          </div>
        </div>

        <div class="conversation-actions">
          <button
            class="view-btn"
            @click="showMessages(conv.id)"
          >
            查看消息
          </button>
          <button
            class="delete-btn"
            @click="confirmDeleteConversation(conv.id)"
          >
            删除对话
          </button>
        </div>
      </div>
    </div>

    <!-- 消息对话框 -->
    <div v-if="showMessagesDialog" class="messages-dialog">
      <div class="dialog-overlay" @click="closeMessagesDialog"></div>

      <div class="dialog-content">
        <div class="dialog-header">
          <h2>对话消息</h2>
          <button class="close-btn" @click="closeMessagesDialog">
            &times;
          </button>
        </div>

        <div v-if="messagesLoading" class="loading-messages">
          加载消息中...
        </div>

        <div v-else class="messages-list">
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="message-item"
            :class="msg.role"
          >
            <div class="message-header">
              <span class="message-role">{{ msg.role }}</span>
              <span class="message-time">
                {{ formatDate(msg.createdAt) }}
              </span>
            </div>
            <div class="message-content">
              {{ msg.content }}
            </div>

          </div>
        </div>
      </div>
    </div>

    <!-- 确认删除对话框 -->
    <div v-if="showConfirmDialog" class="confirm-dialog">
      <div class="dialog-overlay" @click="cancelDelete"></div>

      <div class="dialog-content">
        <h3>{{ confirmTitle }}</h3>
        <p>{{ confirmMessage }}</p>

        <div class="dialog-actions">
          <button class="cancel-btn" @click="cancelDelete">
            取消
          </button>
          <button class="confirm-btn" @click="confirmDelete">
            确认删除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useAdminConversationStore } from '@/stores/adminConversationStore';

const store = useAdminConversationStore();
const conversations = computed(() => store.conversations);
const messages = computed(() => store.messages);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

const showMessagesDialog = ref(false);
const messagesLoading = ref(false);
const showConfirmDialog = ref(false);
const confirmTitle = ref('');
const confirmMessage = ref('');
const itemToDelete = ref(null);
const isDeletingMessage = ref(false);

const fetchConversations = () => {
  store.fetchAllConversations();
};

const showMessages = async (convId) => {
  messagesLoading.value = true;
  showMessagesDialog.value = true;
  await store.fetchMessages(convId);
  messagesLoading.value = false;
};

const closeMessagesDialog = () => {
  showMessagesDialog.value = false;
};
// 搜索参数

const searchParams = ref({
    convId: null,
    title: '',
    userId: null
});
const handleSearch = () => {

    store.searchConversations(searchParams.value);
};

const resetSearch = () => {
    searchParams.value = {
        convId: null,
        title: '',
        userId: null
    };
    store.fetchAllConversations();
};
const confirmDeleteConversation = (convId) => {
  itemToDelete.value = convId;
  isDeletingMessage.value = false;
  confirmTitle.value = '删除对话';
  confirmMessage.value = '确定要删除这个对话及其所有消息吗？此操作不可撤销。';
  showConfirmDialog.value = true;
};

const confirmDeleteMessage = (msgId) => {
  itemToDelete.value = msgId;
  isDeletingMessage.value = true;
  confirmTitle.value = '删除消息';
  confirmMessage.value = '确定要删除这条消息吗？此操作不可撤销。';
  showConfirmDialog.value = true;
};

const cancelDelete = () => {
  showConfirmDialog.value = false;
  itemToDelete.value = null;
};

const confirmDelete = async () => {
  showConfirmDialog.value = false;

  if (isDeletingMessage.value) {
    await store.deleteMessage(itemToDelete.value);
  } else {
    await store.deleteConversation(itemToDelete.value);
  }

  itemToDelete.value = null;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString();
};

// 初始化加载对话
fetchConversations();
</script>

<style scoped>


.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

h1 {
  color: #333;
  margin: 0;
}

.refresh-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.refresh-btn:hover {
  background-color: #45a049;
}

.refresh-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #d32f2f;
  background-color: #fde8e8;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
  border-left: 4px solid #d32f2f;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
  background-color: #f9f9f9;
  border-radius: 4px;
}
/* 全屏背景设置 */
.admin-conversations {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: auto;
  padding: 20px;
  background-image: url('@/assets/images/back.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

/* 内容容器（保持1200px最大宽度但全屏背景） */
.content-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 调整各区块样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.search-bar {
  display: flex;
  gap: 10px;
  padding: 15px;
  background-color: rgba(255, 255, 255, 0.85);
  border-radius: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.conversation-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  padding: 15px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
}

/* 卡片样式增强 */
.conversation-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.conversation-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}



.conversation-header {
  background-color: #f5f5f5;
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.conv-id {
  font-weight: bold;
  margin-right: 10px;
}

.username {
  font-weight: 500;
  color: #333;
}

.user-id {
  color: #666;
  font-size: 13px;
}

.conversation-body {
  padding: 15px;
}

.conv-title {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.conv-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
}

.conversation-actions {
  display: flex;
  border-top: 1px solid #eee;
}

.view-btn, .delete-btn {
  flex: 1;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.view-btn {
  color: #2196F3;
  border-right: 1px solid #eee;
}

.view-btn:hover {
  background-color: #e3f2fd;
}

.delete-btn {
  color: #f44336;
}

.delete-btn:hover {
  background-color: #ffebee;
}

/* 消息对话框样式 */
.messages-dialog, .confirm-dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.dialog-content {
  position: relative;

  background-color: rgba(255, 255, 255, 0.95);

  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 80%;
  max-width: 800px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.dialog-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.loading-messages {
  padding: 30px;
  text-align: center;
  color: #666;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.message-item {
  margin-bottom: 15px;
  padding: 12px;
  border-radius: 6px;
  background-color: #f9f9f9;
  position: relative;
  padding-right: 60px;
}

.message-item.user {
  background-color: #e3f2fd;
}

.message-item.assistant {
  background-color: #f1f8e9;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.message-role {
  font-weight: bold;
  text-transform: capitalize;
}

.message-time {
  color: #666;
}

.message-content {
  line-height: 1.5;
  white-space: pre-wrap;
}

.delete-msg-btn {
  position: absolute;
  right: 12px;
  top: 12px;
  background-color: #ffebee;
  border: none;
  color: #f44336;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.delete-msg-btn:hover {
  background-color: #ffcdd2;
}

/* 确认对话框样式 */
.confirm-dialog .dialog-content {
  width: 400px;
  padding: 20px;
}

.confirm-dialog h3 {
  margin-top: 0;
  color: #d32f2f;
}

.confirm-dialog p {
  margin-bottom: 20px;
  color: #555;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn, .confirm-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.confirm-btn {
  background-color: #d32f2f;
  color: white;
}

.confirm-btn:hover {
  background-color: #b71c1c;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  flex: 1;
  min-width: 120px;
  max-width: 200px;
}

.search-btn {
  background-color: #2196F3;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.reset-btn {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #0b7dda;
}

.reset-btn:hover {
  background-color: #e0e0e0;
}
.admin-conversations {
  width: 100%;
  min-height: 100vh;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  /* 背景设置 */
  background-image: url('@/assets/images/back.png');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  background-repeat: no-repeat;
}

/* 内容容器调整为全宽 */
.header,
.search-bar,
.conversation-list,
.empty-state,
.error-message {
  max-width: 1200px;
  margin: 0 auto 15px auto;
  background-color: rgba(255, 255, 255, 0.85);
  padding: 15px;
  border-radius: 8px;
}

/* 对话列表调整为全宽 */
.conversation-list {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

/* 确保对话框也能全屏显示 */
.messages-dialog .dialog-content,
.confirm-dialog .dialog-content {
  width: 90%;
  max-width: 1000px;
}






</style>
