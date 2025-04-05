import { defineStore } from "pinia";
import { ref } from 'vue'
import { loginAPI } from "@/apis/user";

export const useUserStore = defineStore('user', () => {
  const userInfo = ref({accessToken:''})
  const getUserInfo = async({username,password})=>{
    const res = await loginAPI({username,password});
    if(res.data != null)userInfo.value = res.data;//可能需要修改
    else userInfo.value = {accessToken:''};
    console.log(res.data);
    return res;
  }
  const logout = () => {
    userInfo.value = {};
  }
  return {
    userInfo,
    getUserInfo,
    logout
  }

},{
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'user',
        storage: localStorage,
      },
    ],
  },
})