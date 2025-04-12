package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.rankRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface rankRecordRepository extends JpaRepository<rankRecord, Integer> {
    Optional<rankRecord> findById(Integer id);

    rankRecord save(rankRecord rankRecord);

    void deleteById(Integer id);

}
