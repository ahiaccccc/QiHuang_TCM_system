package com.example.qihuangserver.service;

import com.example.qihuangserver.model.PushRecord;
import com.example.qihuangserver.repository.PushRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Service
public class PushRecordService {

    @Autowired
    private PushRecordRepository pushRecordRepository;


    public Optional<PushRecord> findById(Integer id) {
        return pushRecordRepository.findById(id);
    }

    public PushRecord save(PushRecord pushRecord) {
        return pushRecordRepository.save(pushRecord);
    }

    public void deleteById(Integer id) {
        pushRecordRepository.deleteById(id);
    }

    public PushRecord isPushed(Long userId, LocalDate date) {
        return pushRecordRepository.findByUserIdAndPushDate(userId , date);
    }

    public Optional<PushRecord> findByUserIdAndPushDate(Long userId, LocalDate date) {
        return Optional.ofNullable(pushRecordRepository.findByUserIdAndPushDate(userId, date));
    }
}
