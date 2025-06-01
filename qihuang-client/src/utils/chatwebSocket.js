class ChatWebSocket {
  constructor(url) {
    this.socket = null;
    this.url = url;
    this.messageHandlers = [];
    this.errorHandlers = [];
    this.openHandlers = [];
    this.closeHandlers = [];
  }

  connect() {
    this.socket = new WebSocket(this.url);

    this.socket.onmessage = (event) => {
      this.messageHandlers.forEach(handler => handler(event.data));
    };

    this.socket.onerror = (error) => {
      this.errorHandlers.forEach(handler => handler(error));
    };

    this.socket.onopen = () => {
      this.openHandlers.forEach(handler => handler());
    };

    this.socket.onclose = () => {
      this.closeHandlers.forEach(handler => handler());
    };
  }

  send(message) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message));
    }
  }

  close() {
    if (this.socket) {
      this.socket.close();
    }
  }

  onMessage(handler) {
    this.messageHandlers.push(handler);
  }

  onError(handler) {
    this.errorHandlers.push(handler);
  }

  onOpen(handler) {
    this.openHandlers.push(handler);
  }

  onClose(handler) {
    this.closeHandlers.push(handler);
  }
}

export default ChatWebSocket;
