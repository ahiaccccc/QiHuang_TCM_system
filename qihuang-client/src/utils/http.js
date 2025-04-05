import axios from 'axios'

const httpInstance = axios.create({
  baseURL: 'https://api.example.com', // Replace with your API base URL
  timeout: 30000 ,
  // headers:{
  //     'Accept': 'application/json',
  //     'Content-Type': 'application/json'
  // }
})

export default httpInstance