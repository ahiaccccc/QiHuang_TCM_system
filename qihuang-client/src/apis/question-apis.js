import request from '@/utils/http'

export const findQuestionByclassId = (classId) => {
  return request({
    url: '/questionBank/findQuestionByClassId',
    method: 'POST',
    data: { data:{classId }}
  })
}

export const findRandomQuestionByclassId = (classId,limit) => {
  return request({
    url: '/questionBank/findRandomQuestionByClassId',
    method: 'POST',
    data: { data:{classId,limit }}
  })
}

export const startAnswerRecord = (classId, playMode, limit) => {
  return request({
    url: '/answerRecord/startRecord',
    method: 'POST',
    data: { data:{classId, playMode, limit }}
  })
}

export const initAnswerRecord = () => {
  return request({
    url: '/answerRecord/initRecord',
    method: 'POST'
  })
}