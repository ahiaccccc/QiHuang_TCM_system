package com.example.qihuangserver.websocket;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 从客户端接收到消息
        String clientMessage = message.getPayload();

        // 模拟生成流式的回答，可以根据需求替换为实际的AI回答生成逻辑
        String[] responseParts = generateStreamedResponse(clientMessage);

        // 每隔1秒发送一次消息到客户端
        for (String part : responseParts) {
            try {
                session.sendMessage(new TextMessage(part));
                Thread.sleep(1000); // 控制消息发送的时间间隔
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private String[] generateStreamedResponse(String messageContent) {
        // 将消息内容按空格拆分成多个部分来模拟流式回答
        return messageContent.split(" ");
    }
}
