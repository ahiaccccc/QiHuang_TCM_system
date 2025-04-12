package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.pushRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface pushRecordRepository extends JpaRepository<pushRecord, Integer> {
    Optional<pushRecord> findById(Integer id);

    pushRecord save(pushRecord pushRecord);

    void deleteById(Integer id);

}
