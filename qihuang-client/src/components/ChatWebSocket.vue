<template>
  <div>
    <div v-for="(message, index) in messages" :key="index">
      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      messages: [], // 用来存储流式的消息
      socket: null,  // WebSocket 实例
    };
  },
  mounted() {
    this.initWebSocket();
  },
  methods: {
    initWebSocket() {
      // 创建 WebSocket 连接
      this.socket = new WebSocket('ws://localhost:8080/chat/message');

      // 监听消息事件
      this.socket.onmessage = (event) => {
        // 接收到流式消息
        this.messages.push(event.data); // 将消息追加到 messages 数组中
      };

      // WebSocket 连接建立成功
      this.socket.onopen = () => {
        console.log("WebSocket 连接已建立");
      };

      // WebSocket 连接关闭
      this.socket.onclose = () => {
        console.log("WebSocket 连接关闭");
      };
    },
    sendMessage(message) {
      if (this.socket) {
        this.socket.send(message); // 向后端发送消息
      }
    }
  }
};
</script>
