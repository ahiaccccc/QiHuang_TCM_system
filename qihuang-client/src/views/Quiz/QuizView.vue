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
            <p class="p">
              <span class="text-wrapper">|</span>
              <span class="span">&nbsp;</span>
              <span class="text-wrapper-2">答题主题</span>
            </p>
          </div>
          <div class="card-grid-image">
            <div class="frame-2">
              <div
                class="frame-3"
                v-for="(item, idx) in computedQuizList"
                :key="item.id"
              >
                <!-- 根据激活状态显示不同内容 -->
                <div v-if="isActivating && activeClassId === item.id">
                  <div class="group">
                    <div
                      class="overlap-group"
                      @click="stopQuiz()"
                    >
                      <div class="text-wrapper-3">停止答题</div>
                    </div>
                  </div>
                  <div class="text-wrapper-4">{{ item.title }}</div>
                  <div
                    class="button-blue"
                    @click="continueQuiz(item.id)"
                  >
                    <div class="text-wrapper-5">继续答题</div>
                  </div>

                  <div
                    class="frame-4"
                    :style="{ 
    backgroundImage: `url('${getImageUrl(item.title)}')`,
      backgroundSize: 'cover',
      backgroundPosition: '50% 50%'
  }"
                  ></div>
                  <div class="frame-5">
                    <!-- 修改1：激活状态下显示答题进度 -->
                    <div class="frame-6">
                      <div class="text-wrapper-6">{{ item.progress }}</div>
                      <div class="text-wrapper-7">答题进度</div>
                    </div>
                    <!-- 修改2：激活状态下显示测试类型 -->
                    <div class="frame-7">
                      <div class="text-wrapper-8">{{ item.mode }}</div>
                      <div class="text-wrapper-9">测试类型</div>
                    </div>
                  </div>
                </div>
                <div v-else>
                  <div class="group">
                    <div
                      class="overlap-group"
                      @click="startQuiz(item.id, 'PRACTICE', 30)"
                    >
                      <div class="text-wrapper-3">模拟测试</div>
                    </div>
                  </div>
                  <div class="text-wrapper-4">{{ item.title }}</div>
                  <div
                    class="button-blue"
                    @click="startQuiz(item.id, 'RANK', 30)"
                  >
                    <div class="text-wrapper-5">正式答题</div>
                  </div>
                  <div
                    class="frame-4"
                    :style="{ 
    backgroundImage: `url('${getImageUrl(item.title)}')`,
      backgroundSize: 'cover',
      backgroundPosition: '50% 50%'
  }"
                  ></div>
                  <div class="frame-5">
                    <div class="frame-6">
                      <div class="text-wrapper-6">{{ item.rate }}</div>
                      <div class="text-wrapper-7">正确率</div>
                    </div>
                    <div class="frame-7">
                      <div class="text-wrapper-8">{{ item.count }}</div>
                      <div class="text-wrapper-9">测试次数</div>
                    </div>
                  </div>
                </div>

              </div>

            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue'
import {
  initAnswerRecord,
  findAllQuestionClasses,
  finishAnswerRecord,
  startAnswerRecord,
} from '@/apis/question-apis'
import { useQuizStore } from '@/stores/quiz'
import { useRouter } from 'vue-router'

const getImageUrl = (title) => {
  try {
    return `/${title}.jpg`
  } catch {
    return '' // 或返回默认图片
  }
}

// 定义响应式数据
const classList = ref([]) // 存储问题类别
const historyList = ref([]) // 存储历史答题记录
const isActivating = ref(false) // 当前是否有激活的答题
const activeClassId = ref(null) // 当前激活的答题类别ID
const activeRecord = ref(null) // 存储激活的记录详情

const quizStore = useQuizStore()
const router = useRouter()

// const computedQuizList = computed(() => {
//   // 如果有激活的答题，只返回激活的类别
//   if (isActivating.value && activeClassId.value) {
//     const activeClass = classList.value.find((c) => c.id === activeClassId.value)
//     if (activeClass && activeRecord.value) {
//       // 计算答题进度
//       const answers = activeRecord.value.answers || ''
//       const total = answers.length
//       const answered = answers.replace(/\*/g, '').length
//       const progress = total > 0 ? ((answered / total) * 100).toFixed(2) + '%' : '0%'

//       // 确定测试类型
//       const mode = activeRecord.value.playMode === 'PRACTICE' ? '练习' : '排位赛'

//       return [
//         {
//           id: activeClass.id,
//           title: activeClass.className,
//           progress, // 使用progress字段
//           mode, // 使用mode字段
//         },
//       ]
//     }
//     return []
//   }

//   // 没有激活答题时，计算所有类别的历史最高正确率和测试次数
//   return classList.value.map((cls) => {
//     // 过滤出当前类别的历史记录
//     const records = historyList.value.filter(
//       (record) => record.classId === cls.id && record.activating === 0
//     )

//     let highestRate = 0 // 历史最高正确率
//     let recordCount = records.length // 测试次数

//     // 计算历史最高正确率
//     if (records.length > 0) {
//       records.forEach((record) => {
//         const correctStr = record.correct || ''
//         const total = correctStr.length

//         if (total > 0) {
//           // 计算1的个数（正确答题数）
//           const correctNum = (correctStr.match(/1/g) || []).length
//           const rate = (correctNum / total) * 100
//           // console.log(`类别ID: ${cls.id}, 正确率: ${rate.toFixed(2)}%`)

//           // 更新最高正确率
//           if (rate > highestRate) {
//             highestRate = rate
//           }
//         }
//       })
//     }

//     // 格式化正确率字符串
//     const rateStr = records.length > 0 ? highestRate.toFixed(2) + '%' : '0%'

//     return {
//       id: cls.id,
//       title: cls.className,
//       rate: rateStr, // 历史最高正确率
//       count: recordCount, // 测试次数
//     }
//   })
// })
const computedQuizList = computed(() => {
  // 如果有激活的答题，只返回激活的类别
  if (isActivating.value && activeClassId.value) {
    const activeClass = classList.value.find((c) => c.id === activeClassId.value)
    if (activeClass && activeRecord.value) {
      // 计算答题进度
      const answers = activeRecord.value.answers || ''
      const total = answers.length
      const answered = answers.replace(/\*/g, '').length
      const progress = total > 0 ? ((answered / total) * 100).toFixed(2) + '%' : '0%'

      // 确定测试类型
      const mode = activeRecord.value.playMode === 'PRACTICE' ? '练习' : '排位赛'

      return [
        {
          id: activeClass.id,
          title: activeClass.className,
          progress, // 使用progress字段
          mode, // 使用mode字段
        },
      ]
    }
    return []
  }

  // 没有激活答题时，计算所有类别的历史最高正确率和测试次数
  return classList.value.map((cls) => {
    // 过滤出当前类别的历史记录，只计算RANK模式的记录
    const records = historyList.value.filter(
      (record) => record.classId === cls.id && record.activating === 0 && record.playMode === 'RANK'
    )

    let highestRate = 0 // 历史最高正确率
    let recordCount = records.length // 测试次数（只计算RANK模式）

    // 计算历史最高正确率
    if (records.length > 0) {
      records.forEach((record) => {
        const correctStr = record.correct || ''
        const total = correctStr.length

        if (total > 0) {
          // 计算1的个数（正确答题数）
          const correctNum = (correctStr.match(/1/g) || []).length
          const rate = (correctNum / total) * 100
          // console.log(`类别ID: ${cls.id}, 正确率: ${rate.toFixed(2)}%`)

          // 更新最高正确率
          if (rate > highestRate) {
            highestRate = rate
          }
        }
      })
    }

    // 格式化正确率字符串
    const rateStr = records.length > 0 ? highestRate.toFixed(2) + '%' : '0%'

    return {
      id: cls.id,
      title: cls.className,
      rate: rateStr, // 历史最高正确率
      count: recordCount, // 测试次数（只计算RANK模式）
    }
  })
})

// 开始答题
const startQuiz = async (classId, playMode, limit) => {
  try {
    const className = classList.value.find((c) => c.id === classId)?.className || '未知类别'
    // 调用API开始答题
    await quizStore.startAnswerRecordStore(classId, playMode, limit, className)

    // 跳转到答题页面
    router.push({
      name: 'quiz-answer',
    })
  } catch (error) {
    console.error('开始答题失败:', error)
    // 这里可以添加错误提示
  }
}

// 停止答题
const stopQuiz = async () => {
  // console.log('停止答题，当前回答记录ID:', activeRecord.value.id)
  activeRecord.value = await finishAnswerRecord(activeRecord.value.id, activeRecord.value.answers)
  // 假设finishAnswerRecord是一个异步函数，返回更新后的记录
  // console.log('更新答题记录:', activeRecord.value)
  // let oldActiveClassId = activeRecord.value.id
  isActivating.value = false
  activeClassId.value = null
  activeRecord.value = null
  // 这里可以添加停止答题的API调用
  // console.log('停止答题，类别ID:', oldActiveClassId)
  // 重新获取历史记录
  try {
    const historyResponse = await initAnswerRecord()
    historyList.value = historyResponse
    // console.log('更新后的历史答题记录:', historyResponse)
  } catch (error) {
    console.error('更新历史答题记录失败:', error)
  }
}

// 继续答题
const continueQuiz = (classId) => {
  // 这里可以添加继续答题的逻辑
  // console.log('继续答题，类别ID:', classId)

  const classItem = classList.value.find((c) => c.id === classId)
  // console.log('继续答题的类别:', classItem)
  const className = classItem ? classItem.className : '未知类别'
  // console.log('继续答题的类别名称:', className)
  quizStore.setCurrentQuiz(activeRecord.value, className)
  // 跳转到答题页面
  router.push({
    name: 'quiz-answer',
  })
}

onMounted(async () => {
  try {
    // 获取历史答题记录
    const historyResponse = await initAnswerRecord()
    historyList.value = historyResponse

    // 检查激活记录
    const activatingRecord = historyResponse.find((record) => record.activating === 1)
    if (activatingRecord) {
      isActivating.value = true
      activeClassId.value = activatingRecord.classId
      activeRecord.value = activatingRecord // 存储激活记录
    }

    console.log('初始化答题记录返回:', historyResponse)
  } catch (error) {
    console.error('初始化答题记录失败:', error)
  }

  try {
    // 获取问题类别
    const classResponse = await findAllQuestionClasses()
    classList.value = classResponse
    // console.log('获取所有题目类别返回:', classResponse)
  } catch (error) {
    console.error('获取所有题目类别失败:', error)
  }
  await loadProfile()
})

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
      // console.log('个人信息加载成功:', profile.value)
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
  }
}
</script>

<style lang="scss" scoped>
/* 新增按钮容器样式 */
.button-container {
  display: flex;
  gap: 10px;
  position: absolute;
  top: 230px;
  left: 205px;

  .button-stop,
  .button-continue {
    display: flex;
    width: 112px;
    height: 35px;
    align-items: center;
    justify-content: center;
    border-radius: 8px;

    &:hover {
      opacity: 0.8;
      cursor: pointer;
    }
  }

  .button-stop {
    background-color: #f76b56;
  }

  .button-continue {
    background-color: var(--zhonglv);
  }
}

html,
body {
  margin: 0px;
  height: 100%;
}

.xuanzhuti {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  position: relative;

  .back {
    position: relative;
    width: 100%;
    min-width: 1000px;
    height: 100vh;
  }

  .hero-section {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 53px;
    left: 0;
  }

  .main-area {
    position: relative;
    width: 100%;
    height: 100%;
    left: -3px;
    border-radius: 10px;
    overflow: hidden;
    overflow-y: scroll;

    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }
  }

  .frame {
    position: absolute;
    width: 100%;
    height: 25vh;
    top: 0;
    left: 0;
    background-color: #ffffff33;
  }

  .div {
    display: flex;
    flex-direction: column;
    width: 100%;
    align-items: center;
    position: absolute;
    top: 148px;
    left: 0;
    background-color: var(--danlan);
  }

  .div-wrapper {
    position: relative;
    flex: 0 0 auto;
    left: -30vw;
  }

  .p {
    width: 148px;
    top: 3px;
    left: 10px;
    margin: 20px;
    font-family: var(--susularge-bold-font-family);
    font-weight: var(--susularge-bold-font-weight);
    color: transparent;
    font-size: var(--susularge-bold-font-size);
    letter-spacing: var(--susularge-bold-letter-spacing);
    line-height: var(--susularge-bold-line-height);
    white-space: nowrap;
    font-style: var(--susularge-bold-font-style);
  }

  .text-wrapper {
    color: #50ac32;
    font-family: var(--susularge-bold-font-family);
    font-style: var(--susularge-bold-font-style);
    font-weight: var(--susularge-bold-font-weight);
    letter-spacing: var(--susularge-bold-letter-spacing);
    line-height: var(--susularge-bold-line-height);
    font-size: var(--susularge-bold-font-size);
  }

  .span {
    color: #298117;
    font-family: var(--susularge-bold-font-family);
    font-style: var(--susularge-bold-font-style);
    font-weight: var(--susularge-bold-font-weight);
    letter-spacing: var(--susularge-bold-letter-spacing);
    line-height: var(--susularge-bold-line-height);
    font-size: var(--susularge-bold-font-size);
  }

  .text-wrapper-2 {
    color: #000000;
    font-family: var(--susularge-bold-font-family);
    font-style: var(--susularge-bold-font-style);
    font-weight: var(--susularge-bold-font-weight);
    letter-spacing: var(--susularge-bold-letter-spacing);
    line-height: var(--susularge-bold-line-height);
    font-size: var(--susularge-bold-font-size);
  }

  .card-grid-image {
    display: flex;
    flex-direction: column;
    width: 75%;
    align-items: flex-start;
    gap: 20px;
    padding: 40px 64px;
    margin-bottom: 7vh;
    position: relative;
    background-color: #ffffff;
    box-shadow: 0px 4px 4px #00000040;
  }

  .frame-2 {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(359px, 1fr));
    gap: 35px 24px;
    padding: 20px 10px;
    width: 100%;
    height: 761px;
    align-items: flex-start;
    position: relative;
    overflow-x: auto;
    justify-items: center;

    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }
  }

  .frame-3 {
    position: relative;
    width: 359px;
    height: 291px;
    background-color: #ffffff;
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid;
    border-color: #c0c0c0;
  }

  .group {
    position: absolute;
    width: 114px;
    height: 35px;
    top: 230px;
    left: 41px;
  }

  .overlap-group {
    position: relative;
    width: 112px;
    height: 35px;
    background-color: #ffffff;
    border-radius: 5px;
    border: 1px solid;
    border-color: #f76b56;

    &:hover {
      background-color: #f76b56;
      cursor: pointer;
      transition: background-color 0.1s ease;

      .text-wrapper-3 {
        color: #ffffff;
      }
    }
  }

  .text-wrapper-3 {
    position: absolute;
    width: 87px;
    top: 7px;
    left: 13px;
    font-family: var(--middle-font-family);
    font-weight: var(--middle-font-weight);
    color: #f76b56;
    font-size: var(--middle-font-size);
    text-align: center;
    letter-spacing: var(--middle-letter-spacing);
    line-height: var(--middle-line-height);
    white-space: nowrap;
    font-style: var(--middle-font-style);
  }

  .text-wrapper-4 {
    position: absolute;
    width: 111px;
    top: 128px;
    left: 134px;
    font-family: var(--large-bold-font-family);
    font-weight: var(--large-bold-font-weight);
    color: #4c4c4c;
    font-size: var(--large-bold-font-size);
    letter-spacing: var(--large-bold-letter-spacing);
    line-height: var(--large-bold-line-height);
    white-space: nowrap;
    font-style: var(--large-bold-font-style);
  }

  .button-blue {
    display: flex;
    width: 112px;
    height: 35px;
    align-items: center;
    justify-content: center;
    position: absolute;
    top: 230px;
    left: 205px;
    background-color: var(--zhonglv);
    border-radius: 8px;

    &:hover {
      background-color: var(--heilan);
      cursor: pointer;
      transition: background-color 0.1s ease;
    }
  }

  .text-wrapper-5 {
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

  .frame-4 {
    position: absolute;
    width: 359px;
    height: 117px;
    top: 0;
    left: 0;
    // background: url(../../../public/中医基础理论.jpg) 50% 50% / cover;
  }

  .frame-5 {
    position: absolute;
    width: 307px;
    height: 73px;
    top: 149px;
    left: 41px;
  }

  .frame-6 {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 9px 0px;
    position: absolute;
    top: 0;
    left: 195px;
  }

  .text-wrapper-6 {
    position: relative;
    width: 27px;
    height: 23px;
    margin-top: -1px;
    font-family: var(--middle-font-family);
    font-weight: var(--middle-font-weight);
    color: var(--heilan);
    font-size: var(--middle-font-size);
    letter-spacing: var(--middle-letter-spacing);
    line-height: var(--middle-line-height);
    font-style: var(--middle-font-style);
  }

  .text-wrapper-7 {
    position: relative;
    align-self: stretch;
    height: 20px;
    font-family: var(--middle-font-family);
    font-weight: var(--middle-font-weight);
    color: #000000;
    font-size: var(--middle-font-size);
    letter-spacing: var(--middle-letter-spacing);
    line-height: var(--middle-line-height);
    font-style: var(--middle-font-style);
  }

  .frame-7 {
    display: flex;
    flex-direction: column;
    width: 85px;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 9px 0px;
    position: absolute;
    top: 3px;
    left: 25px;
  }

  .text-wrapper-8 {
    position: relative;
    width: auto;
    padding: 0 5px;
    height: 23px;
    margin-top: -1px;
    font-family: var(--middle-font-family);
    font-weight: var(--middle-font-weight);
    color: var(--shenlan);
    font-size: var(--middle-font-size);
    text-align: center;
    letter-spacing: var(--middle-letter-spacing);
    line-height: var(--middle-line-height);
    font-style: var(--middle-font-style);
  }

  .text-wrapper-9 {
    position: relative;
    width: 75px;
    height: 20px;
    font-family: var(--middle-font-family);
    font-weight: var(--middle-font-weight);
    color: #000000;
    font-size: var(--middle-font-size);
    letter-spacing: var(--middle-letter-spacing);
    line-height: var(--middle-line-height);
    font-style: var(--middle-font-style);
  }

  .frame-8 {
    position: relative;
    width: 359px;
    height: 291px;
    margin-bottom: -222px;
    background-color: #ffffff;
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid;
    border-color: #c0c0c0;
  }

  .navigation {
    display: flex;
    width: 100%;
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

  .nav-container {
    display: flex;
    width: 900px;
    align-items: center;
    justify-content: space-between;
    padding: 0px 16.15px;
    position: relative;
  }

  .left-column {
    display: flex;
    align-items: center;
    gap: 32.29px;
    position: relative;
    flex: 1;
    flex-grow: 1;
  }

  .logo {
    display: flex;
    width: 114.64px;
    height: 26px;
    align-items: center;
    gap: 5.38px;
    position: relative;
  }

  .img {
    position: relative;
    width: 27px;
    height: 25px;
    object-fit: cover;
  }

  .text-wrapper-10 {
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

  .nav-links {
    display: inline-flex;
    min-height: 0.59px;
    align-items: center;
    gap: 16.15px;
    position: relative;
    flex: 0 0 auto;
  }

  .nav-link {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    flex: 0 0 auto;
  }

  .text-wrapper-11 {
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

  .nav-link-2 {
    display: inline-flex;
    flex-direction: column;
    align-items: flex-start;
    position: relative;
    flex: 0 0 auto;
  }

  .text-wrapper-12 {
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

  .text-wrapper-13 {
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

  .right-column {
    display: flex;
    width: 187px;
    min-height: 0.59px;
    align-items: center;
    gap: 5.38px;
    position: relative;
  }

  .icon-button {
    display: flex;
    width: 28.26px;
    height: 28.26px;
    align-items: center;
    justify-content: center;
    position: relative;
    background-color: #0812254c;
    border-radius: 269.12px;
  }

  .bell {
    position: relative;
    width: 10.77px;
    height: 10.77px;
  }

  .heart {
    position: relative;
    width: 10px;
    height: 10px;
  }

  .profile {
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

  .profile-avatar {
    position: relative;
    width: 24.22px;
    height: 24.22px;
    border-radius: 269.12px;
    background-image: url(https://c.animaapp.com/mabzoynihMfxud/img/th-1.png);
    background-size: cover;
    background-position: 50% 50%;
  }

  .profile-details {
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

  .username {
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

  .email {
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

  .caret-down {
    position: relative;
    width: 8.07px;
    height: 8.07px;
    margin-left: -2.38e-7px;
  }
}

// }
</style>

<style>
:root {
  --shenlan: rgba(53, 86, 90, 1);
  --zhonglv: rgba(92, 176, 106, 1);
  --heilan: rgba(71, 85, 105, 1);
  --danlan: rgba(232, 239, 238, 1);
  --black-1: rgba(68, 68, 68, 1);
  --grayswhite: rgba(255, 255, 255, 1);
  --transparent: rgba(255, 255, 255, 0.6);
  --middle-font-family: 'DengXian', Helvetica;
  --middle-font-weight: 400;
  --middle-font-size: 16px;
  --middle-letter-spacing: 0px;
  --middle-line-height: normal;
  --middle-font-style: normal;
  --susularge-bold-font-family: 'DengXian', Helvetica;
  --susularge-bold-font-weight: 700;
  --susularge-bold-font-size: 26px;
  --susularge-bold-letter-spacing: 0px;
  --susularge-bold-line-height: normal;
  --susularge-bold-font-style: normal;
  --large-bold-font-family: 'DengXian', Helvetica;
  --large-bold-font-weight: 700;
  --large-bold-font-size: 18px;
  --large-bold-letter-spacing: 0px;
  --large-bold-line-height: normal;
  --large-bold-font-style: normal;
  --heading-font-family: 'Inter', Helvetica;
  --heading-font-weight: 600;
  --heading-font-size: 24px;
  --heading-letter-spacing: -0.48px;
  --heading-line-height: 120.00000762939453%;
  --heading-font-style: normal;
  --single-line-body-base-font-family: 'Inter', Helvetica;
  --single-line-body-base-font-weight: 400;
  --single-line-body-base-font-size: 16px;
  --single-line-body-base-letter-spacing: 0px;
  --single-line-body-base-line-height: 100%;
  --single-line-body-base-font-style: normal;
  --body-strong-font-family: 'Inter', Helvetica;
  --body-strong-font-weight: 600;
  --body-strong-font-size: 16px;
  --body-strong-letter-spacing: 0px;
  --body-strong-line-height: 140%;
  --body-strong-font-style: normal;
  --body-base-font-family: 'Inter', Helvetica;
  --body-base-font-weight: 400;
  --body-base-font-size: 16px;
  --body-base-letter-spacing: 0px;
  --body-base-line-height: 140%;
  --body-base-font-style: normal;
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
  --drop-shadow-200: 0px 1px 4px 0px rgba(12, 12, 13, 0.05), 0px 1px 4px 0px rgba(12, 12, 13, 0.1);
}
</style>