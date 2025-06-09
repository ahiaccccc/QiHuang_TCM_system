import axios from 'axios'

export function getClassic(id) {
return axios.get(`/api/classics/${id}`)
.then(res => res.data)
}
export function getSessions(classicId, userId) {
return axios.get(`/api/qa/sessions`, { params: { classicId, userId } })
.then(res => res.data)
}
export function getMessages(sessionId) {
return axios.get(`/api/qa/messages`, { params: { sessionId } })
.then(res => res.data)
}
export function createSession(classicId, userId, firstQuestion) {
return axios.post(`/api/qa/sessions`, null, { params: { classicId, userId, firstQuestion } })
.then(res => res.data)
}
export function createMessage(sessionId, content, role, parentId = null) {
return axios.post(`/api/qa/messages`, { sessionId, content, role, parentId })
.then(res => res.data)
}
export function deleteSession(id) {
return axios.delete(`/api/qa/sessions/${id}`)
}
export function deleteMessage(id) {
return axios.delete(`/api/qa/messages/${id}`)
}
export function updateMessage(id, newContent) {
return axios.put(`/api/qa/messages/${id}`, { newContent })
.then(res => res.data)
}
export function regenerateMessage(messageId) {
return axios.post(`/api/qa/regenerate`, null, { params: { messageId } })
.then(res => res.data)
}
export function sendFeedback(messageId, feedback) {
return axios.post(`/api/qa/feedback`, null, { params: { messageId, feedback } })
}
// 重命名接口
export function renameSession(sessionId, newTitle) {
    return axios.put(`/api/qa/sessions/rename/${sessionId}`, { 
        newTitle 
    }).then(res => res.data);
}