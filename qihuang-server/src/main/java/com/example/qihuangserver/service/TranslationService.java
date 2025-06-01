package com.example.qihuangserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TranslationService {
    private static final Logger logger = Logger.getLogger(TranslationService.class.getName());

    private final String TRANSLATE_API_URL = "http://localhost:5002/translate"; // 你的Flask翻译API地址

    @Autowired
    private RestTemplate restTemplate;

    public String translateText(String text, String fromLang, String toLang) {
        try {
            // 构建请求参数
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(TRANSLATE_API_URL)
                    .queryParam("text", text)
                    .queryParam("from", fromLang)
                    .queryParam("to", toLang);

            // 发送请求
            ResponseEntity<String> response = restTemplate.getForEntity(
                    builder.toUriString(),
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                return root.path("trans_result").path(0).path("dst").asText();
            } else {
                logger.warning("翻译API请求失败: " + response.getStatusCode());
                return text; // 返回原文
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "翻译过程中出错", e);
            return text; // 返回原文
        }
    }
}