import request from '@/utils/http'

/**
 * 获取某一类别的题目列表
 * @param {*} classId 类别id
 * @returns 
 */
export const findQuestionByclassId = (classId) => {
  return request({
    url: '/questionBank/findQuestionByClassId',
    method: 'POST',
    data: { data:{classId }}
  })
}

/**
 * 根据分类ID随机获取limit道题目
 * @param {int} classId 
 * @param {int} limit 
 * @returns 
 */
export const findRandomQuestionByclassId = (classId,limit) => {
  return request({
    url: '/questionBank/findRandomQuestionByClassId',
    method: 'POST',
    data: { data:{classId,limit }}
  })
}

/**
 * 开始答题记录
 * @param {int} classId 
 * @param {string} playMode  刷题 训练
 * @param {int} limit 
 * @returns 
 */
export const startAnswerRecord = (classId, playMode, limit) => {
  return request({
    url: '/answerRecord/startRecord',
    method: 'POST',
    data: { data:{classId, playMode, limit }}
  })
}

/**
 * 初始化答题界面时调用的接口
 * @returns 
 */
export const initAnswerRecord = () => {
  return request({
    url: '/answerRecord/initRecord',
    method: 'POST'
  })
}

/**
 * 获取所有题目分类
 * @returns {Promise} 包含题目分类列表的Promise
 */
export const findAllQuestionClasses = () => {
  return request({
    url: '/questionBank/findAllQuestionClasses',
    method: 'POST'
  })
}


export const finishAnswerRecord = (answerRecordId, answers) => {
  return request({
    url: '/answerRecord/finishRecord',
    method: 'POST',
    data: { data:{answerRecordId, answers}}
  })
}