package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.CollectionsChat;
import com.example.qihuangserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionsChatRepository extends JpaRepository<CollectionsChat, Long> {
    List<CollectionsChat> findByUserOrderByCreatedAtDesc(User user);
    boolean existsByUserAndContent(User user, String content);
    @Query("SELECT c FROM CollectionsChat c WHERE c.user.userId = :userId AND c.id = :collectionId")
    List<CollectionsChat> findByCollectionIdAndUserId(
            @Param("collectionId") Long collectionId,
            @Param("userId") Long userId
    );

    }