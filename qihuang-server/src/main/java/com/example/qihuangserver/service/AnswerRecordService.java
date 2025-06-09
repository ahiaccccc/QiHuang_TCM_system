package com.example.qihuangserver.service;

import com.example.qihuangserver.dto.question.AnswerRecordDTO;
import com.example.qihuangserver.dto.question.QuestionBankDTO;
import com.example.qihuangserver.model.AnswerRecord;
import com.example.qihuangserver.model.PlayMode;
import com.example.qihuangserver.model.QuestionBank;
import com.example.qihuangserver.repository.AnswerRecordRepository;
import com.example.qihuangserver.repository.QuestionBankRepository;
import com.example.qihuangserver.utils.DataRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AnswerRecordService {

    private static final Logger log = LoggerFactory.getLogger(AnswerRecordService.class); // 日志对象

    @Autowired
    private AnswerRecordRepository answerRecordRepository;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    public Optional<AnswerRecord> findById(Integer id) {
        return answerRecordRepository.findById(id);
    }

    public AnswerRecord save(AnswerRecord answerRecord) {return answerRecordRepository.save(answerRecord);}

    public void deleteById(Integer id) {
        answerRecordRepository.deleteById(id);
    }

//    public List<AnswerRecord>findActivateRecordByUserId(Integer userId){
//        return answerRecordRepository.findByUserIdAndIsActivate(userId,1);
//    }

    public List<AnswerRecord> findActivateRecordByUserId(Integer userId) {
        List<AnswerRecord> result = null;
        try {
            // 校验参数非空（可选，根据需求添加）
            if (Objects.isNull(userId)) {
                log.error("用户 ID 为空，无法查询激活记录");
                throw new IllegalArgumentException("用户 ID 不能为空");
            }

            // 执行查询
            result = answerRecordRepository.findByUserIdAndIsActivate(userId, 1);
            log.debug("查询到 {} 条激活记录，用户 ID：{}", result != null ? result.size() : 0, userId);
            return result;

        } catch (Exception ex) { // 捕获所有异常（建议根据实际情况细化异常类型）
            // 记录完整的错误堆栈
            log.error("查询激活记录时发生异常，用户 ID：{}", userId, ex);

            // 可选：抛出自定义异常或包装异常返回给前端
            throw new RuntimeException("查询激活记录失败，请联系管理员", ex); // 或返回特定错误码
        }
    }
    public List<AnswerRecord>findHistoryRecordByUserId(Integer userId){return answerRecordRepository.findByUserIdAndIsActivate(userId,0); }

    public AnswerRecord startAnswerRecord(Integer userId, Integer classId, Integer limit, PlayMode playMode, String questionStr,String answerStr) {
        LocalDateTime now = LocalDateTime.now();
        AnswerRecord answerRecord = AnswerRecord.builder()
                .userId(userId)
                .classId(classId)
                .number(limit)
                .playMode(playMode)
                .questions(questionStr)
                .answers(answerStr)
                .activating(1)
                .time(now)
                .build();
        answerRecordRepository.save(answerRecord);
        return answerRecord;
    }

    public AnswerRecordDTO getAnswerRecordDTO(Integer id,Integer userId, Integer classId, Integer limit, PlayMode playMode, List<QuestionBankDTO> questions, String answerStr, LocalDateTime time) {
        return AnswerRecordDTO.builder()
                .id(id)
                .userId(userId)
                .classId(classId)
                .number(limit)
                .playMode(playMode)
                .questions(questions)
                .answers(answerStr)
                .time(time)
                .build();
    }

    public AnswerRecord finishAnswerRecord(DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("answerRecordId");
        String answers = dataRequest.getString("answers");

        Optional<AnswerRecord> optional = answerRecordRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("未找到答题记录");
        }

        AnswerRecord record = optional.get();
        String questions = record.getQuestions();
        String[] questionIds = questions.split(",");
        StringBuilder correctBuilder = new StringBuilder();

        for (int i = 0; i < questionIds.length; i++) {
            Integer qid = Integer.parseInt(questionIds[i].trim());
            String rightAnswer = questionBankRepository.findById(qid)
                    .map(QuestionBank::getCorrectAnswer)
                    .orElse("");
            char userAnswer = (answers != null && i < answers.length()) ? answers.charAt(i) : ' ';
            // 比较答案
            if (String.valueOf(userAnswer).equalsIgnoreCase(rightAnswer)) {
                correctBuilder.append('1');
            } else {
                correctBuilder.append(rightAnswer);
            }
        }

        record.setActivating(0);
        record.setAnswers(answers);
        record.setCorrect(correctBuilder.toString());
        answerRecordRepository.save(record);
        return record;
    }

}