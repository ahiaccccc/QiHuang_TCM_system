package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // 正确的查询方法
    Optional<Conversation> findFirstByUser_UserIdOrderByUpdatedAtDesc(Long userId);
    List<Conversation> findByUser_UserIdOrderByUpdatedAtDesc(Long userId);

}