package com.example.qihuangserver.service;

import com.example.qihuangserver.model.AdminQaSessionDTO;
import com.example.qihuangserver.model.QaMessage;
import com.example.qihuangserver.model.QaSession;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.QaSessionRepository;
import com.example.qihuangserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.qihuangserver.repository.QaMessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminQaService {
    private final QaSessionRepository qaSessionRepository;
    private final UserRepository userRepository;
    private final QaMessageRepository qaMessageRepository;

    public Page<AdminQaSessionDTO> getAllSessions(Long userId, Long classicId, int page, int size) {
        Page<QaSession> sessions = qaSessionRepository.findAdminSessions(
                userId,
                classicId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        return sessions.map(session -> {
            AdminQaSessionDTO dto = new AdminQaSessionDTO();
            dto.setSessionId(session.getId());
            dto.setUserId(session.getUserId());

            User user = userRepository.findById(session.getUserId()).orElse(null);
            dto.setUserName(user != null ? user.getUsername() : "已删除用户");

            dto.setClassicId(session.getClassic().getId());
            dto.setClassicTitle(session.getClassic().getTitle());
            dto.setSessionTitle(session.getTitle());
            dto.setCreatedAt(session.getCreatedAt());
            return dto;
        });
    }

    public void deleteSession(Long id) {
        qaSessionRepository.deleteById(id);
    }

    // 新增方法：获取会话消息
    public List<QaMessage> getMessagesBySessionId(Long sessionId) {
        return qaMessageRepository.findBySession_Id(sessionId);
    }

    // 新增方法：删除消息
    public void deleteMessage(Long messageId) {
        qaMessageRepository.deleteById(messageId);
    }
}