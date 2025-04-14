package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Collected;
import com.example.qihuangserver.repository.CollectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollectedService {

    @Autowired
    private CollectedRepository collectedRepository;

    public List<Collected> findAll() {
        return collectedRepository.findAll();
    }

    public Optional<Collected> findById(Long id) {
        return collectedRepository.findById(id);
    }

    public Collected save(Collected collected) {
        return collectedRepository.save(collected);
    }

    public void deleteById(Long id) {
        collectedRepository.deleteById(id);
    }

    public void collect(Long classicId) {
    }
}