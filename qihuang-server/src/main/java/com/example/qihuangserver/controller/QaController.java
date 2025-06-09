package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.QaMessage;
import com.example.qihuangserver.model.QaSession;
import com.example.qihuangserver.service.QaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QaController {
    private final QaService qaService;

    @PostMapping("/sessions")
    public ResponseEntity<QaSession> createSession(
            @RequestParam Long userId,
            @RequestParam Long classicId,
            @RequestParam(required = false) String firstQuestion // 新增参数
    ) {
        return ResponseEntity.ok(qaService.createNewSession(userId, classicId, firstQuestion));
    }

//    @PostMapping("/messages")
//    public ResponseEntity<QaMessage> createMessageWithSession(
//            @RequestParam Long userId,
//            @RequestParam Long classicId,
//            @RequestParam(required = false) String content // 新增参数
//    ) {
//        return ResponseEntity.ok(qaService.createMessageWithSession(userId, classicId, content));
//    }

    @PostMapping("/messages")
    public ResponseEntity<QaMessage> sendMessage(
            @RequestBody MessageRequest request
    ) {
        return ResponseEntity.ok(qaService.addMessageToSession(
                request.sessionId(),
                request.content(),
                request.role(),
                request.parentId()
        ));
    }

    // 流式响应端点
    @PostMapping(value = "/stream-messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessage(
            @RequestBody MessageRequest request
    ) {
        SseEmitter emitter = new SseEmitter(60_000L);
        qaService.streamResponse(
                request.sessionId(),
                request.content(),
                request.role(),
                request.parentId(),
                emitter
        );
        return emitter;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<QaSession>> getSessions(
            @RequestParam Long userId,
            @RequestParam Long classicId
    ) {
        return ResponseEntity.ok(qaService.getUserSessions(userId, classicId));
    }

    @PostMapping("/regenerate")
    public ResponseEntity<QaMessage> regenerate(
            @RequestParam Long messageId
    ) {
        return ResponseEntity.ok(qaService.regenerateResponse(messageId));
    }

    @PostMapping("/feedback")
    public ResponseEntity<Void> submitFeedback(
            @RequestParam Long messageId,
            @RequestParam String feedback
    ) {
        qaService.saveFeedback(messageId, feedback);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/messages")
    public ResponseEntity<List<QaMessage>> getMessagesBySessionId(@RequestParam("sessionId") Long sessionId) {
        List<QaMessage> messages = qaService.getMessagesBySessionId(sessionId);
        return ResponseEntity.ok(messages);
    }

    // 会话删除端点
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        qaService.deleteSession(sessionId);
        return ResponseEntity.ok().build();
    }

    // 消息删除端点
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        qaService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }

    // 消息修改端点
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<QaMessage> updateMessage(
            @PathVariable Long messageId,
            @RequestBody UpdateMessageRequest request) {
        return ResponseEntity.ok(qaService.updateMessage(messageId, request.newContent()));
    }

    // DTO定义
    public record UpdateMessageRequest(String newContent) {}

    // 会话名称修改
    @PutMapping("/sessions/rename/{sessionId}")
    public ResponseEntity<QaSession> renameSession(
            @PathVariable Long sessionId,
            @RequestBody RenameSessionRequest request) {
        return ResponseEntity.ok(qaService.renameSession(sessionId, request.newTitle()));
    }

    // 添加DTO
    public record RenameSessionRequest(String newTitle) {}

    // 重新生成流式
    @PostMapping(value = "/stream-regenerate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRegenerate(@RequestBody RegenerateRequest request) {
        SseEmitter emitter = new SseEmitter(60_000L);
        qaService.streamRegenerateResponse(request.messageId(), emitter);
        return emitter;
    }

    public record RegenerateRequest(Long messageId) {}

    // 流式修改问题回复
    @PostMapping(value = "/stream-edit", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEditMessage(@RequestBody StreamEditRequest request) {
        SseEmitter emitter = new SseEmitter(60_000L);
        qaService.streamEditResponse(
                request.messageId(),
                request.newContent(),
                emitter
        );
        return emitter;
    }

    public record StreamEditRequest(Long messageId, String newContent) {}
    public record MessageRequest(
            Long sessionId,
            String content,
            String role,
            Long parentId
    ) {}
}