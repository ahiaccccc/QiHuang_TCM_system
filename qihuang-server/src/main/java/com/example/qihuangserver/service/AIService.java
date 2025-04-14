package com.example.qihuangserver.service;

import com.example.qihuangserver.model.QaMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private static final String API_URL = "http://10.2.8.77:3000/v1/chat/completions";
    private static final String API_KEY = "sk-93nWYhI8SrnXad5m9932CeBdDeDf4233B21d93D217095f22";

    public String getAIResponse(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "DeepSeek-R1");
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                API_URL,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map firstChoice = (Map) ((List) response.getBody().get("choices")).get(0);
        Map message = (Map) firstChoice.get("message");
        return message.get("content").toString();
    }

    public String getEnhancedResponse(QaMessage original) {
        String prompt = "请改进以下回答内容：\n" + original.getContent();
        return getAIResponse(prompt);
    }
}
