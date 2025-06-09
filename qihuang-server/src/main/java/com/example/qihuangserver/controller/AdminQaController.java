package com.example.qihuangserver.controller;


import com.example.qihuangserver.model.AdminQaSessionDTO;
import com.example.qihuangserver.service.AdminQaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.qihuangserver.model.QaMessage;

import java.util.List;

@RestController
@RequestMapping("/api/admin/qa-sessions")
@RequiredArgsConstructor
public class AdminQaController {
    private final AdminQaService adminQaService;

    @GetMapping
    public ResponseEntity<Page<AdminQaSessionDTO>> getAllSessions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long classicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminQaService.getAllSessions(userId, classicId, page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        adminQaService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    // 新增获取会话消息接口
    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<List<QaMessage>> getSessionMessages(@PathVariable Long sessionId) {
        return ResponseEntity.ok(adminQaService.getMessagesBySessionId(sessionId));
    }

    // 新增删除消息接口
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        adminQaService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}