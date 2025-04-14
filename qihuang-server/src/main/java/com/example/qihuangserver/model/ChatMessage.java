package com.example.qihuangserver.model;

public class ChatMessage {
    private String role; // "user" or "system" or "assistant"
    private String content;

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

// getter 和 setter
}
