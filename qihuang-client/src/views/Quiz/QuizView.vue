<template>
  <div class="xuanzhuti">
    <img
      class="back"
      src="https://c.animaapp.com/mabzoynihMfxud/img/back.png"
    />
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
                v-for="(item, idx) in quizList"
                :key="item.id"
              >
                <div class="group">
                  <div class="overlap-group">
                    <div class="text-wrapper-3">{{ item.type }}</div>
                  </div>
                </div>
                <div class="text-wrapper-4">{{ item.title }}</div>
                <div class="button-blue">
                  <div class="text-wrapper-5">正式答题</div>
                </div>
                <div class="frame-4"></div>
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
    <div class="navigation">
      <div class="nav-container">
        <div class="left-column">
          <div class="logo">
            <img
              class="img"
              src="https://c.animaapp.com/mabzoynihMfxud/img/logo-1.png"
            />
            <div class="text-wrapper-10">岐黄慧问</div>
          </div>
          <div class="nav-links">
            <div class="nav-link">
              <div class="text-wrapper-11">AI问答</div>
            </div>
            <div class="nav-link-2">
              <div class="text-wrapper-12">典籍解读</div>
            </div>
            <div class="nav-link-2">
              <div class="text-wrapper-12">养生方案</div>
            </div>
            <div class="nav-link-2">
              <div class="text-wrapper-13">答题挑战</div>
            </div>
          </div>
        </div>
        <div class="right-column">
          <div class="icon-button"><img
              class="bell"
              src="https://c.animaapp.com/mabzoynihMfxud/img/bell.svg"
            /></div>
          <div class="icon-button">
            <img
              class="heart"
              src="https://c.animaapp.com/mabzoynihMfxud/img/heart.svg"
            />
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
            <img
              class="caret-down"
              src="https://c.animaapp.com/mabzoynihMfxud/img/caretdown.svg"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref } from 'vue'

const quizList = ref([
  {
    id: 1,
    type: '模拟测试',
    title: '答题主题一',
    rate: '90%',
    count: 1,
  },
  {
    id: 2,
    type: '模拟测试',
    title: '答题主题二',
    rate: '85%',
    count: 2,
  },
  {
    id: 3,
    type: '模拟测试',
    title: '答题主题三',
    rate: '92%',
    count: 3,
  },
  {
    id: 4,
    type: '模拟测试',
    title: '答题主题四',
    rate: '92%',
    count: 4,
  },
  {
    id: 5,
    type: '模拟测试',
    title: '答题主题五',
    rate: '92%',
    count: 5,
  },
  {
    id: 6,
    type: '模拟测试',
    title: '答题主题六',
    rate: '92%',
    count: 6,
  },
  {
    id: 7,
    type: '模拟测试',
    title: '答题主题七',
    rate: '92%',
    count: 7,
  },
  {
    id: 8,
    type: '模拟测试',
    title: '答题主题八',
    rate: '92%',
    count: 8,
  },
  {
    id: 9,
    type: '模拟测试',
    title: '答题主题九',
    rate: '92%',
    count: 9,
  },
  {
    id: 10,
    type: '模拟测试',
    title: '答题主题十',
    rate: '92%',
    count: 10,
  },

  // ...可以继续添加更多主题
])
</script>

<style >
html,
body {
  margin: 0px;
  height: 100%;
}
.xuanzhuti {
  display: flex;
  flex-direction: column;
  /* height: 1600px; */
  align-items: flex-start;
  position: relative;
}

.xuanzhuti .back {
  position: relative;
  width: 100%;
  min-width: 1000px;
  height: 100vh;
}

.xuanzhuti .hero-section {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 53px;
  left: 0;
}

.xuanzhuti .main-area {
  position: relative;
  width: 100%;
  height: 100%;
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
  width: 100%;
  height: 25vh;
  top: 0;
  left: 0;
  background-color: #ffffff33;
}

.xuanzhuti .div {
  display: flex;
  flex-direction: column;
  width: 100%;
  /* height: 1324px; */
  align-items: center;
  /* gap: 34px; */
  /* padding: 40px; */
  position: absolute;
  top: 223px;
  left: 0;
  background-color: var(--danlan);
}

.xuanzhuti .div-wrapper {
  position: relative;
  flex: 0 0 auto;
  left: -30vw;
}

.xuanzhuti .p {
  /* position: absolute; */
  width: 148px;
  top: 3px;
  left: 10px;
  font-family: var(--susularge-bold-font-family);
  font-weight: var(--susularge-bold-font-weight);
  color: transparent;
  font-size: var(--susularge-bold-font-size);
  letter-spacing: var(--susularge-bold-letter-spacing);
  line-height: var(--susularge-bold-line-height);
  white-space: nowrap;
  font-style: var(--susularge-bold-font-style);
}

.xuanzhuti .text-wrapper {
  color: #50ac32;
  font-family: var(--susularge-bold-font-family);
  font-style: var(--susularge-bold-font-style);
  font-weight: var(--susularge-bold-font-weight);
  letter-spacing: var(--susularge-bold-letter-spacing);
  line-height: var(--susularge-bold-line-height);
  font-size: var(--susularge-bold-font-size);
}

.xuanzhuti .span {
  color: #298117;
  font-family: var(--susularge-bold-font-family);
  font-style: var(--susularge-bold-font-style);
  font-weight: var(--susularge-bold-font-weight);
  letter-spacing: var(--susularge-bold-letter-spacing);
  line-height: var(--susularge-bold-line-height);
  font-size: var(--susularge-bold-font-size);
}

.xuanzhuti .text-wrapper-2 {
  color: #000000;
  font-family: var(--susularge-bold-font-family);
  font-style: var(--susularge-bold-font-style);
  font-weight: var(--susularge-bold-font-weight);
  letter-spacing: var(--susularge-bold-letter-spacing);
  line-height: var(--susularge-bold-line-height);
  font-size: var(--susularge-bold-font-size);
}

.xuanzhuti .card-grid-image {
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

.xuanzhuti .frame-2 {
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
}

.xuanzhuti .frame-2::-webkit-scrollbar {
  width: 0;
  display: none;
}

.xuanzhuti .frame-3 {
  position: relative;
  width: 359px;
  height: 291px;
  background-color: #ffffff;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid;
  border-color: #c0c0c0;
}

.xuanzhuti .group {
  position: absolute;
  width: 114px;
  height: 35px;
  top: 230px;
  left: 41px;
}

.xuanzhuti .overlap-group {
  position: relative;
  width: 112px;
  height: 35px;
  background-color: #ffffff;
  border-radius: 5px;
  border: 1px solid;
  border-color: #f76b56;
}
.xuanzhuti .overlap-group:hover {
  background-color: #f76b56;
  /* color: #ffffff; */
  cursor: pointer;
  transition: background-color 0.1s ease;
}
.xuanzhuti .overlap-group:hover .text-wrapper-3 {
  color: #ffffff;
}

.xuanzhuti .text-wrapper-3 {
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

.xuanzhuti .text-wrapper-4 {
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

.xuanzhuti .button-blue {
  display: flex;
  width: 112px;
  height: 35px;
  align-items: center;
  justify-content: center;
  /* padding: 10.76px; */
  position: absolute;
  top: 230px;
  left: 205px;
  background-color: var(--zhonglv);
  border-radius: 8px;
}
.xuanzhuti .button-blue:hover {
  background-color: var(--heilan);
  cursor: pointer;
  transition: background-color 0.1s ease;
}
.xuanzhuti .text-wrapper-5 {
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

.xuanzhuti .frame-4 {
  position: absolute;
  width: 359px;
  height: 117px;
  top: 0;
  left: 0;
  background: url(https://c.animaapp.com/mabzoynihMfxud/img/frame-1437252860-8.png) 50% 50% / cover;
}

.xuanzhuti .frame-5 {
  position: absolute;
  width: 307px;
  height: 73px;
  top: 149px;
  left: 41px;
}

.xuanzhuti .frame-6 {
  display: flex;
  flex-direction: column;
  width: 51px;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 9px 0px;
  position: absolute;
  top: 0;
  left: 195px;
}

.xuanzhuti .text-wrapper-6 {
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

.xuanzhuti .text-wrapper-7 {
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

.xuanzhuti .frame-7 {
  display: flex;
  flex-direction: column;
  width: 75px;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 9px 0px;
  position: absolute;
  top: 3px;
  left: 25px;
}

.xuanzhuti .text-wrapper-8 {
  position: relative;
  width: 53px;
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

.xuanzhuti .text-wrapper-9 {
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

.xuanzhuti .frame-8 {
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

.xuanzhuti .navigation {
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

.xuanzhuti .img {
  position: relative;
  width: 27px;
  height: 25px;
  object-fit: cover;
}

.xuanzhuti .text-wrapper-10 {
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

.xuanzhuti .text-wrapper-11 {
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

.xuanzhuti .text-wrapper-12 {
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

.xuanzhuti .text-wrapper-13 {
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
  width: 10.77px;
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
  background-image: url(https://c.animaapp.com/mabzoynihMfxud/img/th-1.png);
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