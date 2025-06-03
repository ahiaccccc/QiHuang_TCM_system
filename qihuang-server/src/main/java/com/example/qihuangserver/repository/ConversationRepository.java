package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // 正确的查询方法
    Optional<Conversation> findFirstByUser_UserIdOrderByUpdatedAtDesc(Long userId);
    List<Conversation> findByUser_UserIdOrderByUpdatedAtDesc(Long userId);


    @Query("SELECT DISTINCT c FROM Conversation c " +
            "LEFT JOIN FETCH c.user u " +
            "LEFT JOIN FETCH c.messages m " +
            "ORDER BY c.createdAt DESC")
    List<Conversation> findAllWithUser();

    List<Conversation> findByUserUserId(Long userId);

    @Query("SELECT DISTINCT c FROM Conversation c " +
            "LEFT JOIN c.user u " +
            "WHERE (:convId IS NULL OR c.id = :convId) " +
            "AND (:title IS NULL OR c.title LIKE %:title%) " +
            "AND (:userId IS NULL OR u.userId = :userId) " +
            "ORDER BY c.createdAt DESC")
    List<Conversation> searchConversations(
            @Param("convId") Long convId,
            @Param("title") String title,
            @Param("userId") Long userId);

}