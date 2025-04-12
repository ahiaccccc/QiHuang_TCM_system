package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.answerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface answerRecordRepository extends JpaRepository<answerRecord, Integer> {
    Optional<answerRecord> findById(Integer id);

    void deleteById(Integer id);

    answerRecord save(answerRecord answerRecord);

}
