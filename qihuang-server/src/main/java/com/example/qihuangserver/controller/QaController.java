package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.QaMessage;
import com.example.qihuangserver.model.QaSession;
import com.example.qihuangserver.service.QaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QaController {
    private final QaService qaService;

    @PostMapping("/sessions")
    public ResponseEntity<QaSession> createSession(
            @RequestParam Long userId,
            @RequestParam Long classicId
    ) {
        return ResponseEntity.ok(qaService.createNewSession(userId, classicId));
    }

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
}