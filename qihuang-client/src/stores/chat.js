import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import {
  getConversations,
  getConversation,
  sendMessage as apiSendMessage,
  renameConversation as apiRenameConversation,
  deleteConversation as apiDeleteConversation,
  regenerateResponse as apiRegenerateResponse
} from '@/apis/chat';

export const useChatStore = defineStore('chat', () => {
  const messages = ref([]);
  const conversations = ref([]);
  const currentConversation = ref(null);
  const isLoading = ref(false);
  const error = ref(null);
  const selectedFile = ref(null);

  const userId = computed(() => 100000005); // 可以改为从用户store获取

  const loadConversations = async () => {
    try {
      isLoading.value = true;
      const res = await getConversations(userId.value);
      conversations.value = res.data.filter(conv => conv.id);
    } catch (err) {
      error.value = err;
      console.error("加载会话失败", err);
    } finally {
      isLoading.value = false;
    }
  };

  const loadConversation = async (convId) => {
    try {
      isLoading.value = true;
      const conv = conversations.value.find(c => c.id === convId);
      if (!conv) return;

      currentConversation.value = conv;
      const res = await getConversation(convId);
      messages.value = res.data.map(msg => ({
        role: msg.role,
        content: msg.content,
      }));
    } catch (err) {
      error.value = err;
      console.error("加载会话消息失败", err);
    } finally {
      isLoading.value = false;
    }
  };

  const sendMessage = async (content, forceNew = false) => {
    try {
      isLoading.value = true;

      const formData = new FormData();
      formData.append("forceNew", forceNew);
      formData.append("messages", JSON.stringify(messages.value));
      formData.append("format", "markdown");

      if (selectedFile.value) {
        formData.append("file", selectedFile.value);
      }

      const res = await apiSendMessage(formData);
      messages.value.push({ role: "assistant", content: res.data });
      await loadConversations();
    } catch (err) {
      error.value = err;
      messages.value.push({
        role: "assistant",
        content: "出错了，请稍后再试。",
      });
    } finally {
      isLoading.value = false;
      selectedFile.value = null;
    }
  };

  const renameConversation = async (convId, newTitle) => {
    try {
      await apiRenameConversation(convId, newTitle);
      const index = conversations.value.findIndex(c => c.id === convId);
      if (index !== -1) {
        conversations.value[index].title = newTitle;
      }
      if (currentConversation.value?.id === convId) {
        currentConversation.value.title = newTitle;
      }
    } catch (err) {
      error.value = err;
      throw err;
    }
  };

  const deleteConversation = async (convId) => {
    try {
      await apiDeleteConversation(convId);
      conversations.value = conversations.value.filter(conv => conv.id !== convId);
      if (currentConversation.value?.id === convId) {
        currentConversation.value = null;
        messages.value = [];
      }
    } catch (err) {
      error.value = err;
      throw err;
    }
  };

  const regenerateResponse = async () => {
    try {
      if (!currentConversation.value?.id) return;

      const messagesToSend = messages.value
        .filter(msg => msg.role !== 'assistant' || msg !== messages.value[messages.value.length - 1])
        .map(msg => ({ role: msg.role, content: msg.content }));

      const res = await apiRegenerateResponse(currentConversation.value.id, messagesToSend);

      if (messages.value[messages.value.length - 1].role === 'assistant') {
        messages.value[messages.value.length - 1].content = res.data;
      } else {
        messages.value.push({ role: 'assistant', content: res.data });
      }
    } catch (err) {
      error.value = err;
      throw err;
    }
  };

  return {
    messages,
    conversations,
    currentConversation,
    isLoading,
    error,
    selectedFile,
    loadConversations,
    loadConversation,
    sendMessage,
    renameConversation,
    deleteConversation,
    regenerateResponse
  };
});
