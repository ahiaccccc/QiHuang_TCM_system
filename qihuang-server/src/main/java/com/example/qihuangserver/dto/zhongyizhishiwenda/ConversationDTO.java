package com.example.qihuangserver.dto.zhongyizhishiwenda;

import com.example.qihuangserver.model.Conversation;

import java.time.Instant;
import java.util.List;

public class ConversationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String username;
    private Instant createdAt;
    private Instant updatedAt;
    private int messageCount;


    public ConversationDTO(Long id, Long userId, String title,Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public ConversationDTO(Long id, Long userId, String title) {
        this.id = id;
        this.userId = userId;
        this.title = title;
    }
    public ConversationDTO(){}

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
// Getters and Setters
}

