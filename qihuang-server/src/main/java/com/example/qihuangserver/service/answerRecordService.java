package com.example.qihuangserver.service;

import com.example.qihuangserver.model.answerRecord;
import com.example.qihuangserver.repository.answerRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class answerRecordService {

    @Autowired
    private answerRecordRepository answerRecordRepository;

    public Optional<answerRecord> findById(Integer id) {
        return answerRecordRepository.findById(id);
    }

    public answerRecord save(answerRecord answerRecord) {return answerRecordRepository.save(answerRecord);}

    public void deleteById(Integer id) {
        answerRecordRepository.deleteById(id);
    }

}