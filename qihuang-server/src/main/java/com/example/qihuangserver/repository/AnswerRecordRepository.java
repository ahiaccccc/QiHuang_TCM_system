package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.AnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Integer> {
    Optional<AnswerRecord> findById(Integer id);

    void deleteById(Integer id);

    AnswerRecord save(AnswerRecord answerRecord);

    @Query(value = "SELECT * FROM answer_record WHERE user_id = ?1 AND activating = ?2", nativeQuery = true)
    List<AnswerRecord> findByUserIdAndIsActivate(Integer userId, Integer isActivate);


    List<AnswerRecord> findByUserId(Integer userId);

}
