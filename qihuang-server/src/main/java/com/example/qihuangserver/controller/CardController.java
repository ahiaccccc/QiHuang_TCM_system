package com.example.qihuangserver.controller;

import com.example.qihuangserver.exception.UserNotFoundException;
import com.example.qihuangserver.exception.myException;
import com.example.qihuangserver.model.CardVO;
import com.example.qihuangserver.model.Herb;
import com.example.qihuangserver.model.PushRecord;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.UserRepository;
import com.example.qihuangserver.service.HerbService;
import com.example.qihuangserver.service.PushRecordService;
import com.example.qihuangserver.util.JWTUtils;
import com.example.qihuangserver.util.SimpleTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class CardController {

    @Resource
    private HerbService herbService;
    @Resource
    private PushRecordService pushRecordService;
    @Resource
    private UserRepository userRepository;

    // 大模型API配置
    private static final String API_URL = "http://10.2.8.77:3000/v1/chat/completions";
    private static final String API_KEY = "sk-93nWYhI8SrnXad5m9932CeBdDeDf4233B21d93D217095f22";
    private static final String MODEL_NAME = "DeepSeek-R1";

    @GetMapping("/api/card/get")
    public CardVO getCard(@RequestHeader("Authorization") String authHeader) throws myException {
        String token = authHeader.replace("Bearer ", "");
        long userId = JWTUtils.getUserIdFromJwtToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("用户不存在"));

        if (userId == 0L) {
            return new CardVO();
        }

        CardVO cardVO = new CardVO();
        LocalDate currentDate = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();

        PushRecord pushRecord = pushRecordService.isPushed(userId, currentDate);

        if (pushRecord == null) {
            Optional<Herb> herbOptional = herbService.getRandomHerb();
            if (herbOptional.isEmpty()) {
                throw new myException("没有找到随机草药");
            }

            Herb herb = herbOptional.get();
            cardVO.img = herb.getImageUrl();
            String effect = herb.getEffect();
            String usage = herb.getUsage();
            String description = effect + usage;
            cardVO.describe = description;
            cardVO.name = herb.getName();

            // 调用大模型生成祝福语
            String blessing = generateBlessing(herb.getName());
            cardVO.word = blessing;

            PushRecord newPushRecord = new PushRecord();
            newPushRecord.setUserId(userId);
            newPushRecord.setPushDate(currentDate);
            newPushRecord.setBlessing(blessing);
            newPushRecord.setHerbId(herb.getId());
            newPushRecord.setCreatedTime(currentDateTime);

            //保存前最后检查一次是不是已经有了
            Optional<PushRecord> existingRecord = pushRecordService.findByUserIdAndPushDate(userId, currentDate);
            if (existingRecord.isPresent()) {
                // 如果已经存在，直接返回已经保存的
                Herb existingHerb = herbService.findById(existingRecord.get().getHerbId())
                        .orElseThrow(() -> new myException("草药不存在"));
                cardVO.img = existingHerb.getImageUrl();
                cardVO.describe = existingHerb.getEffect() + existingHerb.getUsage();
                cardVO.name = existingHerb.getName();
                cardVO.word = existingRecord.get().getBlessing();
                return cardVO;
            }
            pushRecordService.save(newPushRecord);
            return cardVO;
        } else {
            String HerbId = pushRecord.getHerbId();
            Optional<Herb> herbOptional = herbService.findById(HerbId);

            Herb herb = herbOptional.get();
            cardVO.img = herb.getImageUrl();
            String effect = herb.getEffect();
            String usage = herb.getUsage();
            String description = effect + usage;
            cardVO.describe = description;
            cardVO.name = herb.getName();
            cardVO.word = pushRecord.getBlessing();
            return cardVO;
        }
    }

    // 调用大模型生成祝福语
    private String generateBlessing(String herbName) throws myException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // 强化版提示词（增加XML标签禁止指令）
            String prompt = "[指令]\n"
                    + "1. 你是一位中医祝福语专家\n"
                    + "2. 为中药「" + herbName + "」创作祝福语\n"
                    + "3. 严格禁止输出任何标签（包括但不限于<think>...</think>）\n"
                    + "4. 仅输出最终祝福语，不要解释、思考过程或示例\n"
                    + "5. 30字以内，体现中药特性\n\n"
                    + "格式要求：\n"
                    + "直接输出如：\"愿您如" + herbName + "般[特性]，[祝福]\"\n"
                    + "违规示例（不要这样写）：\n"
                    + "<think>这是思考过程</think>\n"
                    + "正确示例：\n"
                    + "愿黄芪补气固表，正气内存邪不可干";

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(message);

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", MODEL_NAME);
            payload.put("messages", messages);
            payload.put("temperature", 0.3); // 降低随机性

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(response.getBody());
                String reply = json.get("choices").get(0).get("message").get("content").asText();

                // 强化清理逻辑（多步处理）
                // 安全的正则处理（修正$符号转义）
                String cleaned = reply
                        .replaceAll("(?s)<think>.*?</think>", "")  // 多行标签
                        .replaceAll("<[^>]+>", "")                 // 所有HTML标签
                        .replaceAll("\\$.*?\\$", "")              // 匹配 $...$ 内容（正确转义）
                        .replaceAll("\\\\\\$.*?\\\\\\$", "")      // 匹配 \$...\$ 内容（如果需要）
                        .replaceAll("\\s+", " ")                  // 合并空格
                        .trim();

                // 提取第一句祝福语（应对模型可能的多段输出）
                String[] sentences = cleaned.split("[.!?。！？]");
                String blessing = sentences[0].trim();

                // 最终校验
                if (blessing.isEmpty()) {
                    return "愿" + herbName + "为您带来健康平安"; // 默认祝福语
                }
                return blessing.length() > 100 ?
                        blessing.substring(0, 100) :
                        blessing;
            } else {
                throw new myException("生成祝福语失败: " + response.getStatusCode());
            }
        } catch (Exception e) {
            return "愿" + herbName + "护佑您安康"; // 异常时返回简单祝福
        }
    }
}