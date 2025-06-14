package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.zhongyizhishiwenda.ConversationDTO;
import com.example.qihuangserver.dto.zhongyizhishiwenda.MessageDTO;
import com.example.qihuangserver.model.*;
import com.example.qihuangserver.repository.AttachmentRepository;
import com.example.qihuangserver.repository.ConversationRepository;
import com.example.qihuangserver.repository.MessageRepository;
import com.example.qihuangserver.repository.UserRepository;
import com.example.qihuangserver.service.BaiduImageRecognitionService;
import com.example.qihuangserver.service.ConversationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "http://localhost:4000")
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageProperties fileStorageProperties;



    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    private final String API_URL = "http://10.2.8.77:3000/v1/chat/completions";
    private final String API_KEY = "sk-93nWYhI8SrnXad5m9932CeBdDeDf4233B21d93D217095f22";
    @Autowired
    private ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    //    public ChatController(SimpMessagingTemplate messagingTemplate1) {
//        this.messagingTemplate = messagingTemplate1;
//    }
    private final WebClient webClient;

    public ChatController(SimpMessagingTemplate messagingTemplate1) {
        this.messagingTemplate = messagingTemplate1;
        this.webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + API_KEY)
                .build();
    }



    @GetMapping("/translate")
    public ResponseEntity<?> translateText(
            @RequestParam String text,
            @RequestParam(defaultValue = "auto") String from,
            @RequestParam(defaultValue = "en") String to) {

        try {
            // 检查文本是否为空
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("翻译文本不能为空");
            }

            // 打印日志（调试用）
            System.out.println("原始文本: " + text);

            // 百度翻译API配置
            String appId = "20250530002370147"; // 替换为你的百度翻译APP ID
            String secretKey = "m2zbn51zlKrUByZoLe6K"; // 替换为你的百度翻译密钥
            String apiUrl = "https://fanyi-api.baidu.com/api/trans/vip/translate";

            // 生成随机数
            String salt = String.valueOf(System.currentTimeMillis());

            // 计算签名（appid+q+salt+密钥的MD5值）
            String signStr = appId + text + salt + secretKey;
            String sign = DigestUtils.md5DigestAsHex(signStr.getBytes(StandardCharsets.UTF_8));

            // 构建请求参数
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("q", text);
            params.add("from", from);
            params.add("to", to);
            params.add("appid", appId);
            params.add("salt", salt);
            params.add("sign", sign);

            // 配置RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // 发送POST请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            // 解析返回结果
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("翻译API请求失败");
            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("翻译失败: " + e.getMessage());
        }
    }

    //获取历史对话z
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(@RequestParam Long userId, HttpServletRequest request) {
        // 验证用户ID
        if (userId == null) {
            return ResponseEntity.badRequest().body("userId 不能为空");
        }

        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在");
        }

        logger.info("获取用户收藏列表，userId: " + userId);

        try {
            List<Conversation> conversations = conversationRepository
                    .findByUser_UserIdOrderByUpdatedAtDesc(userId);

            List<ConversationDTO> conversationDTOs = conversations.stream()
                    .map(c -> new ConversationDTO(c.getId(), c.getUser().getUserId(), c.getTitle()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(conversationDTOs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "获取会话列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取会话列表失败: " + e.getMessage());
        }
    }



    @GetMapping("/conversations/{convId}")
    @CrossOrigin(origins = {"http://localhost:4000", "http://127.0.0.1:4000"})
    public ResponseEntity<?> getMessagesByConversation(@PathVariable Long convId,HttpServletRequest request) {
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

    //用户修改标题
    @CrossOrigin(origins = "http://localhost:4000")
    @PutMapping("/conversations/{convId}/rename")
    @Transactional
    public ResponseEntity<?> updateConversationTitle(@PathVariable Long convId, @RequestBody Map<String, String> request) {
        String newTitle = request.get("title");

        return conversationRepository.findById(convId).map(conversation -> {
            conversation.setTitle(newTitle);
            conversation.setUpdatedAt(Instant.now());
            conversationRepository.save(conversation);
            return ResponseEntity.ok("标题更新成功");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("会话未找到"));
    }

    @CrossOrigin(origins = "http://localhost:4000")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> chat(
            @RequestParam("messages") String messagesJson,
            @RequestParam(value = "newConversation", defaultValue = "false") boolean newConversation,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        RestTemplate restTemplate = new RestTemplate();
        logger.log(Level.INFO, "Request Messages: " + messagesJson);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> messages;

        try {
            messages = mapper.readValue(messagesJson, new TypeReference<>() {});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid message format.");
        }

        try {
            if (file != null && !file.isEmpty()) {
                String fileContent;

                if (file.getContentType().startsWith("text/")) {
                    fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                } else if (file.getContentType().startsWith("image/")) {
                    fileContent = "[图片文件] 文件名：" + file.getOriginalFilename() + "，大小：" + file.getSize() + "字节";
                } else {
                    fileContent = "[文件] 文件名：" + file.getOriginalFilename() + "，类型：" + file.getContentType();
                }

                Map<String, String> fileMessage = new HashMap<>();
                fileMessage.put("role", "user");
                fileMessage.put("content", "上传文件内容：\n" + fileContent);
                messages.add(fileMessage);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "文件处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件处理失败");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "DeepSeek-R1");
        payload.put("messages", messages);
        payload.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode json = mapper.readTree(response.getBody());
                String reply = json.get("choices").get(0).get("message").get("content").asText();

                Long userId = 100000005L;
                String username = "pyaaa";
                Instant now = Instant.now();

                Conversation conversation = null;

                // 只有在明确要求新会话或没有当前会话时才创建新会话
                if (newConversation) {
                    User user = new User();
                    user.setUserId(userId);
                    user.setUsername(username);

                    String title = messages.get(messages.size() - 1).get("content");
                    title = title.length() > 10 ? title.substring(0, 10) : title;

                    conversation = new Conversation();
                    conversation.setUser(user);
                    conversation.setTitle(title);
                    conversation.setCreatedAt(now);
                    conversation.setUpdatedAt(now);
                    conversation = conversationRepository.save(conversation);
                } else {
                    // 否则使用最近的会话
                    conversation = conversationRepository.findFirstByUser_UserIdOrderByUpdatedAtDesc(userId).orElse(null);

                    if (conversation == null) {
                        // 如果没有会话，则创建一个新的
                        User user = new User();
                        user.setUserId(userId);
                        user.setUsername(username);

                        String title = messages.get(messages.size() - 1).get("content");
                        title = title.length() > 10 ? title.substring(0, 10) : title;

                        conversation = new Conversation();
                        conversation.setUser(user);
                        conversation.setTitle(title);
                        conversation.setCreatedAt(now);
                        conversation.setUpdatedAt(now);
                        conversation = conversationRepository.save(conversation);
                    } else {
                        // 更新现有会话时间
                        conversation.setUpdatedAt(now);
                        conversation = conversationRepository.save(conversation);
                    }
                }

                // 保存用户消息
                Map<String, String> lastUserMsg = messages.get(messages.size() - 1);
                Message userMsg = new Message();
                userMsg.setConv(conversation);
                userMsg.setRole("user");
                userMsg.setContent(lastUserMsg.get("content"));
                userMsg.setContentType("text");
                userMsg.setLang("zh");
                userMsg.setCreatedAt(Instant.now());
                userMsg = messageRepository.save(userMsg);

                // 处理文件上传
                if (file != null && !file.isEmpty()) {
                    String originalFilename = file.getOriginalFilename();
                    String fileType = file.getContentType();
                    long fileSize = file.getSize();

                    String uploadDir = "E:/小刘的桌面/项目实训/QiHuang_TCM_system-main/qihaung2025011402/QiHuang_TCM_system/qihuang-server/uploads/";
                    String thumbnailDir = uploadDir + "thumbnails/";

                    new File(uploadDir).mkdirs();
                    new File(thumbnailDir).mkdirs();

                    String uniqueName = System.currentTimeMillis() + "_" + originalFilename;
                    String filePath = uploadDir + uniqueName;
                    String thumbnailPath = thumbnailDir + "thumb_" + uniqueName;

                    File dest = new File(filePath);
                    file.transferTo(dest);

                    Attachment attachment = new Attachment();
                    attachment.setMsg(userMsg);
                    attachment.setFileType(fileType);
                    attachment.setFileUrl(filePath);
                    attachment.setFileSize((int) fileSize);
                    attachment.setCreatedAt(Instant.now());

                    if (fileType != null && fileType.startsWith("image/")) {
                        File thumbnailFile = new File(thumbnailPath);
                        Thumbnails.of(dest).size(200, 200).outputFormat("jpg").toFile(thumbnailFile);
                        attachment.setThumbnailUrl(thumbnailPath);
                    }

                    attachmentRepository.save(attachment);
                }

                // 保存AI回复
                Message aiMsg = new Message();
                aiMsg.setConv(conversation);
                aiMsg.setRole("assistant");
                aiMsg.setContent(reply);
                aiMsg.setContentType("text");
                aiMsg.setLang("zh");
                aiMsg.setCreatedAt(Instant.now());
                messageRepository.save(aiMsg);

                return ResponseEntity.ok(reply);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "请求处理失败: " + e.getMessage(), e);
            return ResponseEntity.status(500).body("请求处理失败: " + e.getMessage());
        }
    }



    // 使用消息映射的方式，接收前端的消息并进行流式响应
    @MessageMapping("/chat/message")
    @CrossOrigin(origins = "http://localhost:4000")
    public void streamMessage(String messageContent, @RequestBody Map<String, String> request) {
        // 解析消息
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(messageContent);
        } catch (Exception e) {

            return;
        }

        String content = jsonNode.get("content").asText();
        Long conversationId = jsonNode.has("conversationId") ? jsonNode.get("conversationId").asLong() : null;

        // 发送开始消息
        Map<String, String> startMsg = new HashMap<>();
        startMsg.put("type", "start");
        messagingTemplate.convertAndSend("/topic/messages", startMsg);

        // 模拟流式响应
        String[] parts = content.split(" ");
        for (String part : parts) {
            try {
                Thread.sleep(300); // 模拟延迟

                Map<String, String> chunkMsg = new HashMap<>();
                chunkMsg.put("type", "chunk");
                chunkMsg.put("content", part + " ");
                messagingTemplate.convertAndSend("/topic/messages", chunkMsg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 发送结束消息
        Map<String, String> endMsg = new HashMap<>();
        endMsg.put("type", "end");
        messagingTemplate.convertAndSend("/topic/messages", endMsg);
    }
    private String[] generateStreamedResponse(String messageContent) {
        return messageContent.split(" "); // 按空格拆分成多个小段来模拟流式回答
    }





    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private BaiduImageRecognitionService baiduImageRecognitionService;

    // 修改stream方法
    @PostMapping(value = "/stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin(origins = "http://localhost:4000", allowedHeaders = "*", methods = {RequestMethod.POST})
    public SseEmitter streamResponse(
            @RequestParam Long userId,
            @RequestParam("messages") String messagesJson,
            @RequestParam(value = "newConversation", defaultValue = "false") boolean newConversation,
            @RequestParam(value = "conversationId", required = false) Long conversationId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        SseEmitter emitter = new SseEmitter(600_000L);
        StringBuilder fullResponse = new StringBuilder();

        try {
            // 解析消息
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> messages = mapper.readValue(messagesJson, new TypeReference<>() {});

            // 处理文件上传
            if (file != null && !file.isEmpty()) {
                String fileContent;
                if (file.getContentType().startsWith("text/")) {
                    fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                } else if (file.getContentType().startsWith("image/")) {
                    // 调用百度图像识别
                    String recognitionResult = baiduImageRecognitionService.recognizeGeneralImage(file);
                    fileContent = "图片识别结果:\n" + recognitionResult +
                            "\n文件名：" + file.getOriginalFilename() +
                            "，大小：" + file.getSize() + "字节";
                } else {
                    fileContent = "[文件] 文件名：" + file.getOriginalFilename() +
                            "，类型：" + file.getContentType();
                }

                Map<String, String> fileMessage = new HashMap<>();
                fileMessage.put("role", "user");
                fileMessage.put("content", "上传文件内容：\n" + fileContent);
                messages.add(fileMessage);
            }

            // 保存用户消息并获取/创建对话
            Conversation conversation = saveUserMessage(userId, messages, conversationId, newConversation);

            // 异步处理AI回复
            executorService.submit(() -> {
                try {
                    // 准备请求到AI模型
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(API_KEY);


                    Map<String, Object> payload = new HashMap<>();
                    payload.put("model", "DeepSeek-R1");
                    payload.put("messages", messages);


                    List<Map<String, String>> formattedMessages = new ArrayList<>();
                    formattedMessages.addAll(messages);


                    Map<String, String> systemMessage = new HashMap<>();
                    systemMessage.put("role", "system");
                    systemMessage.put("content", "请按照以下格式回答：首先是一段总体介绍，然后使用【】标注关键点，格式为【关键点名称】: 具体内容。");
                    formattedMessages.add(systemMessage);

                    payload.put("messages", formattedMessages);
                    payload.put("temperature", 0.7);
                    payload.put("stream", true);


                    // 使用WebClient调用AI API并处理流式响应
                    webClient.post()
                            .bodyValue(payload)
                            .retrieve()
                            .bodyToFlux(String.class)
                            .subscribe(
                                    chunk -> {
                                        try {
                                            if (chunk != null && !chunk.trim().isEmpty()) {
                                                if (chunk.startsWith("data: ")) {
                                                    chunk = chunk.substring(6);
                                                }
                                                if ("[DONE]".equals(chunk.trim())) {
                                                    return;
                                                }
                                                ObjectMapper mapper1 = new ObjectMapper();
                                                JsonNode jsonNode = mapper1.readTree(chunk);
                                                JsonNode choicesNode = jsonNode.get("choices");
                                                if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
                                                    JsonNode deltaNode = choicesNode.get(0).get("delta");
                                                    if (deltaNode != null) {
                                                        JsonNode contentNode = deltaNode.get("content");
                                                        if (contentNode != null && !contentNode.isNull()) {
                                                            String content = contentNode.asText();
                                                            if (content.matches("[a-zA-Z]+") && !content.endsWith(" ")) {
                                                                content += " ";
                                                            }
                                                            fullResponse.append(content);
                                                            emitter.send(SseEmitter.event()
                                                                    .data(content));
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (IOException e) {
                                            if (fullResponse.length() > 0) {
                                                saveAiMessage(conversation, fullResponse.toString());
                                            }
                                            emitter.completeWithError(e);
                                        }
                                    },
                                    error -> {
                                        logger.log(Level.SEVERE, "流式响应出错: " + error.getMessage(), error);
                                        if (fullResponse.length() > 0) {
                                            saveAiMessage(conversation, fullResponse.toString());
                                        }
                                        emitter.completeWithError(error);
                                    },
                                    () -> {
                                        saveAiMessage(conversation, fullResponse.toString());
                                        emitter.complete();
                                    }
                            );
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "处理流式请求失败: " + e.getMessage(), e);
                    emitter.completeWithError(e);
                }
            });

        } catch (Exception e) {
            logger.log(Level.SEVERE, "初始化流式请求失败: " + e.getMessage(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }
    // 修改后的辅助方法：保存用户消息并返回会话
    private Conversation saveUserMessage(Long userId,List<Map<String, String>> messages, Long conversationId, boolean newConversation) {
        Instant now = Instant.now();
        Conversation conversation;

        String username = "pyaaa";

        if (newConversation || conversationId == null) {
            // 创建新会话
            User user = userRepository.findById(userId)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUserId(userId);
                        newUser.setUsername(username);
                        return userRepository.save(newUser);
                    });

            String title = messages.get(messages.size() - 1).get("content");
            title = title.length() > 10 ? title.substring(0, 10) : title;

            conversation = new Conversation();
            conversation.setUser(user);
            conversation.setTitle(title);
            conversation.setCreatedAt(now);
            conversation.setUpdatedAt(now);
            conversation = conversationRepository.save(conversation);
        } else {
            // 获取现有会话
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("会话不存在"));
            conversation.setUpdatedAt(now);
            conversation = conversationRepository.save(conversation);
        }

        // 保存用户消息
        Map<String, String> userMsgMap = messages.get(messages.size() - 1);
        Message userMsg = new Message();
        userMsg.setConv(conversation);
        userMsg.setRole("user");
        userMsg.setContent(userMsgMap.get("content"));
        userMsg.setContentType("text");
        userMsg.setLang("zh");
        userMsg.setCreatedAt(Instant.now());
        messageRepository.save(userMsg);

        return conversation;
    }

    // 辅助方法：保存用户消息并返回会话
    private Conversation saveUserMessage(List<Map<String, String>> messages, Long conversationId) {
        Instant now = Instant.now();
        Conversation conversation;

        if (conversationId == null) {
            // 创建新会话
            User user = userRepository.findById(100000005L)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            String title = messages.get(messages.size() - 1).get("content");
            title = title.length() > 10 ? title.substring(0, 10) : title;

            conversation = new Conversation();
            conversation.setUser(user);
            conversation.setTitle(title);
            conversation.setCreatedAt(now);
            conversation.setUpdatedAt(now);
            conversation = conversationRepository.save(conversation);
        } else {
            // 获取现有会话
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("会话不存在"));
            conversation.setUpdatedAt(now);
            conversation = conversationRepository.save(conversation);
        }

        // 保存用户消息
        Map<String, String> userMsgMap = messages.get(messages.size() - 1);
        Message userMsg = new Message();
        userMsg.setConv(conversation);
        userMsg.setRole("user");
        userMsg.setContent(userMsgMap.get("content"));
        userMsg.setContentType("text");
        userMsg.setLang("zh");
        userMsg.setCreatedAt(Instant.now());
        messageRepository.save(userMsg);

        return conversation;
    }


    // 辅助方法：保存AI消息
    private void saveAiMessage(Conversation conversation, String content) {
        Message aiMsg = new Message();
        aiMsg.setConv(conversation);
        aiMsg.setRole("assistant");
        aiMsg.setContent(content);
        aiMsg.setContentType("text");
        aiMsg.setLang("zh");
        aiMsg.setCreatedAt(Instant.now());
        messageRepository.save(aiMsg);
    }

    //删除对话
    @CrossOrigin(origins = "http://localhost:4000")
    @DeleteMapping("/conversations/{id}")
    @Transactional
    public ResponseEntity<?> deleteConversation(@PathVariable Long id) {
        try {
            conversationService.deleteConversationWithMessages(id);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败");
        }
    }


    //上传附件
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "msgId", required = false) Long msgId
            , @RequestBody Map<String, String> request) {
        try {
            // 创建临时目录
            String tempDir = System.getProperty("java.io.tmpdir") + "/chat_uploads/";
            File tempDirFile = new File(tempDir);
            if (!tempDirFile.exists()) {
                tempDirFile.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueName = UUID.randomUUID().toString() + fileExtension;
            String filePath = tempDir + uniqueName;

            // 保存文件到临时目录
            File dest = new File(filePath);
            file.transferTo(dest);

            // 返回文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("fileUrl", filePath);
            result.put("fileName", originalFilename);
            result.put("fileType", file.getContentType());
            result.put("fileSize", file.getSize());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("上传失败: " + e.getMessage());
        }
    }



}
