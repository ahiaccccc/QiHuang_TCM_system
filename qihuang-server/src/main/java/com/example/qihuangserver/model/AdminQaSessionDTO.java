package com.example.qihuangserver.model;

import lombok.Data;
import java.time.Instant;

@Data
public class AdminQaSessionDTO {
    private Long sessionId;
    private Long userId;
    private String userName;
    private Long classicId;
    private String classicTitle;
    private String sessionTitle;
    private Instant createdAt;
}