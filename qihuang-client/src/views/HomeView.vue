<template>
  <div class="main-content">
    <img
      class="img"
      src="https://c.animaapp.com/mbp1e89xyPt5yw/img/dab4c2b0cf469b636334f9116ea47276-1.png"
    />
    <div class="navigation">
      <div class="nav-container">
        <div class="left-column">
          <div class="logo">
            <img
              class="logo-2"
              src="https://c.animaapp.com/mbp1e89xyPt5yw/img/logo-1.png"
            />
            <div class="text-wrapper">岐黄慧问</div>
          </div>
        </div>
      </div>
    </div>
    <div class="hero-section">
      <div class="hero-container">
        <div class="content-grid">
          <div class="wrapper">
            <div class="display">岐黄慧问</div>
            <div class="heading">智启中医养生新体验</div>
          </div>
          <div class="text-block">AI赋能中医智慧，畅享双语问答、定制养生、古籍新解与趣味研习一站式体验</div>
        </div>
        <div class="actions">
          <button
            class="button"
            @click="handleRegister"
          >
            <div class="div">注册</div>
          </button>
          <button
            class="div-wrapper"
            @click="handleLogin"
          >
            <div class="text-wrapper-2">登录</div>
          </button>
        </div>
        <div class="card-wrapper">
          <img
            class="card-circle"
            src="https://c.animaapp.com/mbp1e89xyPt5yw/img/card-circle.svg"
          />
          <div class="card-item">
            <div class="logo-wrapper">
              <img
                class="aetna"
                src="https://c.animaapp.com/mbp1e89xyPt5yw/img/aetna-2.svg"
              />
            </div>
            <div class="content-wrapper">
              <div class="header">
                <div class="text-wrapper-3">趣味交互学习</div>
              </div>
              <div class="match-details">
                <div class="text-wrapper-4">动态药材卡+AI养生箴言+成就系统，玩转中医知识</div>
              </div>
            </div>
            <img
              class="arrow-line-up-right"
              src="https://c.animaapp.com/mbp1e89xyPt5yw/img/arrowlineupright.svg"
            />
          </div>
        </div>
        <div class="card-wrapper-2">
          <img
            class="card-circle-2"
            src="https://c.animaapp.com/mbp1e89xyPt5yw/img/card-circle-1.svg"
          />
          <div class="card-item-2">
            <div class="logo-wrapper">
              <img
                class="aetna"
                src="https://c.animaapp.com/mbp1e89xyPt5yw/img/aetna-1.svg"
              />
            </div>
            <div class="content-wrapper-2">
              <div class="header-2">
                <div class="text-wrapper-5">经典活化解读</div>
              </div>
              <div class="match-details">
                <div class="text-wrapper-6">AI解构《黄帝内经》，白话注释+生活案例秒懂古籍</div>
              </div>
            </div>
            <img
              class="arrow-line-up-right-2"
              src="https://c.animaapp.com/mbp1e89xyPt5yw/img/arrowlineupright.svg"
            />
          </div>
        </div>
        <div class="card-wrapper-3">
          <div class="card-item-3">
            <div class="logo-wrapper">
              <div class="element-humana-logo-RGB-wrapper">
                <img
                  class="element-humana-logo-RGB"
                  src="https://c.animaapp.com/mbp1e89xyPt5yw/img/2022-humana-logo-rgb-contrast-green-no-r-no-pad-horiz-1-2.png"
                />
              </div>
            </div>
            <div class="content-wrapper-2">
              <div class="header-2">
                <div class="text-wrapper-5">个性养生定制</div>
              </div>
              <div class="match-details">
                <div class="text-wrapper-6">节气/体质/年龄多维分析，AI生成专属调理方案</div>
              </div>
            </div>
            <img
              class="arrow-line-up-right-2"
              src="https://c.animaapp.com/mbp1e89xyPt5yw/img/arrowlineupright.svg"
            />
          </div>
          <img
            class="card-circle-3"
            src="https://c.animaapp.com/mbp1e89xyPt5yw/img/card-circle-1.svg"
          />
        </div>
        <div
          class="card-wrapper-4"
          :style="{ 'animation-delay': '0.2s' }"
        >
          <div class="card-item-3">
            <div class="logo-wrapper">
              <img
                class="aetna"
                src="https://c.animaapp.com/mbp1e89xyPt5yw/img/aetna.svg"
              />
            </div>
            <div class="content-wrapper-2">
              <div class="header-3">
                <div class="text-wrapper-7">AI双语智答</div>
              </div>
              <div class="match-details">
                <div class="text-wrapper-6">中英语音/文字即时问答，溯源典籍+案例解析</div>
              </div>
            </div>
            <img
              class="arrow-line-up-right-2"
              src="https://c.animaapp.com/mbp1e89xyPt5yw/img/arrowlineupright.svg"
            />
          </div>
          <img
            class="card-circle-3"
            src="https://c.animaapp.com/mbp1e89xyPt5yw/img/card-circle.svg"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
// 可以在这里添加交互逻辑
const handleLogin = () => {
  console.log('登录按钮点击')
  router.push('/login')
}

const handleRegister = () => {
  console.log('注册按钮点击')
  router.push('/register')
}
</script>

<style>
:root {
  --black-1: rgba(68, 68, 68, 1);
  --transparent: rgba(255, 255, 255, 0.6);
  --black-2: rgba(52, 52, 52, 1);
  --shenlan: rgba(53, 86, 90, 1);
  --large-font-family: 'DengXian', Helvetica;
  --large-font-weight: 400;
  --large-font-size: 18px;
  --large-letter-spacing: 0px;
  --large-line-height: normal;
  --large-font-style: normal;
  --middle-bold-font-family: 'Inter', Helvetica;
  --middle-bold-font-weight: 700;
  --middle-bold-font-size: 16px;
  --middle-bold-letter-spacing: 0px;
  --middle-bold-line-height: normal;
  --middle-bold-font-style: normal;
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
@import url('https://fonts.googleapis.com/css?family=Inter:var(--middle-bold-font-weight)');
@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}
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

.main-content {
  display: flex;
  flex-direction: column;
  height: 100vh;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
}

.main-content .img {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120%; // 宽度放大20%

  transform: translate(-50%, -50%);
  object-fit: cover;
}

.main-content {
  // ... 其他样式

  [class^='card-wrapper'] {
    animation: float 3s ease-in-out infinite;
  }

  // 或者单独为每个card-wrapper添加
  // .card-wrapper,
  // .card-wrapper-2,
  // .card-wrapper-3,
  // .card-wrapper-4 {
  //   animation: float 3s ease-in-out infinite;
  // }
}
.main-content .navigation {
  display: flex;
  width: 100vw;
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

.main-content .nav-container {
  display: flex;
  width: 768px;
  align-items: center;
  justify-content: space-around;
  padding: 0px 16.15px;
  position: relative;
}

.main-content .left-column {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 32.29px;
  position: relative;
  margin-left: -232.15px;
  margin-right: -232.15px;
}

.main-content .logo {
  display: flex;
  width: 114.64px;
  height: 26px;
  align-items: center;
  gap: 5.38px;
  position: relative;
}

.main-content .logo-2 {
  position: relative;
  width: 27px;
  height: 25px;
  object-fit: cover;
}

.main-content .text-wrapper {
  width: 92px;
  margin-top: -2.59px;
  margin-bottom: -1.41px;
  margin-right: -9.74px;
  font-family: 'STZhongsong-Regular', Helvetica;
  font-weight: 400;
  color: var(--black-1);
  font-size: 20px;
  line-height: 30px;
  position: relative;
  letter-spacing: 0;
}

.main-content .hero-section {
  width: 1027px;
  height: 509px;
  padding: 84.38px 0px 0px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.main-content .hero-container {
  width: 70%;
  height: 50%;
  gap: 103px;
  padding: 0px 16.15px;
  position: absolute;
  // top: 50%;
  // left: 50%;
  // transform: translate(-50%, -50%);
  margin-top: -12.69px;
  margin-bottom: -12.69px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.main-content .content-grid {
  width: 427px;
  height: 230px;
  align-items: center;
  gap: 30px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.main-content .wrapper {
  display: flex;
  flex-direction: column;
  width: 419.83px;
  align-items: center;
  gap: 10.76px;
  position: relative;
  flex: 0 0 auto;
}

.main-content .display {
  position: relative;
  width: fit-content;
  margin-top: -0.59px;
  text-shadow: 0px 2.34px 2.34px #4d6dc04c;
  font-family: 'STZhongsong-Regular', Helvetica;
  font-weight: 400;
  color: var(--black-2);
  font-size: 96px;
  letter-spacing: 1px;
  line-height: 96px;
  white-space: nowrap;
}

.main-content .heading {
  position: relative;
  width: 599px;
  margin-left: -89.58px;
  margin-right: -89.58px;
  font-family: 'Microsoft YaHei-Regular', Helvetica;
  font-weight: 400;
  color: var(--black-2);
  font-size: 64px;
  text-align: center;
  letter-spacing: -1.61px;
  line-height: 60.8px;
}

.main-content .text-block {
  align-self: stretch;
  margin-bottom: -5.18px;
  font-family: var(--large-font-family);
  font-weight: var(--large-font-weight);
  color: var(--black-2);
  font-size: var(--large-font-size);
  text-align: center;
  line-height: var(--large-line-height);
  position: relative;
  letter-spacing: var(--large-letter-spacing);
  font-style: var(--large-font-style);
}

.main-content .actions {
  &:hover {
    cursor: pointer;
  }
  display: flex;
  width: 235px;
  align-items: center;
  justify-content: center;
  gap: 25px;
  position: relative;
  flex: 0 0 auto;
}

.main-content .button {
  all: unset;
  box-sizing: border-box;
  display: flex;
  width: 100px;
  height: 35px;
  align-items: center;
  justify-content: center;
  padding: 10.76px;
  position: relative;
  background-color: var(--shenlan);
  border-radius: 8px;
}

.main-content .div {
  width: fit-content;
  margin-top: -3.44px;
  margin-bottom: -2.09px;
  font-family: var(--middle-bold-font-family);
  font-weight: var(--middle-bold-font-weight);
  color: #ffffffe6;
  font-size: var(--middle-bold-font-size);
  text-align: center;
  line-height: var(--middle-bold-line-height);
  white-space: nowrap;
  position: relative;
  letter-spacing: var(--middle-bold-letter-spacing);
  font-style: var(--middle-bold-font-style);
}

.main-content .div-wrapper {
  &:hover {
    cursor: pointer;
  }
  all: unset;
  box-sizing: border-box;
  display: flex;
  width: 100px;
  height: 35px;
  align-items: center;
  justify-content: center;
  gap: 5.38px;
  padding: 10.76px 10.76px;
  position: relative;
  border-radius: 8px;
  border: none;
  background: linear-gradient(180deg, rgba(255, 255, 255, 1) 0%, rgba(255, 255, 255, 0.83) 100%);
}

.main-content .div-wrapper::before {
  content: '';
  position: absolute;
  inset: 0;
  padding: 0.67px;
  border-radius: 8px;
  background: linear-gradient(17deg, rgba(58, 106, 229, 0.34) 0%, rgba(0, 0, 0, 0) 100%);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  z-index: 1;
  pointer-events: none;
}

.main-content .text-wrapper-2 {
  width: fit-content;
  margin-top: -3.44px;
  margin-bottom: -2.09px;
  font-family: var(--middle-bold-font-family);
  font-weight: var(--middle-bold-font-weight);
  color: var(--black-2);
  font-size: var(--middle-bold-font-size);
  line-height: var(--middle-bold-line-height);
  white-space: nowrap;
  position: relative;
  letter-spacing: var(--middle-bold-letter-spacing);
  font-style: var(--middle-bold-font-style);
}

.main-content .card-wrapper {
  display: flex;
  width: 195px;
  height: 40px;
  align-items: center;
  justify-content: center;
  gap: 10.76px;
  position: absolute;
  top: -120px;
  left: 651px;
}

.main-content .card-circle {
  margin-left: -4.33px;
  position: relative;
  width: 12.89px;
  height: 12.89px;
}

.main-content .card-item {
  height: 70px;
  margin-top: -15px;
  margin-bottom: -15px;
  margin-right: -4.33px;
  display: flex;
  width: 180px;
  align-items: center;
  justify-content: space-between;
  padding: 5.38px 10.76px 5.38px 5.38px;
  position: relative;
  background-color: var(--transparent);
  border-radius: 269.12px;
  backdrop-filter: blur(43.22px) brightness(100%);
  -webkit-backdrop-filter: blur(43.22px) brightness(100%);
}

.main-content .logo-wrapper {
  display: flex;
  width: 28.13px;
  align-items: center;
  justify-content: space-around;
  gap: 5.86px;
  position: relative;
}

.main-content .aetna {
  position: relative;
  width: 28.13px;
  height: 28.13px;
}

.main-content .content-wrapper {
  display: flex;
  flex-direction: column;
  width: 125px;
  height: 60px;
  align-items: flex-start;
  justify-content: center;
  gap: 4.04px;
  padding: 0px 5.38px;
  position: relative;
  margin-top: -0.38px;
  margin-bottom: -0.38px;
  margin-left: -0.5px;
}

.main-content .header {
  width: 93px;
  height: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 1.35px;
  position: relative;
}

.main-content .text-wrapper-3 {
  width: fit-content;
  font-family: var(--small-bold-font-family);
  font-weight: var(--small-bold-font-weight);
  color: var(--black-1);
  font-size: var(--small-bold-font-size);
  line-height: var(--small-bold-line-height);
  white-space: nowrap;
  position: relative;
  letter-spacing: var(--small-bold-letter-spacing);
  font-style: var(--small-bold-font-style);
}

.main-content .match-details {
  display: flex;
  align-items: center;
  gap: 2.69px;
  position: relative;
  align-self: stretch;
  width: 100%;
  flex: 0 0 auto;
}

.main-content .text-wrapper-4 {
  position: relative;
  width: 115px;
  height: 28px;
  margin-top: -0.73px;
  margin-right: -0.76px;
  font-family: var(--sususmall-font-family);
  font-weight: var(--sususmall-font-weight);
  color: var(--black-1);
  font-size: var(--sususmall-font-size);
  letter-spacing: var(--sususmall-letter-spacing);
  line-height: var(--sususmall-line-height);
  font-style: var(--sususmall-font-style);
}

.main-content .arrow-line-up-right {
  position: relative;
  width: 11.72px;
  height: 11.72px;
  margin-left: -0.5px;
}

.main-content .card-wrapper-2 {
  top: 230px;
  left: 716px;
  display: flex;
  width: 195px;
  height: 40px;
  align-items: center;
  gap: 10.76px;
  position: absolute;
}

.main-content .card-circle-2 {
  position: relative;
  width: 12.89px;
  height: 12.89px;
}

.main-content .card-item-2 {
  height: 63px;
  margin-top: -11.5px;
  margin-bottom: -11.5px;
  margin-right: -8.66px;
  display: flex;
  width: 180px;
  align-items: center;
  justify-content: space-between;
  padding: 5.38px 10.76px 5.38px 5.38px;
  position: relative;
  background-color: var(--transparent);
  border-radius: 269.12px;
  backdrop-filter: blur(43.22px) brightness(100%);
  -webkit-backdrop-filter: blur(43.22px) brightness(100%);
}

.main-content .content-wrapper-2 {
  width: 124px;
  height: 52px;
  align-items: flex-start;
  justify-content: center;
  gap: 4.04px;
  padding: 0px 5.38px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.main-content .header-2 {
  display: flex;
  flex-direction: column;
  width: 66px;
  height: 14px;
  align-items: flex-start;
  justify-content: center;
  gap: 1.35px;
  position: relative;
}

.main-content .text-wrapper-5 {
  position: relative;
  width: fit-content;
  margin-top: -1.23px;
  margin-right: -18px;
  font-family: var(--small-bold-font-family);
  font-weight: var(--small-bold-font-weight);
  color: var(--black-1);
  font-size: var(--small-bold-font-size);
  letter-spacing: var(--small-bold-letter-spacing);
  line-height: var(--small-bold-line-height);
  white-space: nowrap;
  font-style: var(--small-bold-font-style);
}

.main-content .text-wrapper-6 {
  width: 115px;
  height: 28px;
  margin-top: -0.73px;
  margin-right: -1.76px;
  font-family: var(--sususmall-font-family);
  font-weight: var(--sususmall-font-weight);
  font-size: var(--sususmall-font-size);
  position: relative;
  color: var(--black-1);
  letter-spacing: var(--sususmall-letter-spacing);
  line-height: var(--sususmall-line-height);
  font-style: var(--sususmall-font-style);
}

.main-content .arrow-line-up-right-2 {
  position: relative;
  width: 11.72px;
  height: 11.72px;
}

.main-content .card-wrapper-3 {
  top: 258px;
  left: -172px;
  display: flex;
  width: 195px;
  height: 40px;
  align-items: center;
  gap: 10.76px;
  position: absolute;
}

.main-content .card-item-3 {
  display: flex;
  width: 180px;
  height: 63px;
  align-items: center;
  justify-content: space-between;
  padding: 5.38px 10.76px 5.38px 5.38px;
  position: relative;
  margin-top: -11.5px;
  margin-bottom: -11.5px;
  background-color: var(--transparent);
  border-radius: 269.12px;
  backdrop-filter: blur(43.22px) brightness(100%);
  -webkit-backdrop-filter: blur(43.22px) brightness(100%);
}

.main-content .element-humana-logo-RGB-wrapper {
  border-radius: 234.4px;
  overflow: hidden;
  background: linear-gradient(180deg, rgba(223, 246, 198, 1) 0%, rgba(232, 255, 208, 0.81) 100%);
  position: relative;
  width: 28.13px;
  height: 28.13px;
}

.main-content .element-humana-logo-RGB {
  position: absolute;
  width: 12px;
  height: 16px;
  top: 7px;
  left: 8px;
}

.main-content .card-circle-3 {
  position: relative;
  width: 12.89px;
  height: 12.89px;
  margin-right: -8.66px;
}

.main-content .card-wrapper-4 {
  top: 0;
  left: -129px;
  display: flex;
  width: 195px;
  height: 40px;
  align-items: center;
  gap: 10.76px;
  position: absolute;
}

.main-content .header-3 {
  width: 55px;
  height: 14px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 1.35px;
  position: relative;
}

.main-content .text-wrapper-7 {
  width: fit-content;
  margin-top: -1.23px;
  margin-right: -15px;
  font-family: var(--small-bold-font-family);
  font-weight: var(--small-bold-font-weight);
  font-size: var(--small-bold-font-size);
  white-space: nowrap;
  position: relative;
  color: var(--black-1);
  letter-spacing: var(--small-bold-letter-spacing);
  line-height: var(--small-bold-line-height);
  font-style: var(--small-bold-font-style);
}
</style>