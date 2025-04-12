package com.example.qihuangserver.service;

import com.example.qihuangserver.model.questionBank;
import com.example.qihuangserver.repository.questionBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class questionBankService {

    @Autowired
    private questionBankRepository questionBankRepository;


    public Optional<questionBank> findById(Integer id) {
        return questionBankRepository.findById(id);
    }

    public questionBank save(questionBank questionBank) {
        return questionBankRepository.save(questionBank);
    }

    public void deleteById(Integer id) {
        questionBankRepository.deleteById(id);
    }

}
