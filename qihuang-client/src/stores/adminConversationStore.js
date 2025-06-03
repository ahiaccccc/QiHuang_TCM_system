// stores/adminConversationStore.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';

export const useAdminConversationStore = defineStore('adminConversation', () => {
    const conversations = ref([]);
    console.log("信息查看",conversations)
    const messages = ref([]);
    const loading = ref(false);
    const error = ref(null);

    const fetchAllConversations = async () => {
      loading.value = true;
      error.value = null;
      try {
          const response = await axios.get('http://localhost:8080/api/admin/conversations');

          // 处理可能的数据结构变化
          conversations.value = response.data.map(item => ({
              id: item.id,
              title: item.title || '无标题',
              createdAt: item.createdAt,
              user: {
                  userId: item.userId || item.user?.userId,
                  username: item.username || item.user?.username || '未知用户'
              },
              messages: item.messages || [],
              messageCount: item.messageCount || 0
          }));

      } catch (err) {
          error.value = err.response?.data?.message ||
                       err.response?.data?.error ||
                       '获取对话列表失败';
          console.error('API错误详情:', err.response);
      } finally {
          loading.value = false;
      }
  };

  const fetchMessages = async (convId) => {
    loading.value = true;
    error.value = null;
    try {
        const response = await axios.get(`http://localhost:8080/api/admin/conversations/${convId}/messages`);

        // 确保数据结构正确
        messages.value = response.data.map(msg => ({
            id: msg.id,
            role: msg.role || 'unknown',
            content: msg.content || '',
            contentType: msg.contentType || 'text',
            lang: msg.lang || 'zh',
            createdAt: msg.createdAt,
            parentMsgId: msg.parentMsgId || msg.parentMsg?.id
        }));
        console.log('API响应数据:', response.data);

    } catch (err) {
        console.error('获取消息详情:', err.response);
        error.value = err.response?.data?.message ||
                     err.response?.data?.error ||
                     '获取消息失败';
    } finally {
        loading.value = false;
    }
};
const searchConversations = async (searchParams) => {
  console.log('开始搜索，参数:', searchParams); // 正确使用参数对象
  loading.value = true;
  error.value = null;
  try {
      console.log('正在发送请求，参数:', {
          convId: searchParams.convId,
          title: searchParams.title,
          userId: searchParams.userId
      });

      const response = await axios.get('http://localhost:8080/api/admin/conversations/search', {
          params: {
              convId: searchParams.convId||undefined,
              title: searchParams.title||undefined,
              userId: searchParams.userId||undefined
          }
      });

      console.log('API响应数据:', response.data);

      conversations.value = response.data.map(item => ({
          id: item.id,
          title: item.title || '无标题',
          createdAt: item.createdAt,
          user: {
              userId: item.user?.userId,
              username: item.user?.username || '未知用户'
          },
          messageCount: item.messages?.length || 0
      }));

  } catch (err) {
      console.error('完整错误详情:', {
          error: err,
          response: err.response,
          config: err.config
      });
      error.value = err.response?.data?.message || '搜索失败';
  } finally {
      loading.value = false;
  }
};



    const deleteConversation = async (convId) => {
        loading.value = true;
        error.value = null;
        try {
            await axios.delete(`http://localhost:8080/api/admin/conversations/${convId}`);
            conversations.value = conversations.value.filter(c => c.id !== convId);
        } catch (err) {
            error.value = err.response?.data?.message || err.message;
        } finally {
            loading.value = false;
        }
    };

    const deleteMessage = async (msgId) => {
        loading.value = true;
        error.value = null;
        try {
            await axios.delete(`http://localhost:8080/api/admin/conversations/messages/${msgId}`);
            messages.value = messages.value.filter(m => m.id !== msgId);
        } catch (err) {
            error.value = err.response?.data?.message || err.message;
        } finally {
            loading.value = false;
        }
    };

    return {
        conversations,
        messages,
        loading,
        error,
        fetchAllConversations,
        fetchMessages,
        deleteConversation,
        deleteMessage,
        searchConversations
    };
});
