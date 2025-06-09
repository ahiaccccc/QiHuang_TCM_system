<!-- <template>
  <div class="chat-container">
    <Sidebar />
    <div class="chat-window">
      <MessageList />
      <div class="input-container">
        <input type="file" ref="fileInput" style="display: none" @change="handleFileChange" />
        <button @click="triggerFileInput" class="send-btn" style="margin-right: 10px;">
          📎 上传文件
        </button>
        <span v-if="store.selectedFile" style="font-size: 12px; margin-right: 10px;">
          {{ store.selectedFile.name }}
        </span>
        <input
          v-model="userInput"
          @keyup.enter="onSendMessage"
          placeholder="请输入内容"
          class="user-input"
        />
        <button @click="onSendMessage" class="send-btn">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useChatStore } from '@/stores/chat';
import Sidebar from './components/Sidebar.vue';
import MessageList from './components/MessageList.vue';

const store = useChatStore();
const userInput = ref('');
const fileInput = ref(null);

const triggerFileInput = () => {
  fileInput.value.click();
};

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    store.selectedFile = file;
  }
};

const onSendMessage = async () => {
  if (!userInput.value.trim() && !store.selectedFile) return;

  store.messages.push({ role: "user", content: userInput.value });
  await store.sendMessage(userInput.value, !store.currentConversation);
  userInput.value = '';
  fileInput.value.value = '';
};
</script>

<style scoped>
@import "@/assets/style/chat.css";


</style> -->
<template>
  <div class="talk">
    <div class="back">
      <div class="hero-section"></div>
      <div class="frame">
        <div class="div">
          <div class="frame-2">
            <div class="vuesax-linear"></div>
            <div class="text-wrapper">对话列表</div>
          </div>
          <div class="frame-3">
            <div v-for="(conv, index) in conversations" :key="index" class="frame-2">
              <div class="vuesax-linear-2"></div>
              <div
                class="text-wrapper-2"
                :class="{ 'text-wrapper-3': currentConversation === index }"
                @click="selectConversation(index)"
              >
                {{ conv.title || '新对话' }}
              </div>
              <div class="group" @click.stop="toggleDropdown(index)">
                <div class="trash"></div>
                <div class="vuesax-linear-edit"></div>
              </div>
              <div class="dropdown-menu" v-if="activeDropdown === index">
                <button class="dropdown-menu__item" @click="deleteConversation(index)">删除</button>
              </div>
            </div>
          </div>
        </div>
        <div class="group-wrapper" @click="newConversation">
          <div class="div-wrapper">
            <div class="text-wrapper-4">新建对话</div>
          </div>
        </div>
      </div>
      <div class="frame-8">
        <div class="frame-9">
          <div class="ellipse"></div>
          <div class="frame-10">
            <div class="text-wrapper-6">当前对话</div>
          </div>
        </div>
        <div class="xaioxizuo">
          <div class="ellipse-2"></div>
          <div class="frame-wrapper">
            <div v-for="(message, index) in messages" :key="index" class="frame-11">
              <p class="p" :class="message.role">{{ message.content }}</p>
            </div>
          </div>
        </div>
        <div class="regenerate-response">
          <div class="vector-2"></div>
          <div class="text-wrapper-7">重新生成</div>
        </div>
        <div class="type">
          <div class="frame-12">
            <input
              v-model="userInput"
              @keyup.enter="onSendMessage"
              placeholder="请输入内容"
              class="user-input"
            />
          </div>
          <div class="frame-13">
            <div class="text-wrapper-8">0/1000</div>
          </div>
          <div class="vuesax-linear-send-wrapper" @click="onSendMessage">
            <div class="vuesax-linear-send"></div>
          </div>
        </div>
      </div>
      <div class="scrollbar-frame">
        <div class="scrollbar"></div>
      </div>
      <div class="navigation">
        <div class="nav-container">
          <div class="left-column">
            <div class="logo-2">
              <div class="logo-3"></div>
              <div class="text-wrapper-9">AI助手</div>
            </div>
            <div class="nav-links">
              <div class="nav-link">
                <div class="text-wrapper-10">首页</div>
              </div>
              <div class="nav-link">
                <div class="text-wrapper-11">对话</div>
              </div>
            </div>
          </div>
          <div class="right-column">
            <div class="icon-button">
              <div class="bell"></div>
            </div>
            <div class="icon-button">
              <div class="heart-instance"></div>
            </div>
            <div class="profile">
              <div class="profile-avatar"></div>
              <div class="profile-details">
                <div class="username">用户名</div>
                <div class="email">user@example.com</div>
              </div>
              <div class="caret-down"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  setup() {
    const conversations = ref([]);
    const currentConversation = ref(null);
    const messages = ref([]);
    const userInput = ref('');
    const activeDropdown = ref(null);

    const selectConversation = (index) => {
      currentConversation.value = index;
      // 加载对应对话的消息
    };

    const toggleDropdown = (index) => {
      activeDropdown.value = activeDropdown.value === index ? null : index;
    };

    const deleteConversation = (index) => {
      conversations.value.splice(index, 1);
      if (currentConversation.value === index) {
        currentConversation.value = null;
        messages.value = [];
      }
      activeDropdown.value = null;
    };

    const newConversation = () => {
      conversations.value.push({ title: '新对话', messages: [] });
      currentConversation.value = conversations.value.length - 1;
      messages.value = [];
    };

    const onSendMessage = async () => {
      if (!userInput.value.trim()) return;

      messages.value.push({ role: "user", content: userInput.value });
      // 这里添加发送消息的逻辑
      userInput.value = '';
    };

    return {
      conversations,
      currentConversation,
      messages,
      userInput,
      activeDropdown,
      selectConversation,
      toggleDropdown,
      deleteConversation,
      newConversation,
      onSendMessage
    };
  }
};
</script>

<style scoped>
@import "@/assets/style/talk.css";

/* 添加一些额外的样式来适配Vue组件 */
.user-input {
  border: none;
  outline: none;
  width: 100%;
  background: transparent;
  color: var(--neutral-gray-600);
  font-family: var(--middle-font-family);
  font-size: var(--middle-font-size);
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: 30px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 10;
}

.dropdown-menu__item {
  padding: 8px 16px;
  width: 100%;
  text-align: left;
  border: none;
  background: none;
  cursor: pointer;
}

.dropdown-menu__item:hover {
  background: #f5f5f5;
}
</style>
