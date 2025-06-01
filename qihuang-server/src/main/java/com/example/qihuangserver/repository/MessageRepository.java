// MessageRepository.java
package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Conversation;
import com.example.qihuangserver.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 根据对话ID查询消息
    List<Message> findByConv_Id(Long convId);

    // 根据父消息ID查询子消息
    List<Message> findByParentMsgId(Long parentMsgId);

    List<Message> findByConvOrderByCreatedAtAsc(Conversation conversation);
    List<Message> findByConv_IdOrderByCreatedAtAsc(Long convId);
    void deleteByConv_Id(Long convId);

    @Modifying
    @Query(value = "DELETE FROM messages WHERE id = " +
            "(SELECT m.id FROM messages m WHERE m.conv_id = :convId AND m.role = 'assistant' " +
            "ORDER BY m.created_at DESC LIMIT 1)",
            nativeQuery = true)
    void deleteLastAssistantMessage(@Param("convId") Long convId);
    List<Message> findByConvIdOrderByCreatedAtAsc(Long convId);

    @Query("SELECT m FROM Message m " +
            "LEFT JOIN FETCH m.conv " +
            "LEFT JOIN m.parentMsg " +  
            "WHERE m.conv.id = :convId " +
            "ORDER BY m.createdAt")
    List<Message> findByConvId(@Param("convId") Long convId);


    @Modifying
    @Query("DELETE FROM Message m WHERE m.conv.id = :convId")
    void deleteByConvId(@Param("convId") Long convId);
}
