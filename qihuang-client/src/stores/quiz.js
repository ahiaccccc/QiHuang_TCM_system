import { defineStore } from 'pinia'
import {startAnswerRecord} from '@/apis/question-apis'
import { ref } from 'vue'

export const useQuizStore = defineStore('quiz', () => {
  const currentQuiz = ref(null)
  const newQuiz = ref(false)
  const currentClassName = ref(null)

  const setCurrentQuiz = (quiz,className) => {
    currentQuiz.value = quiz
    newQuiz.value = false
    currentClassName.value = className
  }
  const startAnswerRecord = (classId, playMode, limit) => {
    const res = startAnswerRecord(classId, playMode, limit)
    console.log("startAnswerRecord", res)
  }

  return {
    currentQuiz,
    newQuiz,
    currentClassName,
    setCurrentQuiz,
    startAnswerRecord
  }
}
)