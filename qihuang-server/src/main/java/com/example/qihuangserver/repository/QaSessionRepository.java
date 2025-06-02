package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.QaSession;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface QaSessionRepository extends JpaRepository<QaSession, Long> {
    List<QaSession> findByUserIdAndClassicId(Long userId, Long classicId);
    List<QaSession> findByClassicId(Long classicId);
    @Query("SELECT s FROM QaSession s " +
            "WHERE (:userId IS NULL OR s.userId = :userId) " +
            "AND (:classicId IS NULL OR s.classic.id = :classicId)")
    Page<QaSession> findAdminSessions(
            @Param("userId") Long userId,
            @Param("classicId") Long classicId,
            Pageable pageable
    );
}