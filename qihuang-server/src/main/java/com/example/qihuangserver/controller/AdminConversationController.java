package com.example.qihuangserver.controller;


import com.example.qihuangserver.dto.zhongyizhishiwenda.ConversationDTO;
import com.example.qihuangserver.dto.zhongyizhishiwenda.MessageDTO;
import com.example.qihuangserver.model.Conversation;
import com.example.qihuangserver.model.Message;
import com.example.qihuangserver.service.AdminConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4000")
@RequestMapping("/api/admin/conversations")
public class AdminConversationController {

    private final AdminConversationService adminConversationService;

    public AdminConversationController(AdminConversationService adminConversationService) {
        this.adminConversationService = adminConversationService;
    }

    @GetMapping
    public ResponseEntity<?> getAllConversations() {
        try {
            List<Conversation> conversations = adminConversationService.getAllConversations();
            List<ConversationDTO> dtos = conversations.stream().map(conv -> {
                ConversationDTO dto = new ConversationDTO();
                dto.setId(conv.getId());
                dto.setTitle(conv.getTitle());
                dto.setCreatedAt(conv.getCreatedAt());
                if(conv.getUser() != null) {
                    dto.setUserId(conv.getUser().getUserId());
                    dto.setUsername(conv.getUser().getUsername());
                }
                dto.setMessageCount(conv.getMessages() != null ? conv.getMessages().size() : 0);
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {

        }
        return null;
    }

    @GetMapping("/user/{userId}")
    public List<Conversation> getConversationsByUserId(@PathVariable Long userId) {
        return adminConversationService.getConversationsByUserId(userId);
    }

    @GetMapping("/{convId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessagesByConversationId(@PathVariable Long convId) {
        List<Message> messages = adminConversationService.getMessagesByConversationId(convId);

        List<MessageDTO> dtos = messages.stream().map(msg -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(msg.getId());
            dto.setRole(msg.getRole());
            dto.setContent(msg.getContent());
            dto.setContentType(msg.getContentType());
            dto.setLang(msg.getLang());
            dto.setCreatedAt(msg.getCreatedAt());
            if(msg.getParentMsg() != null) {
                dto.setParentMsgId(msg.getParentMsg().getId());
            }
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchConversations(
            @RequestParam(required = false) Long convId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long userId) {

        List<Conversation> conversations = adminConversationService.searchConversations(convId, title, userId);

        List<Map<String, Object>> result = conversations.stream().map(conv -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", conv.getId());
            dto.put("title", conv.getTitle());
            dto.put("createdAt", conv.getCreatedAt());  // 确保返回时间字段

            if (conv.getUser() != null) {
                Map<String, Object> user = new HashMap<>();
                user.put("userId", conv.getUser().getUserId());
                user.put("username", conv.getUser().getUsername());
                dto.put("user", user);
            }
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{convId}")
    public void deleteConversation(@PathVariable Long convId) {
        adminConversationService.deleteConversation(convId);
    }

    @DeleteMapping("/messages/{msgId}")
    public void deleteMessage(@PathVariable Long msgId) {
        adminConversationService.deleteMessage(msgId);
    }
}