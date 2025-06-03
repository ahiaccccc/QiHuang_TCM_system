package com.example.qihuangserver.service;

import com.example.qihuangserver.model.QuestionClass;
import com.example.qihuangserver.repository.QuestionClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionClassService {

    @Autowired
    private QuestionClassRepository questionClassRepository;

    // 根据ID查询题目分类
    public Optional<QuestionClass> findById(Integer id) {
        return questionClassRepository.findById(id);
    }

    // 查询所有题目分类
    public List<QuestionClass> findAll() {
        return questionClassRepository.findAll();
    }

    // 保存题目分类（创建或更新）
    public QuestionClass save(QuestionClass questionClass) {
        return questionClassRepository.save(questionClass);
    }

    // 根据ID删除题目分类
    public void deleteById(Integer id) {
        questionClassRepository.deleteById(id);
    }
}