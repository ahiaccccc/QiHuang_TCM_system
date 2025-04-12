package com.example.qihuangserver.service;

import com.example.qihuangserver.model.rankRecord;
import com.example.qihuangserver.repository.rankRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class rankRecordService {

    @Autowired
    private rankRecordRepository rankRecordRepository;


    public Optional<rankRecord> findById(Integer id) {
        return rankRecordRepository.findById(id);
    }

    public rankRecord save(rankRecord rankRecord) {
        return rankRecordRepository.save(rankRecord);
    }

    public void deleteById(Integer id) {
        rankRecordRepository.deleteById(id);
    }

}
