package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.Conversation;
import com.example.qihuangserver.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    // 创建对话
    @PostMapping
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) {
        Conversation savedConversation = conversationService.saveConversation(conversation);
        return new ResponseEntity<>(savedConversation, HttpStatus.CREATED);
    }

    // 根据ID获取对话
    @GetMapping("/{convId}")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long convId) {
        return ResponseEntity.ok(conversationService.getConversationById(convId));
    }

    // 获取所有对话
    @GetMapping
    public ResponseEntity<List<Conversation>> getAllConversations() {
        return ResponseEntity.ok(conversationService.getAllConversations());
    }

    // 删除对话
    @DeleteMapping("/{convId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long convId) {
        conversationService.deleteConversation(convId);
        return ResponseEntity.noContent().build();
    }
}