package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Book;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QaService {
    private final QaSessionRepository sessionRepository;
    private final QaMessageRepository messageRepository;
    private final AIService aiService;
    private final ClassicService classicService;

    @Transactional
    public QaSession createNewSession(Long userId, Long classicId, String firstQuestion) {
        Classic classic = classicService.getClassicById(classicId);

        QaSession session = new QaSession();
        session.setUserId(userId);
        session.setClassic(classic);
        session.setCreatedAt(Instant.now());
        session.setUpdatedAt(Instant.now());

        // 强化标题生成逻辑
        String cleanQuestion = firstQuestion
                .replaceAll("[《》\"“”()（）\\?？]", "") // 移除标点
                .replaceAll("\\s+", " ") // 合并空格
                .trim();

        String titlePrompt = "严格生成6-10字纯中文标题，要求：\n"
                + "1. 基于问题核心：" + cleanQuestion + "\n"
                + "2. 禁止符号、序号、引号"
                + "3.不要回复任何除标题外的其他内容"
                + "4.think部分后面只返回标题"
                + "5.基于典籍《"+classic.getTitle()+"》的相关内容";

        try {
            String rawTitle = aiService.getAIResponse(titlePrompt);

            rawTitle=extractThinkContent(rawTitle);
            // 二次清洗处理
            String finalTitle = rawTitle.replaceAll("[^\\u4e00-\\u9fa5]", "")
                    .substring(0, Math.min(rawTitle.length(), 8));



            session.setTitle(finalTitle.isEmpty() ? "典籍问答" : finalTitle);
        } catch (Exception e) {
            session.setTitle(generateFallbackTitle(cleanQuestion));
        }

        return sessionRepository.save(session);
    }
    // 提取<think>后的内容
    private String extractThinkContent(String input) {
        if (input == null) return "未命名对话";
        int thinkIndex = input.indexOf("</think>");
        return (thinkIndex != -1) ?
                input.substring(thinkIndex + 10).trim() : // 提取</think>后的内容
                input.replaceAll("<.*?>", "").trim();    // 清除所有HTML标签
    }

    @Transactional
    public QaMessage createMessageWithSession(Long userId, Long classicId, String content) {
        // 自动创建会话
        QaSession session = createNewSession(userId, classicId, content);

        // 创建用户消息
        QaMessage userMessage = addMessageToSession(session.getId(), content, "user", null);

        // 生成AI回复
        String aiResponse = aiService.getAIResponse(content);
        QaMessage aiMessage = addMessageToSession(session.getId(), aiResponse, "assistant", userMessage.getId());

        return aiMessage;
    }

    @Transactional
    public QaMessage addMessageToSession(Long sessionId, String content, String role, Long parentId) {
        QaSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 保存用户消息
        QaMessage userMessage = saveMessage(session, content, role, parentId);

        // 如果是用户消息则生成AI回复
        if ("user".equals(role)) {
            String aiResponse = aiService.getAIResponse(content);
            saveMessage(session, aiResponse, "assistant", userMessage.getId());
        }

        return userMessage;
    }

    private QaMessage saveMessage(QaSession session, String content, String role, Long parentId) {
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
            parent.setIsCurrent(false);
            messageRepository.save(parent);
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

        // 验证：只能重新生成AI消息
        if (!"assistant".equals(original.getRole())) {
            throw new IllegalArgumentException("只能重新生成AI消息");
        }

        // 生成新的回复
        String improvedContent = aiService.getEnhancedResponse(original);

        // 更新原消息内容
        original.setContent(improvedContent);

        // 重置反馈状态
        original.setFeedback(null);

        // 删除所有后续消息（子消息）
        deleteChildrenMessages(original);

        return messageRepository.save(original);
    }

    // 新增辅助方法：递归删除子消息
    private void deleteChildrenMessages(QaMessage message) {
        List<QaMessage> children = new ArrayList<>(message.getQaMessages());
        for (QaMessage child : children) {
            deleteChildrenMessages(child);
            messageRepository.delete(child);
        }
        message.getQaMessages().clear();
    }



    public List<QaMessage> getMessagesBySessionId(Long sessionId) {
        return messageRepository.findBySession_Id(sessionId);
    }

    public void saveFeedback(Long messageId, String feedback) {
        QaMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // 添加验证
        if (!Arrays.asList("good", "bad", "neutral").contains(feedback)) {
            throw new IllegalArgumentException("Invalid feedback value");
        }

        message.setFeedback(feedback);
        messageRepository.save(message);
    }

    // 会话删除方法
    @Transactional
    public void deleteSession(Long sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    // 消息删除方法
    @Transactional
    public void deleteMessage(Long messageId) {
        QaMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        messageRepository.delete(message);
    }

    // 消息修改方法
    @Transactional
    public QaMessage updateMessage(Long messageId, String newContent) {
        QaMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!"user".equals(message.getRole())) {
            throw new IllegalArgumentException("只能修改用户消息");
        }

        // 清除关联的后续对话
        message.getQaMessages().clear();
        message.setContent(newContent);
        message.setIsCurrent(true);

        // 生成新的AI回答
        String aiResponse = aiService.getAIResponse(newContent);
        QaMessage aiMessage = new QaMessage();
        aiMessage.setSession(message.getSession());
        aiMessage.setRole("assistant");
        aiMessage.setContent(aiResponse);
        aiMessage.setParent(message);
        aiMessage.setCreatedAt(Instant.now());

        messageRepository.save(message);
        return messageRepository.save(aiMessage);
    }


    private String generateFallbackTitle(String input) {
        // 提取前8个中文字符
        String clean = input.replaceAll("[^\\u4e00-\\u9fa5]", "");
        return clean.length() > 0 ?
                clean.substring(0, Math.min(clean.length(), 10)) :
                "典籍讨论";
    }

    // 会话名称修改
    @Transactional
    public QaSession renameSession(Long sessionId, String newTitle) {
        QaSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 简单验证新标题
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }

        // 清理标题（限制长度和去除特殊字符）
        String cleanTitle = newTitle.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "")
                .substring(0, Math.min(newTitle.length(), 20))
                .trim();

        session.setTitle(cleanTitle.isEmpty() ? "未命名对话" : cleanTitle);
        session.setUpdatedAt(Instant.now());

        return sessionRepository.save(session);
    }

    // 新增流式处理方法
public void streamResponse(Long sessionId, String content, String role, Long parentId, SseEmitter emitter) {
    try {
        QaSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 保存用户消息
        QaMessage userMessage = saveMessage(session, content, role, parentId);

        Classic classic = session.getClassic();
        Book book = classic.getBook();
        content= "请结合典籍"+book.getName()+"中的篇目"+classic.getTitle()+"回答以下问题：/n"+content;

        // 流式生成AI回复
        aiService.streamAIResponse(content, new AIService.StreamCallback() {
            final StringBuilder fullContent = new StringBuilder();
            final QaMessage[] aiMessage = new QaMessage[1];

            @Override
            public void onStart() throws IOException {
                aiMessage[0] = createMessageShell(session, "assistant", userMessage.getId());
                messageRepository.save(aiMessage[0]);  // 立即持久化

                // 先发送用户消息ID
                emitter.send(SseEmitter.event().name("USER_MSG").data(userMessage.getId()));

                // 再发送AI消息ID
                emitter.send(SseEmitter.event().name("START").data(aiMessage[0].getId()));
            }

            @Override
            public void onContent(String token) throws IOException {
                fullContent.append(token);
                emitter.send(SseEmitter.event().data(token));
            }

            @Override
            public void onComplete() throws IOException {
                aiMessage[0].setContent(fullContent.toString());
                messageRepository.save(aiMessage[0]);
                emitter.send(SseEmitter.event().name("COMPLETE"));
                emitter.complete();
            }

            @Override
            public void onError(Exception e) {
                emitter.completeWithError(e);
            }
        });
    } catch (Exception e) {
        emitter.completeWithError(e);
    }
}

    // 重新生成
    public void streamRegenerateResponse(Long messageId, SseEmitter emitter) {
        try {
            QaMessage original = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));

            // 验证：只能重新生成AI消息
            if (!"assistant".equals(original.getRole())) {
                throw new IllegalArgumentException("只能重新生成AI消息");
            }

            // 删除所有后续消息
            deleteChildrenMessages(original);

            // 重置原消息内容
            original.setContent("");
            original.setFeedback(null);
            messageRepository.save(original);

            // 生成改进提示（使用原消息的父消息）
            QaMessage parentMessage = original.getParent();
            String prompt = parentMessage != null ? parentMessage.getContent() : original.getContent();

            aiService.streamAIResponse(prompt, new AIService.StreamCallback() {
                final StringBuilder fullContent = new StringBuilder();

                @Override
                public void onStart() throws IOException {
                    // 发送原消息ID
                    emitter.send(SseEmitter.event().name("START").data(original.getId()));
                }

                @Override
                public void onContent(String token) throws IOException {
                    fullContent.append(token);
                    original.setContent(fullContent.toString());
                    emitter.send(SseEmitter.event().data(token));
                }

                @Override
                public void onComplete() throws IOException {
                    original.setContent(fullContent.toString());
                    messageRepository.save(original);
                    emitter.send(SseEmitter.event().name("COMPLETE"));
                    emitter.complete();
                }

                @Override
                public void onError(Exception e) {
                    original.setContent("重新生成失败: " + e.getMessage());
                    messageRepository.save(original);
                    emitter.completeWithError(e);
                }
            });
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private QaMessage createMessageShell(QaSession session, String role, Long parentId) {
        QaMessage message = new QaMessage();
        message.setSession(session);
        message.setRole(role);
        message.setContent(""); // 设置为空字符串而不是null
        message.setCreatedAt(Instant.now());
        message.setIsCurrent(true);

        if (parentId != null) {
            QaMessage parent = messageRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent message not found"));
            message.setParent(parent);
            parent.setIsCurrent(false);
            messageRepository.save(parent);
        }

        return messageRepository.save(message);
    }

    //流式修改问题回复
    public void streamEditResponse(Long messageId, String newContent, SseEmitter emitter) {
        try {
            QaMessage original = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));

            // 验证消息角色
            if (!"user".equals(original.getRole())) {
                throw new IllegalArgumentException("只能修改用户消息");
            }

            // 更新原始消息内容
            original.setContent(newContent);
            messageRepository.save(original);

            // 清除所有后续消息
            original.getQaMessages().forEach(msg -> msg.setIsCurrent(false));
            messageRepository.saveAll(original.getQaMessages());

            // 创建新消息外壳
            QaSession session = original.getSession();
            QaMessage newAiMessage = createMessageShell(session, "assistant", original.getId());

            // 流式生成新回复
            aiService.streamAIResponse(newContent, new AIService.StreamCallback() {
                final StringBuilder fullContent = new StringBuilder();

                @Override
                public void onStart() throws IOException {
                    emitter.send(SseEmitter.event().name("START").data(newAiMessage.getId()));
                }

                @Override
                public void onContent(String token) throws IOException {
                    fullContent.append(token);
                    emitter.send(SseEmitter.event().data(token));
                }

                @Override
                public void onComplete() throws IOException {
                    newAiMessage.setContent(fullContent.toString());
                    messageRepository.save(newAiMessage);
                    emitter.send(SseEmitter.event().name("COMPLETE"));
                    emitter.complete();
                }

                @Override
                public void onError(Exception e) {
                    emitter.completeWithError(e);
                }
            });

        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }
}