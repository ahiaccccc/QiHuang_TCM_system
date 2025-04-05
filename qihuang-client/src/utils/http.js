import axios from 'axios'

const httpInstance = axios.create({
  baseURL: 'https://api.example.com', // Replace with your API base URL
  timeout: 30000 ,
  // headers:{
  //     'Accept': 'application/json',
  //     'Content-Type': 'application/json'
  // }
})

httpInstance.interceptors.request.use(config => {
  // const userStore = useUserStore();
  // const token = userStore.userInfo.accessToken;
  // if(token){
      // config.headers.Authorization = `Bearer ${token}`
  // }
  return config
}, error => {
  return Promise.reject(error);
})

httpInstance.interceptors.response.use(res =>res.data,e=>{
  if(e.response.status === 401){
    // Handle 401 Unauthorized error, e.g., redirect to login page
    // const router = useRouter()
    // router.push({ name: 'login' })
    // localStorage.removeItem('token')
    // location.reload()
  }

  return Promise.reject(e)

})  

export default httpInstance

