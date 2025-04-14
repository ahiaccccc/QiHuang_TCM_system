package com.example.qihuangserver.controller;

public record MessageRequest(
        Long sessionId,
        String content,
        String role,
        Long parentId
) {
}