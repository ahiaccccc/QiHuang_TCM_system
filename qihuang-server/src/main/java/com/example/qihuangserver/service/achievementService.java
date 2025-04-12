package com.example.qihuangserver.service;

import com.example.qihuangserver.model.achievement;
import com.example.qihuangserver.repository.achievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class achievementService {

    @Autowired
    private achievementRepository achievementRepository;


    public Optional<achievement> findById(Integer id) {
        return achievementRepository.findById(id);
    }

    public achievement save(achievement achievement) {
        return achievementRepository.save(achievement);
    }

    public void deleteById(Integer id) {
        achievementRepository.deleteById(id);
    }

}
