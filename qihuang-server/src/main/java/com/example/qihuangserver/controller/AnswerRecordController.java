package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.ApiResponse;
import com.example.qihuangserver.dto.question.AnswerRecordDTO;
import com.example.qihuangserver.dto.question.QuestionBankDTO;
import com.example.qihuangserver.model.AnswerRecord;
import com.example.qihuangserver.model.PlayMode;
import com.example.qihuangserver.model.QuestionBank;
import com.example.qihuangserver.service.AnswerRecordService;
import com.example.qihuangserver.service.QuestionBankService;
import com.example.qihuangserver.utils.DataRequest;
import com.example.qihuangserver.utils.Result;
import com.example.qihuangserver.util.SimpleTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/answerRecord")
@CrossOrigin(origins = "*")
public class AnswerRecordController {
    @Resource
    private AnswerRecordService answerRecordService;

    @Resource
    private QuestionBankService questionBankService;

    public AnswerRecordDTO getAnswerRecordDTOByRecord(AnswerRecord answerRecord){
        AnswerRecordDTO answerRecordDTO = answerRecord.getDTOExceptQuestions();
        List<QuestionBankDTO> questionBankDTOS = questionBankService.getQuestionByQuestionStr(answerRecord.getQuestions());
        answerRecordDTO.setQuestions(questionBankDTOS);
        return answerRecordDTO;
    }

    @PostMapping("startRecord")
    public ResponseEntity<AnswerRecordDTO> startRecord(@RequestBody DataRequest dataRequest, @RequestHeader("Authorization") String authHeader) {
        // 从SimpleTokenUtil验证token
        String token = authHeader.replace("Bearer ", "");
        Integer userId = Math.toIntExact(SimpleTokenUtil.validateToken(token));

        if (userId == 0L) { // 假设无效token返回0
            return ResponseEntity.status(401).body(new AnswerRecordDTO());
        }

        Integer classId = dataRequest.getInteger("classId");
        Integer limit = dataRequest.getInteger("limit");
        PlayMode playMode = PlayMode.valueOf(dataRequest.getString("playMode"));
        List<QuestionBankDTO> questions = questionBankService.findRandomByClassId(classId, limit);
        //从questions中获取所有的问题id，并以','分隔开，拼接成一个字符串questionStr
        String questionStr = questions.stream()
                .map(QuestionBankDTO::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        //答案先生成limit个*成str
        String answerStr = "*".repeat(limit);
        AnswerRecord answerRecord = answerRecordService.startAnswerRecord(userId,classId,limit,playMode,questionStr,answerStr);
        LocalDateTime answerRecordTime = answerRecord.getTime();
        AnswerRecordDTO answerRecordDTO = answerRecordService.getAnswerRecordDTO(userId,classId,limit,playMode,questions,answerStr,answerRecordTime);
        return ResponseEntity.ok(answerRecordDTO);

    }

    /**
     * 初始化答题记录时调用的接口，此时检查是否有正在答的题目，有则返回其DTO，否则返回历史记录DTO
     * @param authHeader
     * @return
     */
    @PostMapping("initRecord")
    public ResponseEntity<List<AnswerRecordDTO>> initRecord(@RequestHeader("Authorization") String authHeader){
        // 从SimpleTokenUtil验证token
        String token = authHeader.replace("Bearer ", "");
        Integer userId = Math.toIntExact(SimpleTokenUtil.validateToken(token));

        if (userId == 0L) { // 假设无效token返回0
            return ResponseEntity.status(401).body(new ArrayList<>());
        }

        List<AnswerRecord> activateAnswerRecord = answerRecordService.findActivateRecordByUserId(userId);
        if(activateAnswerRecord.isEmpty()){
            List<AnswerRecord> historyAnswerRecord = answerRecordService.findHistoryRecordByUserId(userId);
            List<AnswerRecordDTO> answerRecordDTOS = new ArrayList<>();
            for(AnswerRecord answerRecord : historyAnswerRecord){
                AnswerRecordDTO answerRecordDTO = getAnswerRecordDTOByRecord(answerRecord);
                answerRecordDTOS.add(answerRecordDTO);
            }
            return ResponseEntity.ok(answerRecordDTOS);
        }
        else {
            List<AnswerRecordDTO> answerRecordDTOS = new ArrayList<>();
            for(AnswerRecord answerRecord : activateAnswerRecord){
                AnswerRecordDTO answerRecordDTO = getAnswerRecordDTOByRecord(answerRecord);
                answerRecordDTOS.add(answerRecordDTO);
            }
            return ResponseEntity.ok(answerRecordDTOS);

        }
    }

//    //批改某次答题情况，将正确题数存入correct属性
    @PostMapping("finishRecord")
    public Result finishRecord(@RequestBody DataRequest dataRequest) {
        try {
            AnswerRecord record = answerRecordService.finishAnswerRecord(dataRequest);
            // 返回DTO或原始对象均可
            return Result.success(record.getDTOExceptQuestions(), "批改完成");
        } catch (Exception e) {
        return Result.error("批改失败：" + e.getMessage());
    }
}

}
