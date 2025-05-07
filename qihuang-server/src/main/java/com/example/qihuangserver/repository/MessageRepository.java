// MessageRepository.java
package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Conversation;
import com.example.qihuangserver.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 根据对话ID查询消息
    List<Message> findByConv_Id(Long convId);

    // 根据父消息ID查询子消息
    List<Message> findByParentMsgId(Long parentMsgId);

    List<Message> findByConvOrderByCreatedAtAsc(Conversation conversation);
    List<Message> findByConv_IdOrderByCreatedAtAsc(Long convId);

}
