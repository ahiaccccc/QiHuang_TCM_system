package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.RankRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankRecordRepository extends JpaRepository<RankRecord, Integer> {
    Optional<RankRecord> findById(Integer id);

    RankRecord save(RankRecord rankRecord);

    void deleteById(Integer id);

}
