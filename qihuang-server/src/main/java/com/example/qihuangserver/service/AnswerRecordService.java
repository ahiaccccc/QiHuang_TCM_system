package com.example.qihuangserver.service;

import com.example.qihuangserver.dto.question.AnswerRecordDTO;
import com.example.qihuangserver.dto.question.QuestionBankDTO;
import com.example.qihuangserver.model.AnswerRecord;
import com.example.qihuangserver.model.PlayMode;
import com.example.qihuangserver.repository.AnswerRecordRepository;
import com.example.qihuangserver.repository.QuestionBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerRecordService {

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

    public List<AnswerRecord>findActivateRecordByUserId(Integer userId){return answerRecordRepository.findByUserIdAndIsActivate(userId,1); }

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

    public AnswerRecordDTO getAnswerRecordDTO(Integer userId, Integer classId, Integer limit, PlayMode playMode, List<QuestionBankDTO> questions, String answerStr, LocalDateTime time) {
        return AnswerRecordDTO.builder()
                .userId(userId)
                .classId(classId)
                .number(limit)
                .playMode(playMode)
                .questions(questions)
                .answers(answerStr)
                .time(time)
                .build();
    }

//    public AnswerRecord getAnswerRecordDTOByRecord(AnswerRecord answerRecord){
//        List<QuestionBankDTO> questions = questionBankService.getQuestionByQuestionStr(answerRecord.getQuestions());
//    }

}