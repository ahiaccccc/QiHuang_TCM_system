package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.QaMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface QaMessageRepository extends JpaRepository<QaMessage, Long> {
    List<QaMessage> findBySessionIdOrderByCreatedAt(Long sessionId);
    List<QaMessage> findByParent(QaMessage parent);
}
