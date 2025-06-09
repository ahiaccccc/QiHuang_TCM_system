import axios from 'axios'

// 获取收藏记录
export const getAdminCollections = async (params) => {
  const response = await axios.get('/api/admin/collected', {
    params: {
      userId: params.userId || undefined,
      classicId: params.classicId || undefined,
      page: params.page,
      size: params.size
    }
  });
  return response.data;
};

// 删除收藏记录
export const deleteCollection = (id) => axios.delete(`/api/admin/collected/${id}`);

// 获取对话记录
export const getAdminQaSessions = async (params) => {
  const response = await axios.get('/api/admin/qa-sessions', {
    params: {
      userId: params.userId || undefined,
      classicId: params.classicId || undefined,
      page: params.page,
      size: params.size
    }
  });
  return response.data;
};

// 删除对话记录
export const deleteQaSession = (id) => axios.delete(`/api/admin/qa-sessions/${id}`);

// 获取会话消息
export const getAdminQaMessages = (sessionId) => axios.get(`/api/admin/qa-sessions/${sessionId}/messages`);

// 删除消息
export const deleteQaMessage = (id) => axios.delete(`/api/admin/qa-sessions/messages/${id}`);