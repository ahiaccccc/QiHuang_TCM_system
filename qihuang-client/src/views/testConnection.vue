<template>
  <div>
    <button @click="testGet">测试GET请求</button>
    <button @click="testPost">测试POST请求</button>
    <div>响应结果：{{ response }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const response = ref('');

// GET测试
const testGet = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/test/hello');
    response.value = res.data;
  } catch (error) {
    response.value = '请求失败: ' + error.message;
  }
};

// POST测试
const testPost = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/test/echo', {
      message: 'Hello Vue!'
    });
    response.value = res.data;
  } catch (error) {
    response.value = '请求失败: ' + error.message;
  }
};
</script>