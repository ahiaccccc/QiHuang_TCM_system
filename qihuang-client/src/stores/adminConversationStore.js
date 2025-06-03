// stores/adminConversationStore.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';

export const useAdminConversationStore = defineStore('adminConversation', () => {
    const conversations = ref([]);
    const messages = ref([]);
    const loading = ref(false);
    const error = ref(null);
    const token = ref(localStorage.getItem('token') || ''); // 添加 token 引用
    const fetchAllConversations = async () => {
      loading.value = true;
      error.value = null;
      try {
          const response = await axios.get('http://localhost:8080/api/admin/conversations/search', {
              params: {
                  convId:  undefined,
                  title:  undefined,
                  userId:  undefined
              },
              headers: {
                  'Authorization': `Bearer ${token.value}`,
                  'Content-Type': 'application/json'
              }
          });

          if (!response.data) {
              throw new Error('服务器返回空数据');
          }

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
          console.error('搜索错误:', err);
          error.value = err.response?.data?.message ||
                       err.response?.data?.error ||
                       err.message ||
                       '搜索失败';
      } finally {
          loading.value = false;
      }
  };



    const fetchMessages = async (convId) => {
        loading.value = true;
        error.value = null;
        try {
            const response = await axios.get(`http://localhost:8080/api/admin/conversations/${convId}/messages`, {
                headers: {
                    'Authorization': `Bearer ${token.value}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.data) {
                throw new Error('服务器返回空数据');
            }

            messages.value = response.data.map(msg => ({
                id: msg.id,
                role: msg.role || 'unknown',
                content: msg.content || '',
                contentType: msg.contentType || 'text',
                lang: msg.lang || 'zh',
                createdAt: msg.createdAt,
                parentMsgId: msg.parentMsgId || (msg.parentMsg && msg.parentMsg.id)
            }));

        } catch (err) {
            console.error('获取消息错误:', err);
            error.value = err.response?.data?.message ||
                         err.response?.data?.error ||
                         err.message ||
                         '获取消息失败';
        } finally {
            loading.value = false;
        }
    };

    const searchConversations = async (searchParams) => {
        loading.value = true;
        error.value = null;
        try {
            const response = await axios.get('http://localhost:8080/api/admin/conversations/search', {
                params: {
                    convId: searchParams.convId || undefined,
                    title: searchParams.title || undefined,
                    userId: searchParams.userId || undefined
                },
                headers: {
                    'Authorization': `Bearer ${token.value}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.data) {
                throw new Error('服务器返回空数据');
            }

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
            console.error('搜索错误:', err);
            error.value = err.response?.data?.message ||
                         err.response?.data?.error ||
                         err.message ||
                         '搜索失败';
        } finally {
            loading.value = false;
        }
    };

    const deleteConversation = async (convId) => {
        loading.value = true;
        error.value = null;
        try {
            await axios.delete(`http://localhost:8080/api/admin/conversations/${convId}`, {
                headers: {
                    'Authorization': `Bearer ${token.value}`,
                    'Content-Type': 'application/json'
                }
            });
            conversations.value = conversations.value.filter(c => c.id !== convId);
        } catch (err) {
            console.error('删除对话错误:', err);
            error.value = err.response?.data?.message ||
                         err.response?.data?.error ||
                         err.message ||
                         '删除对话失败';
        } finally {
            loading.value = false;
        }
    };

    const deleteMessage = async (msgId) => {
        loading.value = true;
        error.value = null;
        try {
            await axios.delete(`http://localhost:8080/api/admin/conversations/messages/${msgId}`, {
                headers: {
                    'Authorization': `Bearer ${token.value}`,
                    'Content-Type': 'application/json'
                }
            });
            messages.value = messages.value.filter(m => m.id !== msgId);
        } catch (err) {
            console.error('删除消息错误:', err);
            error.value = err.response?.data?.message ||
                         err.response?.data?.error ||
                         err.message ||
                         '删除消息失败';
        } finally {
            loading.value = false;
        }
    };

    return {
        conversations,
        messages,
        loading,
        error,
        token,
        fetchAllConversations,
        fetchMessages,
        deleteConversation,
        deleteMessage,
        searchConversations
    };
});
