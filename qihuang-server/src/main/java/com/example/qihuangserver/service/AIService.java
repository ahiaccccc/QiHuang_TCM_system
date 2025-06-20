package com.example.qihuangserver.service;

import com.example.qihuangserver.model.QaMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AIService {
    private static final String API_URL = "http://10.2.8.77:3000/v1/chat/completions";
    private static final String API_KEY = "sk-93nWYhI8SrnXad5m9932CeBdDeDf4233B21d93D217095f22";

    public String getAIResponse(String prompt) {
        // 创建带超时设置的 RestTemplate
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(50 * 1000);  // 连接超时 50秒
        factory.setReadTimeout(120 * 1000);    // 读取超时 120秒
        RestTemplate restTemplate = new RestTemplate(factory);
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

    // 流式处理接口
    public interface StreamCallback {
        void onStart() throws IOException;
        void onContent(String token) throws IOException;
        void onComplete() throws IOException;
        void onError(Exception e);
    }

    // 流式处理方法
    public void streamAIResponse(String prompt, StreamCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                callback.onStart();

                // 真实API调用示例（根据实际接口调整）
                String fullResponse = getAIResponse(prompt);

                // 模拟逐字输出
                for (int i = 0; i < fullResponse.length(); i++) {
                    Thread.sleep(30);
                    callback.onContent(fullResponse.substring(i, i+1));
                }

                callback.onComplete();
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                executor.shutdown();
            }
        });
    }
}
