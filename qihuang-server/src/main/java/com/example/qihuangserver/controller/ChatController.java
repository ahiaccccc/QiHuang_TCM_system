package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.zhongyizhishiwenda.ConversationDTO;
import com.example.qihuangserver.dto.zhongyizhishiwenda.MessageDTO;
import com.example.qihuangserver.model.ChatRequest;
import com.example.qihuangserver.model.Conversation;
import com.example.qihuangserver.model.Message;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.ConversationRepository;
import com.example.qihuangserver.repository.MessageRepository;
import com.example.qihuangserver.repository.UserRepository;
import com.example.qihuangserver.service.CollectedService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    private final String API_URL = "http://10.2.8.77:3000/v1/chat/completions";
    private final String API_KEY = "sk-93nWYhI8SrnXad5m9932CeBdDeDf4233B21d93D217095f22";
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(@RequestParam Long userId) {
        List<Conversation> conversations = conversationRepository
                .findByUser_UserIdOrderByUpdatedAtDesc(userId);

        // 打印每个会话的详细信息，看看数据是否正确
        for (Conversation conversation : conversations) {
            System.out.println("Conversation ID: " + conversation.getId() + ", Title: " + conversation.getTitle()+conversation.getUser().getUserId());
        }


        List<ConversationDTO> conversationDTOs = conversations.stream()
                .map(c -> new ConversationDTO(c.getId(), c.getUser().getUserId(),c.getTitle()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(conversationDTOs);
    }



    @GetMapping("/conversations/{convId}")
    public ResponseEntity<?> getMessagesByConversation(@PathVariable Long convId) {
        List<MessageDTO> messages = messageRepository.findByConv_IdOrderByCreatedAtAsc(convId)
                .stream()
                .map(msg -> new MessageDTO(
                        msg.getRole(),
                        msg.getContent(),
                        msg.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(messages);
    }




    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        logger.log(Level.INFO, "Request Messages: " + request.getMessages());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "DeepSeek-R1");
        payload.put("messages", request.getMessages());
        payload.put("temperature", 0.7);

        logger.log(Level.INFO, "Payload: " + payload);
        logger.log(Level.INFO, "Headers: " + headers);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // 解析返回 JSON 并提取内容
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(response.getBody());

                String reply = json.get("choices").get(0).get("message").get("content").asText();

                Conversation conversation = null;
                Long userId = 100000005L;
                String username = "pyaaa";

                // 查询用户最近一次的对话
                conversation = conversationRepository.findFirstByUser_UserIdOrderByUpdatedAtDesc(userId).orElse(null);

                // 提取用户的第一条消息作为标题
                String conversationTitle = null;
                if (!request.getMessages().isEmpty()) {
                    Map<String, String> lastUserMsg = request.getMessages().get(request.getMessages().size() - 1);
                    conversationTitle = lastUserMsg.get("content").substring(0, Math.min(10, lastUserMsg.get("content").length()));
                }

                // 如果没有对话，或者最后更新时间超过30分钟，就新建对话
                Instant now = Instant.now();
                if (Boolean.TRUE.equals(request.getForceNew())) {
                    conversation = null; // 让它强制走新建逻辑
                }
                if (conversation == null || conversation.getUpdatedAt().isBefore(now.minusSeconds(1800))) {
                    User user = new User();
                    user.setUserId(userId);
                    user.setUsername(username);

                    conversation = new Conversation();
                    conversation.setUser(user);
                    conversation.setTitle(conversationTitle != null ? conversationTitle : "新对话"); //使用提取的标题
                    conversation.setCreatedAt(now);
                    conversation.setUpdatedAt(now);
                    conversation = conversationRepository.save(conversation);
                } else {
                    // 有效对话，更新 updatedAt
                    conversation.setUpdatedAt(now);
                    conversation = conversationRepository.save(conversation);
                }

                // 保存用户消息
                Map<String, String> lastUserMsg = request.getMessages().get(request.getMessages().size() - 1);
                Message userMsg = new Message();
                userMsg.setConv(conversation);
                userMsg.setRole("user");
                userMsg.setContent(lastUserMsg.get("content"));
                userMsg.setContentType("text");
                userMsg.setLang("zh");
                userMsg.setCreatedAt(Instant.now());
                messageRepository.save(userMsg);

                // 保存 AI 回复
                Message aiMsg = new Message();
                aiMsg.setConv(conversation);
                aiMsg.setRole("assistant");
                aiMsg.setContent(reply);
                aiMsg.setContentType("text");
                aiMsg.setLang("zh");
                aiMsg.setCreatedAt(Instant.now());
                messageRepository.save(aiMsg);

                logger.log(Level.INFO, "Assistant Reply: " + reply);
                return ResponseEntity.ok(reply);  // 直接返回文本内容

            } else {
                logger.log(Level.WARNING, "Unexpected response status: " + response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (ResourceAccessException e) {
            logger.log(Level.SEVERE, "Failed to access the API: " + e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to access the API: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred: " + e.getMessage(), e);
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }
}
