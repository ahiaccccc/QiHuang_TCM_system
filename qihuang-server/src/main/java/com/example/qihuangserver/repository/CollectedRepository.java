package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Collected;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CollectedRepository extends JpaRepository<Collected, Long> {
    Optional<Collected> findByUserAndClassic(User user, Classic classic);
    void deleteByUserAndClassic(User user, Classic classic);
    List<Collected> findByUser(User user);

    @Query("SELECT c FROM Collected c " +
            "WHERE (:userId IS NULL OR c.user.id = :userId) " +
            "AND (:classicId IS NULL OR c.classic.id = :classicId)")
    Page<Collected> findCollections(
            @Param("userId") Long userId,
            @Param("classicId") Long classicId,
            Pageable pageable
    );
}
