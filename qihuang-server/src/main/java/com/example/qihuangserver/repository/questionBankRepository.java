package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.questionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface questionBankRepository extends JpaRepository<questionBank, Integer> {
    Optional<questionBank> findById(Integer id);

    questionBank save(questionBank questionBank);

    void deleteById(Integer id);

}
