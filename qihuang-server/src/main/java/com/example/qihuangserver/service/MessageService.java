// MessageService.java
package com.example.qihuangserver.service;


import com.example.qihuangserver.model.Message;
import com.example.qihuangserver.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    // 保存消息
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    // 根据ID查询消息
    public Message getMessageById(Long msgId) {
        return messageRepository.findById(msgId).orElse(null);
    }

    // 根据对话ID查询消息列表
    public List<Message> getMessagesByConversationId(Long convId) {
        return messageRepository.findByConv_Id(convId);
    }

    // 删除消息
    public void deleteMessage(Long msgId) {
        messageRepository.deleteById(msgId);
    }

    // 更新消息内容
    public Message updateMessageContent(Long msgId, String newContent) {
        Message message = messageRepository.findById(msgId).orElse(null);
        if (message != null) {
            message.setContent(newContent);
            return messageRepository.save(message);
        }
        return null;
    }
}