package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Conversation;
import com.example.qihuangserver.model.Message;
import com.example.qihuangserver.repository.ConversationRepository;
import com.example.qihuangserver.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public AdminConversationService(ConversationRepository conversationRepository,
                                    MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAllWithUser();
    }

    public List<Conversation> getConversationsByUserId(Long userId) {
        return conversationRepository.findByUserUserId(userId);
    }

    public List<Message> getMessagesByConversationId(Long convId) {

        return messageRepository.findByConvId(convId);
    }

    // 查找对话
    public List<Conversation> searchConversations(Long convId, String title, Long userId) {
        return conversationRepository.searchConversations(convId, title, userId);
    }

    @Transactional
    public void deleteConversation(Long convId) {
        // 先删除关联的消息
        messageRepository.deleteByConvId(convId);
        // 再删除对话
        conversationRepository.deleteById(convId);
    }

    @Transactional
    public void deleteMessage(Long msgId) {
        messageRepository.deleteById(msgId);
    }
}