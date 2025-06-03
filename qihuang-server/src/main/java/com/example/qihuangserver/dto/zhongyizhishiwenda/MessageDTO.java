package com.example.qihuangserver.dto.zhongyizhishiwenda;

import java.time.Instant;
import java.time.LocalDateTime;

public class MessageDTO {
    private String role;
    private String content;
    private Instant createdAt;
    private Long id;

    private String contentType;
    private String lang;

    private Long parentMsgId;

    public MessageDTO(String role, String content, Instant createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }
    public MessageDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Long getParentMsgId() {
        return parentMsgId;
    }

    public void setParentMsgId(Long parentMsgId) {
        this.parentMsgId = parentMsgId;
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

}
