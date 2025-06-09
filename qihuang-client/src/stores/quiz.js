import { defineStore } from 'pinia'
import {startAnswerRecord, finishAnswerRecord} from '@/apis/question-apis'
import { ref } from 'vue'

export const useQuizStore = defineStore('quiz', () => {
  const currentQuiz = ref(null)
  // const newQuiz = ref(false)
  const currentClassName = ref(null)
  const isReviewMode = ref(false)
  const reviewData = ref(null)
  const accuracy = ref(0)

  const setCurrentQuiz = (quiz, className) => {
    currentQuiz.value = quiz
    // newQuiz.value = false
    currentClassName.value = className
    isReviewMode.value = false
    reviewData.value = null
  }

  const setReviewMode = (data,acc) => {
    isReviewMode.value = true
    reviewData.value = data
    // 计算正确率
    if (data && data.correct) {
      let correctCount = 0
      for (let i = 0; i < data.correct.length; i++) {
        if (data.correct[i] === '1') {
          correctCount++
        }
      }
      accuracy.value = acc
    }
  }

  const leaveQuiz = () => {
    currentQuiz.value = null
    // newQuiz.value = false
    currentClassName.value = null
    isReviewMode.value = false
    reviewData.value = null
  }

  const startAnswerRecordStore =async (classId, playMode, limit,className) => {
    const res = await startAnswerRecord(classId, playMode, limit)
    console.log("startAnswerRecord", res)
    currentClassName.value = className
    if (res ) {
      currentQuiz.value = res

      isReviewMode.value = false
      reviewData.value = null
    } else {
      console.error("Failed to start answer record:", res)
    }

    return res
  }

  return {
    currentQuiz,
    // newQuiz,
    currentClassName,
    isReviewMode,
    reviewData,
    accuracy,
    setCurrentQuiz,
    setReviewMode,
    startAnswerRecord,
    leaveQuiz,
    startAnswerRecordStore
  }
})