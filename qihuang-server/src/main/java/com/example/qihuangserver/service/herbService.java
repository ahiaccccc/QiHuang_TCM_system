package com.example.qihuangserver.service;

import com.example.qihuangserver.model.herb;
import com.example.qihuangserver.repository.herbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class herbService {

    @Autowired
    private herbRepository herbRepository;

    public Optional<herb> findById(Integer id) {
        return herbRepository.findById(id);
    }

    public herb save(herb herb) {
        return herbRepository.save(herb);
    }

    public void deleteById(Integer id) {
        herbRepository.deleteById(id);
    }

}
