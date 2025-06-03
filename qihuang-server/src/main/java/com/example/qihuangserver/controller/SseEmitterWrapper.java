package com.example.qihuangserver.controller;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SseEmitterWrapper {
    private final SseEmitter emitter;
    private volatile boolean completed = false;

    public SseEmitterWrapper(SseEmitter emitter) {
        this.emitter = emitter;
        this.emitter.onCompletion(() -> completed = true);
        this.emitter.onTimeout(() -> completed = true);
    }

    public boolean isCompleted() {
        return completed;
    }

    public SseEmitter getEmitter() {
        return emitter;
    }
}