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
              <span class="text-wrapper-2">题目分类管理</span>
            </p>
          </div>
          <div class="card-grid-image">
            <div class="frame-2">
              <div
                class="frame-3"
                v-for="item in classList"
                :key="item.id"
                @click="goToQuestionManagement(item.id)"
              >
                <div class="group">
                  <div
                    class="overlap-group"
                    @click="stopQuiz()"
                  >
                    <div class="text-wrapper-3">停止答题</div>
                  </div>
                </div>
                <div class="text-wrapper-4">{{ item.className }}</div>
                <div
                  class="button-blue"
                  @click="goToQuestionManagement(item.id)"
                >
                  <div class="text-wrapper-5">管理主题</div>
                </div>

                <div
                  class="frame-4"
                  :style="{ 
    backgroundImage: `url('${getImageUrl(item.className)}')`,
      backgroundSize: 'cover',
      backgroundPosition: '50% 50%'
  }"
                ></div>

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { findAllQuestionClasses } from '@/apis/question-apis'
import Navi from '../components/NaviHomeView.vue'
import { getProfileAPI } from '@/apis/user'
import defaultAvatar from '../../assets/images/defaultAvatar.png'

const router = useRouter()
const classList = ref([])

const getImageUrl = (title) => {
  try {
    return `/${title}.jpg`
  } catch {
    return '' // 或返回默认图片
  }
}

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

const goToQuestionManagement = (classId) => {
  console.log('跳转到题目管理，分类ID:', classId)
  router.push({
    name: 'quiz-answer-admin',
    query: {
      classId: classId.toString(),
      className: classList.value.find((cls) => cls.id === classId)?.className || '',
    },
  })
}

onMounted(async () => {
  try {
    const response = await findAllQuestionClasses()
    classList.value = response.map((cls) => ({
      ...cls,
      questionCount: cls.questionCount || 0,
    }))
    console.log('题目分类列表:', classList.value)
  } catch (error) {
    console.error('获取题目分类失败:', error)
  }
  await loadProfile()
})
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
    height: 224px;
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
    left: 130px;
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
    top: 160px;
    left: 130px;
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

// /* 使用文档2中的样式，稍作修改 */
// .xuanzhuti {
//   display: flex;
//   flex-direction: column;
//   align-items: flex-start;
//   position: relative;

//   .back {
//     position: relative;
//     width: 100%;
//     min-width: 1000px;
//     height: 100vh;
//   }

//   .hero-section {
//     position: absolute;
//     width: 100%;
//     height: 100%;
//     top: 53px;
//     left: 0;
//   }

//   .main-area {
//     position: relative;
//     width: 100%;
//     height: 100%;
//     left: -3px;
//     border-radius: 10px;
//     overflow: hidden;
//     overflow-y: scroll;

//     &::-webkit-scrollbar {
//       width: 0;
//       display: none;
//     }
//   }

//   .frame {
//     position: absolute;
//     width: 100%;
//     height: 25vh;
//     top: 0;
//     left: 0;
//     background-color: #ffffff33;
//   }

//   .div {
//     display: flex;
//     flex-direction: column;
//     width: 100%;
//     align-items: center;
//     position: absolute;
//     top: 148px;
//     left: 0;
//     background-color: var(--danlan);
//   }

//   .div-wrapper {
//     position: relative;
//     flex: 0 0 auto;
//     left: -30vw;
//   }

//   .p {
//     width: 148px;
//     top: 3px;
//     left: 10px;
//     margin: 20px;
//     font-family: var(--susularge-bold-font-family);
//     font-weight: var(--susularge-bold-font-weight);
//     color: transparent;
//     font-size: var(--susularge-bold-font-size);
//     letter-spacing: var(--susularge-bold-letter-spacing);
//     line-height: var(--susularge-bold-line-height);
//     white-space: nowrap;
//     font-style: var(--susularge-bold-font-style);
//   }

//   .text-wrapper {
//     color: #50ac32;
//     font-family: var(--susularge-bold-font-family);
//     font-style: var(--susularge-bold-font-style);
//     font-weight: var(--susularge-bold-font-weight);
//     letter-spacing: var(--susularge-bold-letter-spacing);
//     line-height: var(--susularge-bold-line-height);
//     font-size: var(--susularge-bold-font-size);
//   }

//   .span {
//     color: #298117;
//     font-family: var(--susularge-bold-font-family);
//     font-style: var(--susularge-bold-font-style);
//     font-weight: var(--susularge-bold-font-weight);
//     letter-spacing: var(--susularge-bold-letter-spacing);
//     line-height: var(--susularge-bold-line-height);
//     font-size: var(--susularge-bold-font-size);
//   }

//   .text-wrapper-2 {
//     color: #000000;
//     font-family: var(--susularge-bold-font-family);
//     font-style: var(--susularge-bold-font-style);
//     font-weight: var(--susularge-bold-font-weight);
//     letter-spacing: var(--susularge-bold-letter-spacing);
//     line-height: var(--susularge-bold-line-height);
//     font-size: var(--susularge-bold-font-size);
//   }

//   .card-grid-image {
//     display: flex;
//     flex-direction: column;
//     width: 75%;
//     align-items: flex-start;
//     gap: 20px;
//     padding: 40px 64px;
//     margin-bottom: 7vh;
//     position: relative;
//     background-color: #ffffff;
//     box-shadow: 0px 4px 4px #00000040;
//   }

//   .frame-2 {
//     display: grid;
//     grid-template-columns: repeat(auto-fit, minmax(359px, 1fr));
//     gap: 35px 24px;
//     padding: 20px 10px;
//     width: 100%;
//     height: 761px;
//     align-items: flex-start;
//     position: relative;
//     overflow-x: auto;
//     justify-items: center;

//     &::-webkit-scrollbar {
//       width: 0;
//       display: none;
//     }
//   }

//   .frame-3 {
//     position: relative;
//     width: 359px;
//     height: 291px;
//     background-color: #ffffff;
//     border-radius: 10px;
//     overflow: hidden;
//     border: 1px solid;
//     border-color: #c0c0c0;
//     cursor: pointer;
//     transition: transform 0.2s, box-shadow 0.2s;

//     &:hover {
//       transform: translateY(-5px);
//       box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
//     }
//   }

//   .text-wrapper-4 {
//     position: absolute;
//     width: 111px;
//     top: 128px;
//     left: 134px;
//     font-family: var(--large-bold-font-family);
//     font-weight: var(--large-bold-font-weight);
//     color: #4c4c4c;
//     font-size: var(--large-bold-font-size);
//     letter-spacing: var(--large-bold-letter-spacing);
//     line-height: var(--large-bold-line-height);
//     white-space: nowrap;
//     font-style: var(--large-bold-font-style);
//   }

//   .frame-4 {
//     position: absolute;
//     width: 359px;
//     height: 117px;
//     top: 0;
//     left: 0;
//   }

//   .frame-5 {
//     position: absolute;
//     width: 307px;
//     height: 73px;
//     top: 149px;
//     left: 41px;
//   }

//   .frame-6 {
//     display: flex;
//     flex-direction: column;
//     align-items: center;
//     justify-content: center;
//     gap: 4px;
//     padding: 9px 0px;
//     position: absolute;
//     top: 0;
//     left: 195px;
//   }

//   .text-wrapper-6 {
//     position: relative;
//     width: 27px;
//     height: 23px;
//     margin-top: -1px;
//     font-family: var(--middle-font-family);
//     font-weight: var(--middle-font-weight);
//     color: var(--heilan);
//     font-size: var(--middle-font-size);
//     letter-spacing: var(--middle-letter-spacing);
//     line-height: var(--middle-line-height);
//     font-style: var(--middle-font-style);
//   }

//   .text-wrapper-7 {
//     position: relative;
//     align-self: stretch;
//     height: 20px;
//     font-family: var(--middle-font-family);
//     font-weight: var(--middle-font-weight);
//     color: #000000;
//     font-size: var(--middle-font-size);
//     letter-spacing: var(--middle-letter-spacing);
//     line-height: var(--middle-line-height);
//     font-style: var(--middle-font-style);
//   }
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