package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.QaMessage;
import com.example.qihuangserver.model.QaSession;
import com.example.qihuangserver.repository.QaMessageRepository;
import com.example.qihuangserver.repository.QaSessionRepository;
import com.example.qihuangserver.repository.ClassicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// QaService.java
@Service
@RequiredArgsConstructor
public class QaService {
    private final QaSessionRepository sessionRepository;
    private final QaMessageRepository messageRepository;
    private final AIService aiService;
    private final ClassicService classicService;

    @Transactional
    public QaSession createNewSession(Long userId, Long classicId) {
        Classic classic = classicService.getClassicById(classicId);

        QaSession session = new QaSession();
        session.setUserId(userId);
        session.setClassic(classic);
        session.setCreatedAt(Instant.now());
        session.setUpdatedAt(Instant.now());

        // 生成初始标题
        String titlePrompt = "请为关于《" + classic.getTitle() + "》的对话生成一个简短的标题";
        String generatedTitle = aiService.getAIResponse(titlePrompt);
        session.setTitle(generatedTitle.length() > 50 ? generatedTitle.substring(0, 50) : generatedTitle);

        return sessionRepository.save(session);
    }

    @Transactional
    public QaMessage addMessageToSession(Long sessionId, String content, String role, Long parentId) {
        QaSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        QaMessage message = new QaMessage();
        message.setSession(session);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(Instant.now());
        message.setIsCurrent(true);

        if (parentId != null) {
            QaMessage parent = messageRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent message not found"));
            message.setParent(parent);
            parent.getQaMessages().add(message);
            parent.setIsCurrent(false); // 旧回答不再标记为当前
        }

        return messageRepository.save(message);
    }

    public List<QaSession> getUserSessions(Long userId, Long classicId) {
        return sessionRepository.findByUserIdAndClassicId(userId, classicId);
    }

    @Transactional
    public QaMessage regenerateResponse(Long messageId) {
        QaMessage original = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        String improvedContent = aiService.getEnhancedResponse(original);
        return addMessageToSession(
                original.getSession().getId(),
                improvedContent,
                "assistant",
                original.getId()
        );
    }

    public void saveFeedback(Long messageId, String feedback) {
        QaMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setFeedback(feedback);
        messageRepository.save(message);
    }
}