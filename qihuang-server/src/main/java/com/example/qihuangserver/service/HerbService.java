package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Herb;
import com.example.qihuangserver.repository.HerbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HerbService {

    @Autowired
    private HerbRepository herbRepository;

    public Optional<Herb> findById(Integer id) {
        return herbRepository.findById(id);
    }

    public Herb save(Herb herb) {
        return herbRepository.save(herb);
    }

    public void deleteById(Integer id) {
        herbRepository.deleteById(id);
    }

}
