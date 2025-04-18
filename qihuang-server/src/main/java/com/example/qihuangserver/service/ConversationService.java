package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Conversation;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;

    // 创建或更新对话
    @Transactional
    public Conversation saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    // 根据对话ID获取对话
    public Conversation getConversationById(Long convId) {
        return conversationRepository.findById(convId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    // 获取所有对话
    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    // 删除对话
    @Transactional
    public void deleteConversation(Long convId) {
        conversationRepository.deleteById(convId);
    }

    // 创建新的对话
    private Conversation createNewConversation() {
        Conversation conversation = new Conversation();
        User user = new User();
        Long userId = 100000005L;
        user.setUserId(userId);  // 固定的 user_id
        user.setUsername("pyaaa");  // 固定的 username
        conversation.setUser(user);
        conversation.setTitle("新对话");
        conversation.setCreatedAt(Instant.now());
        conversation.setUpdatedAt(Instant.now());
        return conversationRepository.save(conversation); // 新建对话并保存
    }
}