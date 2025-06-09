<template>
  <div class="xuanzhuti">
    <Navi
      :avatar="getAvatarUrl(profile.avatar) || defaultAvatar"
      :nickname="profile.username"
    />

    <div class="hero-section">
      <div class="main-area">
        <div class="frame"></div>
        <div class="div">
          <div class="div-wrapper">
            <p class="p"><span class="text-wrapper">| </span> <span class="span">{{ currentClassName }}（管理员模式）</span></p>
          </div>
          <div class="frame-2">
            <div class="overlap">
              <div class="frame-3">
                <div class="frame-4">
                  <div class="text-wrapper-2">{{currentIndex+1}} .
                    <input
                      v-if="editingQuestion"
                      v-model="currentQuestion.question"
                      class="question-input"
                      @blur="saveQuestion"
                    />
                    <span
                      v-else
                      @click="editQuestion"
                    >{{ currentQuestion.question }}</span>
                  </div>
                  <div class="text-wrapper-3">
                    <div
                      v-for="(option, index) in options"
                      :key="index"
                      class="option-item"
                      @click="setCorrectAnswer(index)"
                      :class="{ 'correct-option': currentQuestion.correctAnswer === String.fromCharCode('A'.charCodeAt(0) + index) }"
                    >
                      <span v-if="currentQuestion.correctAnswer === String.fromCharCode('A'.charCodeAt(0) + index)">●</span>
                      <span v-else>○</span>
                      <span
                        class="option-label"
                        @click.stop="editOption(index)"
                      >
                        <input
                          v-if="editingOptionIndex === index"
                          v-model="options[index]"
                          class="option-input"
                          @blur="saveOption(index)"
                        />
                        <span v-else>
                          &nbsp;&nbsp;{{ String.fromCharCode('A'.charCodeAt(0) + index) }}.&nbsp;{{ option }}
                        </span>
                      </span>
                      <br>
                    </div>
                  </div>
                </div>
                <div class="overlap-group">
                  <div class="frame-5">
                    <n-popconfirm
                      @positive-click="confirmDeleteQuestion"
                      negative-text="取消"
                      positive-text="确认删除"
                    >
                      <template #trigger>
                        <div class="button-red">
                          <div class="text-wrapper-4">删除</div>
                        </div>
                      </template>
                      <span>确认要删除这道题目吗？删除后将无法恢复。</span>
                    </n-popconfirm>

                    <div class="text-wrapper-5">{{ currentIndex + 1 }}/{{ questions.length }}</div>

                    <n-popconfirm
                      @positive-click="saveQuestionChanges"
                      negative-text="取消"
                      positive-text="确认保存"
                    >
                      <template #trigger>
                        <div class="button-blue">
                          <div class="text-wrapper-4">保存</div>
                        </div>
                      </template>
                      <span>确认要保存对这道题目的修改吗？</span>
                    </n-popconfirm>
                  </div>
                </div>
              </div>
              <div class="frame-6">
                <div class="text-wrapper-6">单选题</div>
              </div>
            </div>

            <div class="frame-7">
              <div class="text-wrapper-8">题目列表</div>
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
                  <div class="text-wrapper-9">未完成</div>
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
                  <div class="text-wrapper-10">已完成</div>
                </div>
              </div>
              <div class="frame-10">
                <div
                  v-for="(question, index) in questions"
                  :key="index"
                  class="frame-11"
                  :class="{
                    'current': currentIndex === index,
                    'answered': question.correctAnswer
                  }"
                  @click="goToQuestion(index)"
                >
                  <div class="text-wrapper-11">{{ index + 1 }}</div>
                </div>
              </div>
              <div class="button-group">
                <n-popconfirm
                  @positive-click="addNewQuestion"
                  negative-text="取消"
                  positive-text="确认新增"
                >
                  <template #trigger>
                    <div class="button-blue-submit">
                      <div class="text-wrapper-4">新增</div>
                    </div>
                  </template>
                  <span>确认要新增一道题目吗？</span>
                </n-popconfirm>

                <n-upload
                  action="http://localhost:8080/api/upload"
                  :headers="{
                    'Authorization': 'Bearer ' + token
                  }"
                  @finish="handleUploadFinish"
                >
                  <n-button
                    type="primary"
                    class="upload-button"
                  >
                    批量导入
                  </n-button>
                </n-upload>
              </div>
              <div class="element-wrapper">
                <p class="element">
                  <span class="span"> 题目总数：</span>
                  <span class="text-wrapper-7">{{ questions.length }}</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NPopconfirm, NUpload, NButton, useMessage } from 'naive-ui'
import { findQuestionByclassId } from '@/apis/question-apis'
import Navi from '../components/NaviHomeView.vue'
import { getProfileAPI } from '@/apis/user'
import defaultAvatar from '../../assets/images/defaultAvatar.png'

const route = useRoute()
const message = useMessage() // 添加消息弹窗

const classId = ref(route.query.classId)
const currentClassName = ref('')
const questions = ref([])
const currentIndex = ref(0)
const editingQuestion = ref(false)
const editingOptionIndex = ref(-1)
const token = ref('admin_token')

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
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}

// 当前问题
const currentQuestion = computed(() => {
  return questions.value[currentIndex.value] || {}
})

// 当前问题的选项
const options = computed(() => {
  if (!currentQuestion.value.options) return []
  return currentQuestion.value.options.split('\\n')
})

const setCorrectAnswer = (index) => {
  questions.value[currentIndex.value].correctAnswer = String.fromCharCode('A'.charCodeAt(0) + index)
}

const editQuestion = () => {
  editingQuestion.value = true
}

const saveQuestion = () => {
  editingQuestion.value = false
  // 这里应该调用API保存题目修改
}

const editOption = (index) => {
  editingOptionIndex.value = index
}

const saveOption = (index) => {
  editingOptionIndex.value = -1
  // 更新选项
  const newOptions = [...options.value]
  newOptions[index] = newOptions[index].trim()
  questions.value[currentIndex.value].options = newOptions.join('\\n')
  // 这里应该调用API保存选项修改
}

const saveQuestionChanges = () => {
  // 调用API保存所有修改
  console.log('保存题目修改:', currentQuestion.value)
  setTimeout(() => {
    message.success('题目修改已保存', { duration: 2000 })
  }, 1000)
}

const confirmDeleteQuestion = () => {
  // 调用API删除题目
  questions.value.splice(currentIndex.value, 1)
  if (currentIndex.value >= questions.value.length) {
    currentIndex.value = Math.max(0, questions.value.length - 1)
  }
  setTimeout(() => {
    message.success('题目已删除', { duration: 2000 })
  }, 1000)
}

const addNewQuestion = () => {
  const newQuestion = {
    id: Date.now(),
    question: '新题目内容',
    options: '选项A\\n选项B\\n选项C\\n选项D',
    correctAnswer: 'A',
  }
  questions.value.push(newQuestion)
  currentIndex.value = questions.value.length - 1
  setTimeout(() => {
    message.success('新增题目成功', { duration: 2000 })
  }, 1000)
}

const goToQuestion = (index) => {
  currentIndex.value = index
  editingQuestion.value = false
  editingOptionIndex.value = -1
}

const handleUploadFinish = ({ file, event }) => {
  console.log('上传完成:', file, event)

  const newQuestions = [
    {
      id: Date.now() + 1,
      question: '上传的题目1',
      options: '选项A\\n选项B\\n选项C\\n选项D',
      correctAnswer: 'A',
    },
    {
      id: Date.now() + 2,
      question: '上传的题目2',
      options: '选项A\\n选项B\\n选项C\\n选项D',
      correctAnswer: 'B',
    },
  ]
  questions.value.push(...newQuestions)
  setTimeout(() => {
    message.success('批量导入成功', { duration: 2000 })
  }, 1000)
}

onMounted(async () => {
  try {
    console.log('当前路由参数:', route.query)
    console.log('加载题目，classId:', classId.value)
    const response = await findQuestionByclassId(classId.value)
    console.log('获取题目:', response)
    questions.value = response.map((q) => ({
      ...q,
      // 确保每个题目有correctAnswer字段
      correctAnswer: q.correctAnswer || '',
    }))
    currentClassName.value = route.query.className
  } catch (error) {
    console.error('获取题目失败:', error)
  }
  await loadProfile()
})
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
  // position: absolute;
  left: 80%;
  top: 440px;
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
  width: 100%;
  height: 39px;
  top: -57px;

  left: 0;
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
  height: 50%;
  align-items: flex-start;
  gap: 10px 10px;
  padding: 10px;
  position: relative;
  flex: 0 0 auto;
  overflow-y: scroll;
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

/* 新增和修改的样式部分 */
.question-input {
  width: 90%;
  padding: 5px;
  border: 1px solid #35565a;
  border-radius: 4px;
  font-family: var(--large-font-family);
  font-size: var(--large-font-size);
}

.option-input {
  width: 80%;
  padding: 5px;
  border: 1px solid #35565a;
  border-radius: 4px;
  font-family: var(--large-font-family);
  font-size: var(--large-font-size);
}

.button-red {
  display: flex;
  width: 112px;
  height: 35px;
  align-items: center;
  justify-content: center;
  padding: 10.76px;
  position: relative;
  margin-top: -23.5px;
  margin-bottom: -23.5px;
  background-color: #f76b56;
  border-radius: 8px;
  &:hover {
    background-color: #e05540;
    cursor: pointer;
  }
}

.button-group {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 20px;
  align-items: center;
}

.upload-button {
  background-color: var(--zhonglv);
  &:hover {
    background-color: #7abf8b;
  }
}

/* 正确选项样式 */
.correct-option {
  background-color: rgba(92, 176, 106, 0.3) !important;
  color: #35565a;
  font-weight: bold;
}

/* 答题卡已完成题目样式 */
.frame-11.answered {
  background-color: #cecaca;
}

/* 当前题目样式 */
.frame-11.current {
  border: 2px solid #35565a !important;
  background-color: rgba(92, 176, 106, 0.3);
}
</style>