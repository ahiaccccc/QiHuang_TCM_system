package com.example.qihuangserver.service;

import com.example.qihuangserver.model.RankRecord;
import com.example.qihuangserver.repository.RankRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class rankRecordService {

    @Autowired
    private RankRecordRepository rankRecordRepository;


    public Optional<RankRecord> findById(Integer id) {
        return rankRecordRepository.findById(id);
    }

    public RankRecord save(RankRecord rankRecord) {
        return rankRecordRepository.save(rankRecord);
    }

    public void deleteById(Integer id) {
        rankRecordRepository.deleteById(id);
    }

}
