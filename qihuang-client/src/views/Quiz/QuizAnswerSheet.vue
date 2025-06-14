<template>

  <div class="xuanzhuti">
    <Navi
      :avatar="getAvatarUrl(profile.avatar) || defaultAvatar"
      :nickname="profile.username"
    />
    <img class="back" />
    <div class="hero-section">
      <div class="main-area">
        <div class="frame"></div>
        <div class="div">
          <div class="div-wrapper">
            <p class="p"><span class="text-wrapper">| </span> <span class="span">{{ quizClassName }}</span>
              <span
                v-if="quizStore.currentQuiz && quizStore.currentQuiz.playMode === 'PRACTICE'"
                style="color: black;"
              >（训练模式）</span>
              <span
                v-else-if="quizStore.currentQuiz && quizStore.currentQuiz.playMode === 'RANK'"
                style="color: black;"
              >（正式模式）</span>
            </p>
          </div>
          <div class="frame-2">
            <div class="overlap">
              <div class="frame-3">
                <div class="frame-4">
                  <div class="text-wrapper-2">{{currentIndex+1}} . &nbsp;{{ currentQuestion.question }}</div>
                  <div class="text-wrapper-3">
                    <div
                      v-for="(option, index) in options"
                      :key="index"
                      class="option-item"
                      @click="selectOption(index)"
                      :class="{ 'incorrect-option': isReviewMode && !isAnswerCorrect(currentIndex) && answers[currentIndex] === String.fromCharCode('A'.charCodeAt(0) + index),
                                  'correct-option': correctOption(index),
                                  
                        'selected': (answers[currentIndex].charCodeAt(0) - 'A'.charCodeAt(0) === index) && !isReviewMode}"
                    >
                      <!-- <input
                        type="radio"
                        :name="'question-' + currentIndex"
                        :value="index"
                        :checked="answers[currentIndex].charCodeAt(0) - 'A'.charCodeAt(0) === index"
                        @change="selectOption(index)"
                      /> -->
                      <span v-if="answers[currentIndex].charCodeAt(0) - 'A'.charCodeAt(0) === index">●</span>
                      <span v-else>○</span>
                      <span class="option-label">&nbsp;&nbsp;
                        {{ String.fromCharCode('A'.charCodeAt(0) + index) }}.
                        &nbsp; {{ option }}
                      </span>
                      <br>

                      <!-- &nbsp;&nbsp;&nbsp;&nbsp; ○ {{ option }}  -->
                    </div>
                  </div>
                </div>
                <div class="overlap-group">
                  <div class="frame-5">
                    <div
                      class="button-blue"
                      @click="prevQuestion"
                    >
                      <div class="text-wrapper-4">上一题</div>
                    </div>
                    <div class="text-wrapper-5">{{ currentIndex + 1 }}/{{ questions.length }}</div>
                    <div
                      class="button-blue"
                      @click="nextQuestion"
                    >
                      <div class="text-wrapper-4">下一题</div>
                    </div>
                  </div>

                </div>
              </div>
              <div class="frame-6">
                <div class="text-wrapper-6">单选题</div>
              </div>
            </div>

            <div
              v-if="isReviewMode"
              class="review-mode"
            >
              <!-- 修改答题卡部分 -->
              <div class="frame-7">
                <div class="text-wrapper-8">答题结果</div>
                <div class="frame-8">

                  <div class="frame-9">
                    <svg
                      class="status-icon"
                      viewBox="0 0 24 24"
                    >
                      <rect
                        x="2"
                        y="2"
                        width="20"
                        height="20"
                        rx="2"
                        fill="rgba(92, 176, 106, 0.3)"
                        stroke="#35565a"
                        stroke-width="1"
                      />
                    </svg>
                    <div class="text-wrapper-10">正确</div>
                  </div>
                  <div class="frame-9">
                    <svg
                      class="status-icon"
                      viewBox="0 0 24 24"
                    >
                      <rect
                        x="2"
                        y="2"
                        width="20"
                        height="20"
                        rx="2"
                        fill="rgba(255, 59, 48, 0.3)"
                        stroke="#ff3b30"
                        stroke-width="1"
                      />
                    </svg>
                    <div class="text-wrapper-10">错误</div>
                  </div>
                </div>
                <div class="frame-10">
                  <div
                    v-for="(question, index) in questions"
                    :key="index"
                    class="frame-11"
                    :class="{
            'current': currentIndex === index,
            'correct': isAnswerCorrect(index),
            'incorrect': !isAnswerCorrect(index) && answers[index] !== '*'
          }"
                    @click="goToQuestion(index)"
                  >
                    <div class="text-wrapper-11">{{ index + 1 }}</div>
                  </div>
                </div>
              </div>

              <!-- 修改提交按钮为返回按钮 -->
              <div
                class="button-blue-submit"
                @click="goBack"
              >
                <div class="text-wrapper-4">返回</div>
              </div>

              <!-- 修改倒计时为正确率 -->
              <div class="element-wrapper">
                <p class="element">
                  <span class="span">正确率：</span>
                  <span class="text-wrapper-7">{{ accuracy }}%</span>
                </p>
              </div>
            </div>
            <div v-else>
              <div class="element-wrapper">
                <p class="element"><span class="span"> 倒计时：</span> <span class="text-wrapper-7">{{ formattedTime }}</span></p>
              </div>
              <div class="frame-7">
                <div class="text-wrapper-8">答题卡</div>
                <div class="frame-8">
                  <div class="frame-9">
                    <svg
                      class="status-icon"
                      viewBox="0 0 24 24"
                    >
                      <rect
                        x="2"
                        y="2"
                        width="20"
                        height="20"
                        rx="2"
                        fill="#ffffff"
                        stroke="#000000"
                        stroke-width="1"
                      />
                    </svg>
                    <div class="text-wrapper-9">未作答</div>
                  </div>
                  <div class="frame-9">
                    <svg
                      class="status-icon"
                      viewBox="0 0 24 24"
                    >
                      <rect
                        x="2"
                        y="2"
                        width="20"
                        height="20"
                        rx="2"
                        fill="#cecaca"
                        stroke="#35565a"
                        stroke-width="1"
                      />
                    </svg>
                    <div class="text-wrapper-10">已作答</div>
                  </div>
                </div>
                <div class="frame-10">
                  <div
                    v-for="(question, index) in questions"
                    :key="index"
                    class="frame-11"
                    :class="{
                    'current': currentIndex === index,
                    'answered': answers[index] !== '*',
                    'unanswered':  answers[index] === '*'
                  }"
                    @mouseover="hoverIndex = index"
                    @mouseleave="hoverIndex = -1"
                    @click="goToQuestion(index)"
                  >
                    <div class="text-wrapper-11">{{ index + 1 }}</div>
                  </div>
                </div>
              </div>

              <!-- 修改提交按钮部分 -->
              <n-popconfirm
                v-if="!isReviewMode"
                :show="showSubmitConfirm"
                @update:show="(value) => showSubmitConfirm = value"
                @positive-click="submitAnswers"
                negative-text="取消"
                positive-text="确认提交"
              >
                <template #trigger>
                  <div
                    class="button-blue-submit"
                    @click="showSubmitConfirm = true"
                  >
                    <div class="text-wrapper-4">提交</div>
                  </div>
                </template>
                <span>确认要提交答案吗？提交后将无法修改。</span>
              </n-popconfirm>

            </div>

          </div>
        </div>
      </div>
    </div>
    <div class="navigation">
      <div class="nav-container">
        <div class="left-column">
          <div class="logo">

            <div class="text-wrapper-12">岐黄慧问</div>
          </div>
          <div class="nav-links">
            <div class="nav-link">
              <div class="text-wrapper-13">AI问答</div>
            </div>
            <div class="nav-link-2">
              <div class="text-wrapper-14">典籍解读</div>
            </div>
            <div class="nav-link-2">
              <div class="text-wrapper-14">养生方案</div>
            </div>
            <div class="nav-link-2">
              <div class="text-wrapper-15">答题挑战</div>
            </div>
          </div>
        </div>
        <div class="right-column">
          <div class="icon-button"></div>
          <div class="icon-button">

          </div>
          <div class="profile">
            <div class="profile-avatar"></div>
            <div class="profile-details">
              <div class="username">小奇</div>
              <input
                class="email"
                placeholder="qixiaoxiao@136.com"
                type="email"
              />
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { useQuizStore } from '@/stores/quiz'
import { finishAnswerRecord } from '@/apis/question-apis'
import { NPopconfirm, useLoadingBar, useDialog } from 'naive-ui'

const route = useRouter()
const quizStore = useQuizStore()
const loadingBar = useLoadingBar()
const dialog = useDialog()

const quizClassName = ref('')
const questions = ref([])
const answers = ref([])
const currentIndex = ref(0)
const selectedOption = ref(-1)
const hoverIndex = ref(-1)
// const timeLeft = ref(1800)
// 新增时间差计算
const elapsedTime = ref(0) // 已用秒数
const isReviewMode = ref(false)
const accuracy = ref(0)
const correctCount = ref(0)
const showSubmitConfirm = ref(false)

//-----时间-----//
// 设置总考试时间（秒），例如30分钟=1800秒
const TOTAL_EXAM_TIME = 1800
const timeLeft = ref(TOTAL_EXAM_TIME) // 剩余时间
let timer = null
// 计算考试结束时间
const getExamEndTime = () => {
  if (!quizStore.currentQuiz?.time) return null
  const startTime = new Date(quizStore.currentQuiz.time)
  return new Date(startTime.getTime() + TOTAL_EXAM_TIME * 1000)
}

// 更新剩余时间
const updateTimeLeft = () => {
  const endTime = getExamEndTime()
  if (!endTime) return

  const now = new Date()
  const diff = Math.floor((endTime - now) / 1000) // 剩余秒数

  timeLeft.value = diff > 0 ? diff : 0

  // 检查是否时间到
  if (timeLeft.value <= 0) {
    clearInterval(timer)
    autoSubmitExam()
  }
}
// 自动提交考试
const autoSubmitExam = async () => {
  try {
    const answerString = answers.value.join('')
    const res = await finishAnswerRecord(quizStore.currentQuiz.id, answerString)

    // 计算正确率
    let correctCount = 0
    for (let i = 0; i < answers.value.length; i++) {
      if (res.data.correct[i] === '1') correctCount++
    }
    const accuracy = Math.round((correctCount / answers.value.length) * 100)

    // 弹出提示
    alert(`考试时间结束！\n正确率: ${accuracy}%`)

    // 进入复查模式
    isReviewMode.value = true
    quizStore.setReviewMode(res.data, accuracy)
  } catch (error) {
    console.error('自动提交失败:', error)
    alert('自动提交失败，请手动提交答案')
  }
}

// 格式化时间为MM:SS
const formattedTime = computed(() => {
  const minutes = Math.floor(timeLeft.value / 60)
  const seconds = timeLeft.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 修改submitAnswers函数
const submitAnswers = async () => {
  try {
    loadingBar.start()

    const answerString = answers.value.join('')
    console.log('提交答案:', answerString)

    const res = await finishAnswerRecord(quizStore.currentQuiz.id, answerString)
    console.log('答案提交成功', res.data)

    quizStore.setCurrentQuiz({
      ...quizStore.currentQuiz,
      answers: answerString,
    })

    // 计算正确率和正确数量
    correctCount.value = 0
    for (let i = 0; i < answers.value.length; i++) {
      if (res.data.correct[i] === '1') {
        correctCount.value++
      }
    }
    accuracy.value = Math.round((correctCount.value / answers.value.length) * 100)

    loadingBar.finish()

    // 显示完成对话框
    dialog.success({
      title: '答题完成',
      content: `恭喜您完成作答！正确率: ${accuracy.value}%`,
      positiveText: '确定',
      onPositiveClick: () => {
        isReviewMode.value = true
        quizStore.setReviewMode(res.data, accuracy.value)
      },
    })
  } catch (error) {
    loadingBar.error()
    console.error('提交答案失败:', error)
    dialog.error({
      title: '提交失败',
      content: '提交答案失败，请稍后再试。',
      positiveText: '确定',
    })
  }
}

// 添加判断答案是否正确的方法
const isAnswerCorrect = (index) => {
  if (!quizStore.reviewData) return false
  return quizStore.reviewData.correct[index] === '1'
}

// 添加返回函数
const goBack = () => {
  route.push({ name: 'quiz-select' })
}

// 修改选项样式逻辑
const getOptionClass = (index) => {
  if (!isReviewMode.value) return {}

  const currentAnswer = answers.value[currentIndex.value]
  const correctAnswer = quizStore.reviewData.correct[currentIndex.value]

  // 如果是正确答案
  if (String.fromCharCode('A'.charCodeAt(0) + index) === correctAnswer) {
    return { 'correct-option': true }
  }

  // 如果是用户选择的错误答案
  if (currentAnswer === String.fromCharCode('A'.charCodeAt(0) + index) && correctAnswer !== '1') {
    return { 'incorrect-option': true }
  }

  return {}
}

const correctOption = (index) => {
  if (!isReviewMode.value) return false

  const correctAnswer = quizStore.reviewData.correct[currentIndex.value]
  if (correctAnswer === '1')
    return String.fromCharCode('A'.charCodeAt(0) + index) === answers.value[currentIndex.value]
  return String.fromCharCode('A'.charCodeAt(0) + index) === correctAnswer
}

// ——————————————————————————————————

// 计算格式化时间
// const formattedTime = computed(() => {
//   const minutes = Math.floor(timeLeft.value / 60)
//   const seconds = timeLeft.value % 60
//   return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
// })

// 当前问题
const currentQuestion = computed(() => {
  // console.log('currentquestion:', questions.value[currentIndex.value])
  return questions.value[currentIndex.value] || {}
})

// 当前问题的选项
const options = computed(() => {
  if (!currentQuestion.value.options) return []
  return currentQuestion.value.options.split('\\n')
})

//  添加页面卸载确认功能
const beforeUnloadHandler = (e) => {
  const hasUnansweredQuestions = true

  if (hasUnansweredQuestions) {
    const confirmationMessage = '您有未完成的答题进度，离开页面将丢失当前进度。确定要离开吗？'
    e.preventDefault()
    e.returnValue = confirmationMessage
    return confirmationMessage
  }
}

// 初始化数据
onMounted(async () => {
  //判断currentQuiz是否存在
  if (!quizStore.currentQuiz) {
    console.error('当前测验不存在或未加载')

    route.push({ name: 'quiz-select' }) // 跳转到测验列表页面
    return
  }

  quizClassName.value = quizStore.currentClassName
  console.log('quizClassName:', quizStore.currentClassName)
  console.log('currentQuiz:', quizStore.currentQuiz)
  questions.value = quizStore.currentQuiz.questions || []
  answers.value = quizStore.currentQuiz.answers.split('')
  console.log('answers:', answers.value)

  // 找到第一个未回答的题目
  const firstUnansweredIndex = answers.value.findIndex((answer) => answer === '*')
  currentIndex.value =
    firstUnansweredIndex !== -1 ? firstUnansweredIndex : questions.value.length - 1

  // 设置当前已选择的选项
  if (answers.value[currentIndex.value] !== '*') {
    selectedOption.value = answers.value[currentIndex.value].charCodeAt(0) - 'A'.charCodeAt(0)
  } else {
    selectedOption.value = -1
  }

  // 替换原有的计时器
  updateTimeLeft() // 立即更新一次
  timer = setInterval(updateTimeLeft, 1000) // 每秒更新

  window.addEventListener('beforeunload', beforeUnloadHandler)
  await loadProfile()
})
// 组件卸载前移除事件监听器
onBeforeUnmount(() => {
  quizStore.leaveQuiz() // 重置测验状态
  window.removeEventListener('beforeunload', beforeUnloadHandler)
  clearInterval(timer)
})

// 选择选项
const selectOption = (index) => {
  if (isReviewMode.value) return // 在复查模式下不允许选择选项
  selectedOption.value = index
  answers.value[currentIndex.value] = String.fromCharCode('A'.charCodeAt(0) + index)
}

// 下一题
const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    // 设置当前已选择的选项
    if (answers.value[currentIndex.value] !== 'A' && answers.value[currentIndex.value] !== '*') {
      selectedOption.value = answers.value[currentIndex.value].charCodeAt(0) - 'A'.charCodeAt(0)
    } else {
      selectedOption.value = -1
    }
  }
}

// 上一题
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
    // 设置当前已选择的选项
    if (answers.value[currentIndex.value] !== 'A' && answers.value[currentIndex.value] !== '*') {
      selectedOption.value = answers.value[currentIndex.value].charCodeAt(0) - 'A'.charCodeAt(0)
    } else {
      selectedOption.value = -1
    }
  }
}

// 跳转到指定题目
const goToQuestion = (index) => {
  currentIndex.value = index
  // 设置当前已选择的选项
  if (answers.value[currentIndex.value] !== 'A' && answers.value[currentIndex.value] !== '*') {
    selectedOption.value = answers.value[currentIndex.value].charCodeAt(0) - 'A'.charCodeAt(0)
  } else {
    selectedOption.value = -1
  }
}

// ---------------- 用户信息 ----------------
import Navi from '../components/NaviHomeView.vue'
import { getProfileAPI } from '@/apis/user'
import defaultAvatar from '../../assets/images/defaultAvatar.png'
const getAvatarUrl = (avatar) => {
  return avatar ? `http://localhost:8080${avatar}` : null
}
const profile = ref({
  username: '',
  userId: '',
  email: '',
  avatar: '',
})
const loadProfile = async () => {
  try {
    const response = await getProfileAPI()
    if (response.code === 200) {
      profile.value = response.data
      console.log('个人信息加载成功:', profile.value)
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}
</script>

<style>
/* --------------复查样式------------- */

.review-mode .option-item {
  cursor: default !important;
}

.correct-option {
  background-color: rgba(92, 176, 106, 0.3) !important;
  color: #35565a;
  font-weight: bold;
}

.incorrect-option {
  background-color: rgba(255, 59, 48, 0.3) !important;
  color: #ff3b30;
  font-weight: bold;
}

.frame-11.correct {
  background-color: rgba(92, 176, 106, 0.3) !important;
  border-color: #35565a !important;
}

.frame-11.incorrect {
  background-color: rgba(255, 59, 48, 0.3) !important;
  border-color: #ff3b30 !important;
}
/* ------------------------ */

.option-item {
  cursor: pointer;
  margin-bottom: 10px;
  transition: background-color 0.2s;
}

.option-item:hover {
  background-color: rgba(92, 176, 106, 0.1);
}

.option-item.selected {
  background-color: rgba(92, 176, 106, 0.2);
}

.frame-11 {
  cursor: pointer;
  transition: all 0.2s;
}

.frame-11.current {
  border: 2px solid #35565a !important;
  background-color: rgba(92, 176, 106, 0.3);
}

.frame-11.answered {
  background-color: #cecaca;
}

.frame-11.unanswered:hover {
  background-color: rgba(92, 176, 106, 0.2);
}
:root {
  --shenlan: rgba(53, 86, 90, 1);
  --zhonglv: rgba(92, 176, 106, 1);
  --danmi: rgba(255, 254, 248, 1);
  --black-1: rgba(68, 68, 68, 1);
  --grayswhite: rgba(255, 255, 255, 1);
  --transparent: rgba(255, 255, 255, 0.6);
  --middle-font-family: 'DengXian', Helvetica;
  --middle-font-weight: 400;
  --middle-font-size: 16px;
  --middle-letter-spacing: 0px;
  --middle-line-height: normal;
  --middle-font-style: normal;
  --large-font-family: 'DengXian', Helvetica;
  --large-font-weight: 400;
  --large-font-size: 18px;
  --large-letter-spacing: 0px;
  --large-line-height: 35px;
  --large-font-style: normal;
  --sularge-bold-font-family: 'DengXian', Helvetica;
  --sularge-bold-font-weight: 700;
  --sularge-bold-font-size: 20px;
  --sularge-bold-letter-spacing: 0px;
  --sularge-bold-line-height: normal;
  --sularge-bold-font-style: normal;
  --small-bold-font-family: 'DengXian', Helvetica;
  --small-bold-font-weight: 700;
  --small-bold-font-size: 14px;
  --small-bold-letter-spacing: 0px;
  --small-bold-line-height: normal;
  --small-bold-font-style: normal;
  --sususmall-font-family: 'DengXian', Helvetica;
  --sususmall-font-weight: 400;
  --sususmall-font-size: 10px;
  --sususmall-letter-spacing: 0px;
  --sususmall-line-height: normal;
  --sususmall-font-style: normal;
}
</style>


<style lang="scss" scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css');
* {
  -webkit-font-smoothing: antialiased;
  box-sizing: border-box;
}
html,
body {
  margin: 0px;
  height: 100%;
}
/* a blue color as a generic focus style */
button:focus-visible {
  outline: 2px solid #4a90e2 !important;
  outline: -webkit-focus-ring-color auto 5px !important;
}
a {
  text-decoration: none;
}

/* ========================================================== */

.xuanzhuti {
  display: flex;
  flex-direction: column;
  height: 1142px;
  align-items: flex-start;
  position: relative;
  background-color: var(--danmi);
}

.xuanzhuti .back {
  position: relative;
  width: 100%;
  min-width: 1000px;
  height: 100vh;
}

.xuanzhuti .hero-section {
  position: absolute;
  /* width: 1537px; */
  width: 100%;
  min-width: 1000px;
  height: 100vh;
  /* height: 998px; */
  top: 53px;
  left: 0;
}

.xuanzhuti .main-area {
  position: relative;
  /* width: 1544px; */
  width: 100%;
  min-width: 1000px;
  height: 962px;
  left: -3px;
  border-radius: 10px;
  overflow: hidden;
  overflow-y: scroll;
}

.xuanzhuti .main-area::-webkit-scrollbar {
  width: 0;
  display: none;
}

.xuanzhuti .frame {
  position: absolute;
  /* width: 1544px; */
  width: 100%;
  min-width: 1000px;
  height: 223px;
  top: 0;
  left: 0;
  background-color: #ffffff33;
}

.xuanzhuti .div {
  display: flex;
  flex-direction: column;
  /* width: 1544px; */
  width: 100%;
  min-width: 1000px;
  height: 771px;
  align-items: center;
  gap: 34px;
  padding: 40px 40px 0px;
  position: absolute;
  top: 148px;
  left: 0;
  background-color: var(--danmi);
}

.xuanzhuti .div-wrapper {
  position: relative;
  width: 85%;
  height: 46px;
}

.xuanzhuti .p {
  position: absolute;
  width: 190px;
  top: 3px;
  left: 10px;
  font-family: 'DengXian-Bold', Helvetica;
  font-weight: 400;
  color: transparent;
  font-size: 26px;
  letter-spacing: 0;
  line-height: 26px;
  white-space: nowrap;
}

.xuanzhuti .text-wrapper {
  font-weight: 700;
  color: #35565a;
}

.xuanzhuti .span {
  font-weight: 700;
  color: #000000;
}

.xuanzhuti .frame-2 {
  position: relative;
  /* width: 1344px; */
  width: 87%;
  min-width: 870px;
  height: 655px;
  margin-bottom: -4px;
}

.xuanzhuti .overlap {
  position: absolute;
  width: 56%;
  height: 550px;
  top: 0;
  left: 14px;
}

.xuanzhuti .frame-3 {
  position: absolute;
  width: 100%;
  height: 550px;
  top: 0;
  left: 0;
  border: 1px solid;
  border-color: #35565a;
}

.xuanzhuti .frame-4 {
  position: absolute;
  /* width: 724px; */
  width: 96%;
  height: 195px;
  top: 95px;
  left: 2%;
}

.xuanzhuti .text-wrapper-2 {
  // position: absolute;
  width: 100%;
  // top: 1px;
  // left: 2px;
  font-family: var(--large-font-family);
  font-weight: var(--large-font-weight);
  color: #000000;
  font-size: var(--large-font-size);
  letter-spacing: var(--large-letter-spacing);
  line-height: var(--large-line-height);
  font-style: var(--large-font-style);
}

.xuanzhuti .text-wrapper-3 {
  // position: absolute;
  width: 100%;
  // top: 39px;
  // left: 11px;
  font-family: var(--large-font-family);
  font-weight: var(--large-font-weight);
  color: #000000;
  font-size: var(--large-font-size);
  letter-spacing: var(--large-letter-spacing);
  line-height: var(--large-line-height);
  font-style: var(--large-font-style);
}

.xuanzhuti .overlap-group {
  position: absolute;
  /* width: 765px; */
  width: 100%;
  /* min-width: 870px; */
  height: 96px;
  top: 453px;
  left: 0;
}

.xuanzhuti .frame-5 {
  display: flex;
  /* width: 481px; */
  width: 100%;
  height: 96px;
  align-items: center;
  justify-content: space-between;
  padding: 54px 65px;
  /* position: absolute;
  top: 0;
  left: 142px; */
}

.xuanzhuti .button-blue {
  display: flex;
  width: 112px;
  height: 35px;
  align-items: center;
  justify-content: center;
  padding: 10.76px;
  position: relative;
  margin-top: -23.5px;
  margin-bottom: -23.5px;
  background-color: var(--zhonglv);
  border-radius: 8px;
  &:hover {
    background-color: #7abf8b;
    cursor: pointer;
  }
}

.button-blue-submit {
  display: flex;
  width: 112px;
  height: 35px;
  align-items: center;
  justify-content: center;
  padding: 10.76px;
  position: absolute;
  left: 80%;
  top: 513px;
  margin-top: -23.5px;
  margin-bottom: -23.5px;
  background-color: var(--zhonglv);
  border-radius: 8px;
  margin-left: auto;
  margin-right: auto;
  &:hover {
    background-color: #7abf8b;
    cursor: pointer;
  }
}

.xuanzhuti .text-wrapper-4 {
  position: relative;
  width: fit-content;
  margin-top: -2.44px;
  margin-bottom: -1.09px;
  font-family: var(--middle-font-family);
  font-weight: var(--middle-font-weight);
  color: #ffffffe6;
  font-size: var(--middle-font-size);
  text-align: center;
  letter-spacing: var(--middle-letter-spacing);
  line-height: var(--middle-line-height);
  white-space: nowrap;
  font-style: var(--middle-font-style);
}

.xuanzhuti .text-wrapper-5 {
  position: relative;
  width: 51px;
  height: 31px;
  margin-top: -22.5px;
  margin-bottom: -20.5px;
  font-family: var(--middle-font-family);
  font-weight: var(--middle-font-weight);
  color: #000000;
  font-size: var(--middle-font-size);
  text-align: center;
  letter-spacing: var(--middle-letter-spacing);
  line-height: var(--middle-line-height);
  font-style: var(--middle-font-style);
}

.xuanzhuti .line {
  position: absolute;
  /* width: 765px; */
  width: 100%;
  height: 1px;
  top: 0;
  left: 0;
  object-fit: cover;
}

.xuanzhuti .frame-6 {
  position: absolute;
  width: 100%;
  height: 39px;
  top: 0;
  left: 1px;
  overflow: hidden;
  border-bottom-width: 1px;
  border-bottom-style: solid;
  border-color: #35565a;
}

.xuanzhuti .text-wrapper-6 {
  position: absolute;
  width: 215px;
  top: 8px;
  left: 26px;
  font-family: var(--sularge-bold-font-family);
  font-weight: var(--sularge-bold-font-weight);
  color: #000000;
  font-size: var(--sularge-bold-font-size);
  letter-spacing: var(--sularge-bold-letter-spacing);
  line-height: var(--sularge-bold-line-height);
  font-style: var(--sularge-bold-font-style);
}

.xuanzhuti .element-wrapper {
  position: absolute;
  /* width: 494px; */
  width: 40%;
  height: 39px;
  top: 0;
  left: 850px;
  left: 65%;
  border: 1px solid;
  border-color: #35565a;
}

.xuanzhuti .element {
  position: absolute;
  width: 261px;
  top: 8px;
  /* left: 108px; */
  left: 20%;
  font-family: 'DengXian-Bold', Helvetica;
  font-weight: 400;
  color: transparent;
  font-size: 20px;
  text-align: center;
  letter-spacing: 0;
  line-height: 20px;
  white-space: nowrap;
}

.xuanzhuti .text-wrapper-7 {
  font-weight: var(--sularge-bold-font-weight);
  color: #ff3b30;
  font-family: var(--sularge-bold-font-family);
  font-style: var(--sularge-bold-font-style);
  letter-spacing: var(--sularge-bold-letter-spacing);
  line-height: var(--sularge-bold-line-height);
  font-size: var(--sularge-bold-font-size);
}

.xuanzhuti .frame-7 {
  display: flex;
  flex-direction: column;
  /* width: 492px; */
  width: 40%;
  height: 492px;
  align-items: center;
  gap: 20px;
  padding: 24px 25px 0px;
  position: absolute;
  top: 57px;
  /* left: 852px; */
  left: 65%;
  border: 1px solid;
  border-color: #35565a;
}

.xuanzhuti .text-wrapper-8 {
  position: relative;
  width: 149px;
  height: 37px;
  margin-top: -1px;
  font-family: var(--sularge-bold-font-family);
  font-weight: var(--sularge-bold-font-weight);
  color: #000000;
  font-size: var(--sularge-bold-font-size);
  text-align: center;
  letter-spacing: var(--sularge-bold-letter-spacing);
  line-height: var(--sularge-bold-line-height);
  font-style: var(--sularge-bold-font-style);
}

.xuanzhuti .frame-8 {
  display: flex;
  width: 432px;
  align-items: flex-start;
  justify-content: center;
  padding: 0px 40px;
  position: relative;
  flex: 0 0 auto;
  .status-icon {
    width: 22px;
    height: 22px;
    margin-right: 8px;
    position: absolute;
    top: 10px;
    left: 50px;
  }
}

.xuanzhuti .frame-9 {
  position: relative;
  width: 173px;
  height: 46px;
}

.xuanzhuti .rectangle {
  position: absolute;
  width: 22px;
  height: 22px;
  top: 11px;
  left: 28px;
}

.xuanzhuti .text-wrapper-9 {
  position: absolute;
  width: 104px;
  top: 10px;
  left: 51px;
  font-family: var(--sularge-bold-font-family);
  font-weight: var(--sularge-bold-font-weight);
  color: #000000;
  font-size: var(--sularge-bold-font-size);
  text-align: center;
  letter-spacing: var(--sularge-bold-letter-spacing);
  line-height: var(--sularge-bold-line-height);
  white-space: nowrap;
  font-style: var(--sularge-bold-font-style);
}

.xuanzhuti .text-wrapper-10 {
  position: absolute;
  width: 104px;
  top: 12px;
  left: 53px;
  font-family: var(--sularge-bold-font-family);
  font-weight: var(--sularge-bold-font-weight);
  color: #000000;
  font-size: var(--sularge-bold-font-size);
  text-align: center;
  letter-spacing: var(--sularge-bold-letter-spacing);
  line-height: var(--sularge-bold-line-height);
  white-space: nowrap;
  font-style: var(--sularge-bold-font-style);
}

.xuanzhuti .img {
  position: absolute;
  width: 22px;
  height: 22px;
  top: 13px;
  left: 11px;
}

.xuanzhuti .frame-10 {
  display: flex;
  flex-wrap: wrap;
  /* width: 426px; */
  width: 100%;

  align-items: flex-start;
  gap: 10px 10px;
  padding: 10px;
  position: relative;
  flex: 0 0 auto;
}

.xuanzhuti .frame-11 {
  position: relative;
  width: 35px;
  height: 35px;
  border: 1px solid;
  border-color: #000000;
}

.xuanzhuti .text-wrapper-11 {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  // left: -1px;
  font-family: var(--large-font-family);
  font-weight: var(--large-font-weight);
  color: #000000;
  font-size: var(--large-font-size);
  text-align: center;
  letter-spacing: var(--large-letter-spacing);
  line-height: var(--large-line-height);
  white-space: nowrap;
  font-style: var(--large-font-style);
  &:hover {
    background-color: rgba(92, 176, 106, 0.2);
  }
}

.xuanzhuti .navigation {
  display: flex;
  /* width: 1537px; */
  width: 100%;
  min-width: 1000px;

  align-items: center;
  justify-content: center;
  padding: 10.76px 0px;
  position: absolute;
  top: 0;
  left: 0;
  background-color: var(--transparent);
  backdrop-filter: blur(26.37px) brightness(100%);
  -webkit-backdrop-filter: blur(26.37px) brightness(100%);
}

.xuanzhuti .nav-container {
  display: flex;
  width: 900px;
  align-items: center;
  justify-content: space-between;
  padding: 0px 16.15px;
  position: relative;
}

.xuanzhuti .left-column {
  display: flex;
  align-items: center;
  gap: 32.29px;
  position: relative;
  flex: 1;
  flex-grow: 1;
}

.xuanzhuti .logo {
  display: flex;
  width: 114.64px;
  height: 26px;
  align-items: center;
  gap: 5.38px;
  position: relative;
}

.xuanzhuti .logo-2 {
  position: relative;
  width: 27px;
  height: 25px;
  object-fit: cover;
}

.xuanzhuti .text-wrapper-12 {
  position: relative;
  width: 92px;
  margin-top: -2.59px;
  margin-bottom: -1.41px;
  margin-right: -9.74px;
  font-family: 'STZhongsong-Regular', Helvetica;
  font-weight: 400;
  color: var(--black-1);
  font-size: 20px;
  letter-spacing: 0;
  line-height: 30px;
}

.xuanzhuti .nav-links {
  display: inline-flex;
  min-height: 0.59px;
  align-items: center;
  gap: 16.15px;
  position: relative;
  flex: 0 0 auto;
}

.xuanzhuti .nav-link {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 0 0 auto;
}

.xuanzhuti .text-wrapper-13 {
  position: relative;
  width: 47px;
  margin-top: -0.59px;
  font-family: var(--middle-font-family);
  font-weight: var(--middle-font-weight);
  color: var(--black-1);
  font-size: var(--middle-font-size);
  letter-spacing: var(--middle-letter-spacing);
  line-height: var(--middle-line-height);
  font-style: var(--middle-font-style);
}

.xuanzhuti .nav-link-2 {
  display: inline-flex;
  flex-direction: column;
  align-items: flex-start;
  position: relative;
  flex: 0 0 auto;
}

.xuanzhuti .text-wrapper-14 {
  position: relative;
  width: fit-content;
  margin-top: -0.59px;
  font-family: var(--middle-font-family);
  font-weight: var(--middle-font-weight);
  color: var(--black-1);
  font-size: var(--middle-font-size);
  letter-spacing: var(--middle-letter-spacing);
  line-height: var(--middle-line-height);
  white-space: nowrap;
  font-style: var(--middle-font-style);
}

.xuanzhuti .text-wrapper-15 {
  position: relative;
  width: fit-content;
  margin-top: -0.59px;
  font-family: var(--middle-font-family);
  font-weight: var(--middle-font-weight);
  color: #03a6ba;
  font-size: var(--middle-font-size);
  letter-spacing: var(--middle-letter-spacing);
  line-height: var(--middle-line-height);
  white-space: nowrap;
  font-style: var(--middle-font-style);
}

.xuanzhuti .right-column {
  display: flex;
  width: 187px;
  min-height: 0.59px;
  align-items: center;
  gap: 5.38px;
  position: relative;
}

.xuanzhuti .icon-button {
  display: flex;
  width: 28.26px;
  height: 28.26px;
  align-items: center;
  justify-content: center;
  position: relative;
  background-color: #0812254c;
  border-radius: 269.12px;
}

.xuanzhuti .bell {
  position: relative;
  width: 10.76px;
  height: 10.77px;
}

.xuanzhuti .heart {
  position: relative;
  width: 10px;
  height: 10px;
}

.xuanzhuti .profile {
  display: flex;
  width: 141px;
  align-items: center;
  justify-content: space-between;
  padding: 2.69px 10.76px 2.69px 2.69px;
  position: relative;
  margin-right: -21.28px;
  background-color: #0812254c;
  border-radius: 269.12px;
}

.xuanzhuti .profile-avatar {
  position: relative;
  width: 24.22px;
  height: 24.22px;
  border-radius: 269.12px;
  background-image: url(https://c.animaapp.com/mb7y23llrri4PN/img/th-1.png);
  background-size: cover;
  background-position: 50% 50%;
}

.xuanzhuti .profile-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 1.35px;
  padding: 0px 4.04px;
  position: relative;
  flex: 1;
  align-self: stretch;
  flex-grow: 1;
  margin-left: -2.38e-7px;
}

.xuanzhuti .username {
  position: relative;
  width: fit-content;
  margin-top: -0.59px;
  font-family: var(--small-bold-font-family);
  font-weight: var(--small-bold-font-weight);
  color: #ffffffcc;
  font-size: var(--small-bold-font-size);
  letter-spacing: var(--small-bold-letter-spacing);
  line-height: var(--small-bold-line-height);
  white-space: nowrap;
  font-style: var(--small-bold-font-style);
}

.xuanzhuti .email {
  position: relative;
  width: fit-content;
  margin-right: -1.82px;
  font-family: var(--sususmall-font-family);
  font-weight: var(--sususmall-font-weight);
  color: #ffffffb2;
  font-size: var(--sususmall-font-size);
  letter-spacing: var(--sususmall-letter-spacing);
  line-height: var(--sususmall-line-height);
  white-space: nowrap;
  font-style: var(--sususmall-font-style);
  background: transparent;
  border: none;
  padding: 0;
}

.xuanzhuti .caret-down {
  position: relative;
  width: 8.07px;
  height: 8.07px;
  margin-left: -2.38e-7px;
}
</style>