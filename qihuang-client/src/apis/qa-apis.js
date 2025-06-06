import request from '@/utils/http'

export function getClassic(id) {
  return request({
    url: `/classics/${id}`,
    method: 'GET'
  }).then(res => res)
}

export function getSessions(classicId, userId) {
  return request({
    url: '/qa/sessions',
    method: 'GET',
    params: { classicId, userId }
  }).then(res => res)
}

export function getMessages(sessionId) {
  return request({
    url: '/qa/messages',
    method: 'GET',
    params:  {sessionId }
  }).then(res => res)
}

export function createSession(classicId, userId, firstQuestion) {
  return request({
    url: '/qa/sessions',
    method: 'POST',
    params: { classicId, userId, firstQuestion }
  }).then(res => res)
}

export function createMessage(sessionId, content, role, parentId = null) {
  return request({
    url: '/qa/messages',
    method: 'POST',
    data: {sessionId, content, role, parentId } 
  }).then(res => res)
}

export function deleteSession(id) {
  return request({
    url: `/qa/sessions/${id}`,
    method: 'DELETE'
  })
}

export function deleteMessage(id) {
  return request({
    url: `/qa/messages/${id}`,
    method: 'DELETE'
  })
}

export function updateMessage(id, newContent) {
  return request({
    url: `/qa/messages/${id}`,
    method: 'PUT',
    data:  newContent 
  }).then(res => res)
}

export function regenerateMessage(messageId) {
  return request({
    url: '/qa/regenerate',
    method: 'POST',
    data:  { messageId }
  }).then(res => res)
}

export function sendFeedback(messageId, feedback) {
  return request({
    url: '/qa/feedback',
    method: 'POST',
    data:  { messageId, feedback }
  })
}

export function renameSession(sessionId, newTitle) {
  return request({
    url: `/qa/sessions/rename/${sessionId}`,
    method: 'PUT',
    data: { newTitle }
  }).then(res => res)
}