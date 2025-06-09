package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.PushRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PushRecordRepository extends JpaRepository<PushRecord, Integer> {
    Optional<PushRecord> findById(Integer id);

    PushRecord save(PushRecord pushRecord);

    void deleteById(Integer id);

    PushRecord findByUserIdAndPushDate(Long userId, LocalDate date);



}
