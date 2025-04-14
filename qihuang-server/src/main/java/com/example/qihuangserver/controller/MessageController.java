// MessageController.java
package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.Message;
import com.example.qihuangserver.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 创建消息
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        Message savedMessage = messageService.saveMessage(message);
        return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
    }

    // 根据ID获取消息
    @GetMapping("/{msgId}")
    public ResponseEntity<Message> getMessage(@PathVariable Long msgId) {
        Message message = messageService.getMessageById(msgId);
        return message != null ?
                ResponseEntity.ok(message) :
                ResponseEntity.notFound().build();
    }

    // 根据对话ID获取消息列表
    @GetMapping("/conversation/{convId}")
    public List<Message> getMessagesByConversation(@PathVariable Long convId) {
        return messageService.getMessagesByConversationId(convId);
    }

    // 删除消息
    @DeleteMapping("/{msgId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long msgId) {
        messageService.deleteMessage(msgId);
        return ResponseEntity.noContent().build();
    }

    // 更新消息内容
    @PutMapping("/{msgId}/content")
    public ResponseEntity<Message> updateContent(
            @PathVariable Long msgId,
            @RequestBody String newContent
    ) {
        Message updatedMessage = messageService.updateMessageContent(msgId, newContent);
        return updatedMessage != null ?
                ResponseEntity.ok(updatedMessage) :
                ResponseEntity.notFound().build();
    }
}