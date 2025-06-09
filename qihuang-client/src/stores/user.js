// import { defineStore } from "pinia";
// import { ref } from 'vue'
// import { loginAPI } from "@/apis/users";

// export const useUserStore = defineStore('user', () => {
//   const userInfo = ref({accessToken:''})
//   const getUserInfo = async({username,password})=>{
//     const res = await loginAPI({username,password});
//     if(res.data != null)userInfo.value = res.data;//可能需要修改
//     else userInfo.value = {accessToken:''};
//     console.log(res.data);
//     return res;
//   }
//   const logout = () => {
//     userInfo.value = {};
//   }
//   return {
//     userInfo,
//     getUserInfo,
//     logout
//   }

// },{
//   persist: {
//     enabled: true,
//     strategies: [
//       {
//         key: 'user',
//         storage: localStorage,
//       },
//     ],
//   },
// })


import { defineStore } from 'pinia'
import { registerAPI, loginAPI } from '@/apis/user-apis'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    loading: false,
    error: null
  }),

  actions: {
    async register(registerData) {
      this.loading = true
      this.error = null

      try {
        const res = await registerAPI(registerData)

        // 根据您的ApiResponse结构调整
        if (res.success) {
          return { success: true, data: res.data }
        } else {
          this.error = res.message || '注册失败'
          return { success: false }
        }
      } catch (error) {
        this.error = error.response?.data?.message ||
                    error.message ||
                    '注册请求失败'
        return { success: false }
      } finally {
        this.loading = false
      }
    },

    async login(loginData) {
      // 登录方法实现...
    }
  }
})
