import axios from "axios";

const API_BASE = "http://localhost:8080/api/chat";

export const getConversations = async (userId) => {
  return axios.get(`${API_BASE}/conversations`, { params: { userId } });
};

export const getConversation = async (convId) => {
  return axios.get(`${API_BASE}/conversations/${convId}`);
};

export const sendMessage = async (data) => {
  return axios.post(API_BASE, data, {
    headers: { "Content-Type": "multipart/form-data" }
  });
};

export const renameConversation = async (convId, title) => {
  return axios.put(`${API_BASE}/conversations/${convId}/rename`, { title });
};

export const deleteConversation = async (convId) => {
  return axios.delete(`${API_BASE}/conversations/${convId}`);
};

export const regenerateResponse = async (convId, messages) => {
  return axios.post(`${API_BASE}/regenerate/${convId}`, messages);
};
