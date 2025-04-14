package com.example.qihuangserver.model;

import java.util.List;
import java.util.Map;

public class ChatRequest {
    private Long convId; // 当前对话ID（可选）
    private Long userId;
    private List<Map<String, String>> messages;
    private Boolean forceNew;

    public Boolean getForceNew() {
        return forceNew;
    }

    public void setForceNew(Boolean forceNew) {
        this.forceNew = forceNew;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
    }


    public Long getConvId() {
        return convId;
    }

    public void setConvId(Long convId) {
        this.convId = convId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
