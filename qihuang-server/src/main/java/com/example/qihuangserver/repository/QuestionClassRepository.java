package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.QuestionClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionClassRepository extends JpaRepository<QuestionClass, Integer> {
    Optional<QuestionClass> findById(Integer id);
    //查找所有题目分类
    List<QuestionClass> findAll();

}
