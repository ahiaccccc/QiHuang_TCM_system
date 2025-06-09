import request from '@/utils/http'

export const getAdminCollections = async (params) => {
  const response = await request({
    url: '/admin/collected',
    method: 'GET',
    params: {
      userId: params.userId || undefined,
      classicId: params.classicId || undefined,
      page: params.page,
      size: params.size
    }
  });
  return response;
};

export const deleteCollection = (id) => request({
  url: `/admin/collected/${id}`,
  method: 'DELETE'
});

export const getAdminQaSessions = async (params) => {
  const response = await request({
    url: '/admin/qa-sessions',
    method: 'GET',
    params: {
      userId: params.userId || undefined,
      classicId: params.classicId || undefined,
      page: params.page,
      size: params.size
    }
  });
  return response;
};

export const deleteQaSession = (id) => request({
  url: `/admin/qa-sessions/${id}`,
  method: 'DELETE'
});

export const getAdminQaMessages = async (sessionId) => {
  const response= await request({
  url: `/admin/qa-sessions/${sessionId}/messages`,
  method: 'GET'
});
//console.log("获取对话消息:", response);
  return response;
};

export const deleteQaMessage = (id) => request({
  url: `/admin/qa-sessions/messages/${id}`,
  method: 'DELETE'
});