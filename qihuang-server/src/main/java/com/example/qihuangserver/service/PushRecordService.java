package com.example.qihuangserver.service;

import com.example.qihuangserver.model.pushRecord;
import com.example.qihuangserver.repository.PushRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PushRecordService {

    @Autowired
    private PushRecordRepository pushRecordRepository;


    public Optional<pushRecord> findById(Integer id) {
        return pushRecordRepository.findById(id);
    }

    public pushRecord save(pushRecord pushRecord) {
        return pushRecordRepository.save(pushRecord);
    }

    public void deleteById(Integer id) {
        pushRecordRepository.deleteById(id);
    }

}
