package com.example.qihuangserver.dto.zhongyizhishiwenda;

import java.time.Instant;
import java.time.LocalDateTime;

public class MessageDTO {
    private String role;
    private String content;
    private Instant createdAt;


    public MessageDTO(String role, String content, Instant createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
// Getters and Setters
}
