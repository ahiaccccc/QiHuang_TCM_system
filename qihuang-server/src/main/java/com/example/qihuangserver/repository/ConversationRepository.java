package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findFirstByUserIdOrderByUpdatedAtDesc(Long userId);
    // 可自定义查询方法，例如按用户ID查找对话
    // List<Conversation> findByUser_UserId(String userId);
}