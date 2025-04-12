package com.example.qihuangserver.service;

import com.example.qihuangserver.model.pushRecord;
import com.example.qihuangserver.repository.pushRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class pushRecordService {

    @Autowired
    private pushRecordRepository pushRecordRepository;


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
