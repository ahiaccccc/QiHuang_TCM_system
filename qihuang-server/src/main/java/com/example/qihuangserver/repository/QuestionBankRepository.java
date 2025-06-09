package com.example.qihuangserver.repository;

import com.example.qihuangserver.dto.question.QuestionBankDTO;
import com.example.qihuangserver.model.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Integer> {
    Optional<QuestionBank> findById(Integer id);

    QuestionBank save(QuestionBank questionBank);

    void deleteById(Integer id);

    List<QuestionBank> findAll();

    List<QuestionBank> findByClassId(Integer classId);

    @Query("SELECT NEW com.example.qihuangserver.dto.question.QuestionBankDTO(q.id, q.question, q.options) " +
            "FROM QuestionBank q " +
            "WHERE q.classId = :classId " +
            "ORDER BY RAND() " +
            "LIMIT :limit")
    List<QuestionBankDTO> findRandomByClassId(Integer classId, Integer limit);

    @Query("SELECT NEW com.example.qihuangserver.dto.question.QuestionBankDTO(q.id, q.question, q.options) " +
            "FROM QuestionBank q " +
            "WHERE q.id IN :ids")
    List<QuestionBankDTO> findByIds(List<Integer> ids);



}
